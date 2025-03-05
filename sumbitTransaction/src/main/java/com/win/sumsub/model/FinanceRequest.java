package com.win.sumsub.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.nashorn.internal.objects.annotations.Setter;



public class FinanceRequest {

    @JsonProperty("txnId")
    private String txnId;

    @JsonProperty("txnDate")
    private String txnDate;

    @JsonProperty("type")
    private String type;

    @JsonProperty("info")
    private Info info;

    @JsonProperty("applicant")
    private Applicant applicant;

    @Data
    public static class Info {
        @JsonProperty("direction")
        private String direction;

        @JsonProperty("amount")
        private int amount;

        @JsonProperty("currencyCode")
        private String currencyCode;
    }

    @Data
    public static class Applicant {
        @JsonProperty("type")
        private String type;

        @JsonProperty("externalUserId")
        private String externalUserId;

        @JsonProperty("fullName")
        private String fullName;
    }
}