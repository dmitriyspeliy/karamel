package ru.kotomore.excursionapi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kotomore.excursionapi.dto.Lead;
import ru.kotomore.excursionapi.models.ContactResponse;
import ru.kotomore.excursionapi.models.LeadResponse;
import ru.kotomore.excursionapi.models.PipelineResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmoCRMService implements AmoCRMServiceUseCase {

    private final TokenServiceUseCase tokenServiceUseCase;
    @Value("${amocrm.access_token_link}")
    private String AMO_API_URL;
    private final String SCHOOL = "ШКОЛЬНЫЕ ГРУППЫ";
    private final String COLLECTIVE = "СБОРНЫЕ ГРУППЫ";

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenServiceUseCase.getTokenResponse().getAccessToken());
        return headers;
    }

    private <T> ResponseEntity<T> makeGetRequest(String url, Class<T> responseType) {
        final RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> requestEntity = new HttpEntity<>(createAuthHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
    }

    public List<Lead> getLeadsByPhone(String phone) {
        List<Integer> leadIds = getLeadIdsByPhone(phone);
        Map<Integer, String> pipelines = getPipelines();

        String url = AMO_API_URL + "/api/v4/leads?filter[id]" + Arrays.toString(leadIds.toArray());
        LeadResponse leadResponse = makeGetRequest(url, LeadResponse.class).getBody();

        assert leadResponse != null;

        List<Lead> leadList = new ArrayList<>();
        for (LeadResponse.Embedded.Lead lead : leadResponse.getEmbedded().getLeads()) {
            if (pipelines.containsKey(lead.getPipelineId())) {
                Lead leadDTO = new Lead();
                leadDTO.setEventType(pipelines.get(lead.getPipelineId()));
                for (LeadResponse.Embedded.Lead.CustomField customField : lead.getCustomFieldsValues()) {
                    mapCustomFieldToLeadDTO(leadDTO, customField);
                }
                leadList.add(leadDTO);
            }
        }
        return leadList;
    }

    public List<Integer> getLeadIdsByPhone(String phone) {
        String url = AMO_API_URL + "/api/v4/contacts?with=leads&query=" + phone;

        ContactResponse contactResponse = makeGetRequest(url, ContactResponse.class).getBody();

        assert contactResponse != null;
        return contactResponse
                .getEmbedded()
                .getContacts()
                .stream()
                .flatMap(contact -> contact.getEmbedded().getLeads().stream())
                .map(ContactResponse.Lead::getId)
                .collect(Collectors.toList());
    }

    public Map<Integer, String> getPipelines() {
        String url = AMO_API_URL + "/api/v4/leads/pipelines";

        PipelineResponse pipelineResponse = makeGetRequest(url, PipelineResponse.class).getBody();
        assert pipelineResponse != null;
        return pipelineResponse
                .get_embedded()
                .getPipelines()
                .stream()
                .filter(pipeline -> pipeline.getName().equals(SCHOOL)
                        || pipeline.getName().equals(COLLECTIVE))
                .collect(Collectors.toMap(
                        PipelineResponse.Pipeline::getId,
                        PipelineResponse.Pipeline::getName
                ));
    }

    private void mapCustomFieldToLeadDTO(Lead leadDTO, LeadResponse.Embedded.Lead.CustomField customField) {
        String fieldName = customField.getFieldName();
        String value = customField.getValues().get(0).getValue();

        switch (fieldName) {
            case "Адрес мероприятия" -> leadDTO.setAddress(Arrays.toString(customField.getValues()
                    .stream()
                    .map(LeadResponse.Embedded.Lead.CustomField.CustomFieldValue::getValue)
                    .toArray()));
            case "Предоплата" -> leadDTO.setPrepayment(Integer.parseInt(value));
            case "Цена билета" -> leadDTO.setTicketPrice(Integer.parseInt(value));
            case "Количество детей" -> leadDTO.setChildCount(Integer.parseInt(value));
            case "Кол-во взр. платно" -> leadDTO.setAdultsCount(Integer.parseInt(value));
            case "Бесплатный детский" -> leadDTO.setChildFreeCount(Integer.parseInt(value));
            case "Кол-во взр. беспл." -> leadDTO.setAdultsFreeCount(Integer.parseInt(value));
            case "Кол-во чел всего" -> leadDTO.setPeopleCount(Integer.parseInt(value));
            case "Возраст детей" -> leadDTO.setChildAge(Integer.parseInt(value));
            case "ДАТА ЭКСКУРСИИ" -> leadDTO.setDate(Long.parseLong(value));
        }
    }

}


