package com.win.sumsub.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    // Constructors
    public FinanceRequest() {}

    private FinanceRequest(Builder builder) {
        this.txnId = builder.txnId;
        this.txnDate = builder.txnDate;
        this.type = builder.type;
        this.info = builder.info;
        this.applicant = builder.applicant;
    }

    // Getters and Setters
    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    // Builder class
    public static class Builder {
        private String txnId;
        private String txnDate;
        private String type;
        private Info info;
        private Applicant applicant;

        public Builder txnId(String txnId) {
            this.txnId = txnId;
            return this;
        }

        public Builder txnDate(String txnDate) {
            this.txnDate = txnDate;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder info(Info info) {
            this.info = info;
            return this;
        }

        public Builder applicant(Applicant applicant) {
            this.applicant = applicant;
            return this;
        }

        public FinanceRequest build() {
            return new FinanceRequest(this);
        }
    }

    // Inner class Info
    public static class Info {
        @JsonProperty("direction")
        private String direction;

        @JsonProperty("amount")
        private int amount;

        @JsonProperty("currencyCode")
        private String currencyCode;

        // Constructors
        public Info() {}

        public Info(String direction, int amount, String currencyCode) {
            this.direction = direction;
            this.amount = amount;
            this.currencyCode = currencyCode;
        }

        // Getters and Setters
        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }
    }

    // Inner class Applicant
    public static class Applicant {
        @JsonProperty("type")
        private String type;

        @JsonProperty("externalUserId")
        private String externalUserId;

        @JsonProperty("fullName")
        private String fullName;

        // Constructors
        public Applicant() {}

        public Applicant(String type, String externalUserId, String fullName) {
            this.type = type;
            this.externalUserId = externalUserId;
            this.fullName = fullName;
        }

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getExternalUserId() {
            return externalUserId;
        }

        public void setExternalUserId(String externalUserId) {
            this.externalUserId = externalUserId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}