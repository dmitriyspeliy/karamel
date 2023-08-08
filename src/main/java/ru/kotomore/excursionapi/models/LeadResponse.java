package ru.kotomore.excursionapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LeadResponse {
    @JsonProperty("_embedded")
    private Embedded embedded;

    @Data
    public static class Embedded {
        @JsonProperty("leads")
        private List<Lead> leads;
        @JsonProperty("contacts")
        private List<String> contacts;


        @Data
        public static class Lead {
            private int id;
            private String name;
            private int price;
            @JsonProperty("responsible_user_id")
            private int responsibleUserId;
            @JsonProperty("status_id")
            private int statusId;
            @JsonProperty("pipeline_id")
            private int pipelineId;
            @JsonProperty("custom_fields_values")
            private List<CustomField> customFieldsValues;

            @Data
            public static class CustomField {
                @JsonProperty("field_id")
                private int fieldId;
                @JsonProperty("field_name")
                private String fieldName;
                @JsonProperty("field_type")
                private String fieldType;
                private List<CustomFieldValue> values;

                @Data
                public static class CustomFieldValue {
                    private String value;
                }
            }
        }
    }
}


