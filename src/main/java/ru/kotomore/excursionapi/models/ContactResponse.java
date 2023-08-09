package ru.kotomore.excursionapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContactResponse {

    @JsonProperty("_embedded")
    private Embedded embedded;

    @Data
    public static class Embedded {
        private List<Contact> contacts;
        private List<Lead> leads;
    }

    @Data
    public static class Contact {
        private long id;
        private String name;

        @JsonProperty("custom_fields_values")
        private List<CustomFieldValue> customFieldsValues;
        @JsonProperty("account_id")
        private int accountId;
        @JsonProperty("_embedded")
        private Contact.Embedded embedded;

        @Data
        public static class CustomFieldValue {
            private List<Value> values;

            @Data
            public static class Value {
                private String value;
            }
        }

        @Data
        public static class Embedded {
            private List<Lead> leads;
        }
    }

    @Data
    public static class Lead {
        private int id;

    }
}
