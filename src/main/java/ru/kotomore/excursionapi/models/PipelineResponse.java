package ru.kotomore.excursionapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PipelineResponse {

    @JsonProperty("_total_items")
    private int totalItems;

    private Links _links;
    private Embedded _embedded;

    @Data
    public static class Links {
        private Self self;
    }

    @Data
    public static class Self {
        private String href;
    }

    @Data
    public static class Embedded {
        private List<Pipeline> pipelines;
    }

    @Data
    public static class Pipeline {
        private int id;
        private String name;
        private int sort;
        private boolean is_main;
        private boolean is_unsorted_on;
        private boolean is_archive;
        private int account_id;
        private Links self;
        private EmbeddedStatuses _embedded;
    }

    @Data
    public static class EmbeddedStatuses {
        private List<Status> statuses;
    }

    @Data
    public static class Status {
        private int id;
        private String name;
        private int sort;
        private boolean is_editable;
        private int pipeline_id;
        private String color;
        private int type;
        private int account_id;
        private Links self;
    }
}

