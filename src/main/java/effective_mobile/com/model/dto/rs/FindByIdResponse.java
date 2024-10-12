package effective_mobile.com.model.dto.rs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FindByIdResponse implements Serializable {

    @JsonProperty("result")
    private Result result;
    @JsonProperty("time")
    private Time time;

    @Getter
    @Setter
    public static class Time {
        private Double start;
        private Double finish;
        private Double duration;
        private Double processing;
        private Date date_start;
        private Date date_finish;
        private Integer operating_reset_at;
        private Integer operating;
    }

    @Setter
    @Getter
    public static class Result {
        @JsonProperty("ID")
        private String ID;
        @JsonProperty("POST")
        private Object pOST;
        @JsonProperty("COMMENTS")
        private String cOMMENTS;
        @JsonProperty("HONORIFIC")
        private Object hONORIFIC;
        @JsonProperty("NAME")
        private String nAME;
        @JsonProperty("SECOND_NAME")
        private Object sECOND_NAME;
        @JsonProperty("LAST_NAME")
        private Object lAST_NAME;
        @JsonProperty("PHOTO")
        private Object pHOTO;
        @JsonProperty("LEAD_ID")
        private Object lEAD_ID;
        @JsonProperty("TYPE_ID")
        private String tYPE_ID;
        @JsonProperty("SOURCE_ID")
        private Object sOURCE_ID;
        @JsonProperty("SOURCE_DESCRIPTION")
        private Object sOURCE_DESCRIPTION;
        @JsonProperty("COMPANY_ID")
        private Object cOMPANY_ID;
        @JsonProperty("BIRTHDATE")
        private String bIRTHDATE;
        @JsonProperty("EXPORT")
        private String eXPORT;
        @JsonProperty("HAS_PHONE")
        private String hAS_PHONE;
        @JsonProperty("HAS_EMAIL")
        private String hAS_EMAIL;
        @JsonProperty("HAS_IMOL")
        private String hAS_IMOL;
        @JsonProperty("DATE_CREATE")
        private Date dATE_CREATE;
        @JsonProperty("DATE_MODIFY")
        private Date dATE_MODIFY;
        @JsonProperty("ASSIGNED_BY_ID")
        private String aSSIGNED_BY_ID;
        @JsonProperty("CREATED_BY_ID")
        private String cREATED_BY_ID;
        @JsonProperty("MODIFY_BY_ID")
        private String mODIFY_BY_ID;
        @JsonProperty("OPENED")
        private String oPENED;
        @JsonProperty("ORIGINATOR_ID")
        private Object oRIGINATOR_ID;
        @JsonProperty("ORIGIN_ID")
        private Object oRIGIN_ID;
        @JsonProperty("ORIGIN_VERSION")
        private Object oRIGIN_VERSION;
        @JsonProperty("FACE_ID")
        private Object fACE_ID;
        @JsonProperty("LAST_ACTIVITY_TIME")
        private Date lAST_ACTIVITY_TIME;
        @JsonProperty("ADDRESS")
        private Object aDDRESS;
        @JsonProperty("ADDRESS_2")
        private Object aDDRESS_2;
        @JsonProperty("ADDRESS_CITY")
        private String aDDRESS_CITY;
        @JsonProperty("ADDRESS_POSTAL_CODE")
        private Object aDDRESS_POSTAL_CODE;
        @JsonProperty("ADDRESS_REGION")
        private Object aDDRESS_REGION;
        @JsonProperty("ADDRESS_PROVINCE")
        private Object aDDRESS_PROVINCE;
        @JsonProperty("ADDRESS_COUNTRY")
        private Object aDDRESS_COUNTRY;
        @JsonProperty("ADDRESS_LOC_ADDR_ID")
        private String aDDRESS_LOC_ADDR_ID;
        @JsonProperty("UTM_SOURCE")
        private Object uTM_SOURCE;
        @JsonProperty("UTM_MEDIUM")
        private Object uTM_MEDIUM;
        @JsonProperty("UTM_CAMPAIGN")
        private Object uTM_CAMPAIGN;
        @JsonProperty("UTM_CONTENT")
        private Object uTM_CONTENT;
        @JsonProperty("UTM_TERM")
        private Object uTM_TERM;
        @JsonProperty("PARENT_ID_1036")
        private Object pARENT_ID_1036;
        @JsonProperty("LAST_ACTIVITY_BY")
        private String lAST_ACTIVITY_BY;
        @JsonProperty("UF_CRM_1724038793494")
        private String uF_CRM_1724038793494;
        @JsonProperty("UF_CRM_1724038800114")
        private String uF_CRM_1724038800114;
        @JsonProperty("UF_CRM_CONTACT_AMOID")
        private String uF_CRM_CONTACT_AMOID;
        @JsonProperty("UF_CRM_66D988132964F")
        private String uF_CRM_66D988132964F;
        @JsonProperty("UF_CRM_66D9881352FA3")
        private String uF_CRM_66D9881352FA3;
        @JsonProperty("UF_CRM_66D988135D62A")
        private String uF_CRM_66D988135D62A;
        @JsonProperty("UF_CRM_66D988136C604")
        private String uF_CRM_66D988136C604;
        @JsonProperty("UF_CRM_66D988137A7A8")
        private String uF_CRM_66D988137A7A8;
        @JsonProperty("UF_CRM_66D9881385C86")
        private String uF_CRM_66D9881385C86;
        @JsonProperty("UF_CRM_66D9881390885")
        private String uF_CRM_66D9881390885;
        @JsonProperty("UF_CRM_66D988139E5BF")
        private String uF_CRM_66D988139E5BF;
        @JsonProperty("UF_CRM_66D98813ABE2E")
        private String uF_CRM_66D98813ABE2E;
        @JsonProperty("UF_CRM_66D98813BA72E")
        private String uF_CRM_66D98813BA72E;
        @JsonProperty("UF_CRM_66D98813C9C35")
        private String uF_CRM_66D98813C9C35;
        @JsonProperty("UF_CRM_66D98813D77E3")
        private String uF_CRM_66D98813D77E3;
        @JsonProperty("UF_CRM_66D98813E47B4")
        private String uF_CRM_66D98813E47B4;
        @JsonProperty("UF_CRM_66D98813F143A")
        private String uF_CRM_66D98813F143A;
        @JsonProperty("UF_CRM_66D988140815E")
        private String uF_CRM_66D988140815E;
        @JsonProperty("UF_CRM_66D9881412C0C")
        private String uF_CRM_66D9881412C0C;
        @JsonProperty("UF_CRM_66D988141F0FC")
        private String uF_CRM_66D988141F0FC;
        @JsonProperty("UF_CRM_66D988142A947")
        private String uF_CRM_66D988142A947;
        @JsonProperty("UF_CRM_66D988143654B")
        private String uF_CRM_66D988143654B;
        @JsonProperty("UF_CRM_66D988144380E")
        private String uF_CRM_66D988144380E;
        @JsonProperty("UF_CRM_66D988144ED87")
        private String uF_CRM_66D988144ED87;
        @JsonProperty("UF_CRM_66D9881459D5E")
        private String uF_CRM_66D9881459D5E;
        @JsonProperty("UF_CRM_66D9881465807")
        private String uF_CRM_66D9881465807;
        @JsonProperty("UF_CRM_66D98814709D3")
        private String uF_CRM_66D98814709D3;
        @JsonProperty("UF_CRM_66D988147CDAB")
        private String uF_CRM_66D988147CDAB;
        @JsonProperty("UF_CRM_66D9881489547")
        private String uF_CRM_66D9881489547;
        @JsonProperty("UF_CRM_66D9881494CB9")
        private String uF_CRM_66D9881494CB9;
        @JsonProperty("UF_CRM_66D98814A0E70")
        private String uF_CRM_66D98814A0E70;
        @JsonProperty("UF_CRM_INSTAGRAM_WZ")
        private String uF_CRM_INSTAGRAM_WZ;
        @JsonProperty("UF_CRM_TELEGRAMUSERNAME_WZ")
        private String uF_CRM_TELEGRAMUSERNAME_WZ;
        @JsonProperty("UF_CRM_VK_WZ")
        private String uF_CRM_VK_WZ;
        @JsonProperty("UF_CRM_AVITO_WZ")
        private String uF_CRM_AVITO_WZ;
        @JsonProperty("UF_CRM_TELEGRAMID_WZ")
        private String uF_CRM_TELEGRAMID_WZ;
        @JsonProperty("PHONE")
        private ArrayList<PHONE> pHONE;

        @Getter
        @Setter
        public static class PHONE {
            @JsonProperty("ID")
            private String iD;
            @JsonProperty("VALUE_TYPE")
            private String vALUE_TYPE;
            @JsonProperty("VALUE")
            private String vALUE;
            @JsonProperty("TYPE_ID")
            private String tYPE_ID;
        }
    }

}
