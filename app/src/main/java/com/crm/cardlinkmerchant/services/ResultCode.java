package com.crm.cardlinkmerchant.services;

public enum ResultCode {
    OK("OK"),
    INVALID_CARD("01"),
    CUSTOMER_NOT_FOUND("02"),
    CUSTOMER_FINANCIAL_NOT_FOUND("03"),
    UNKNOWN_ERROR("99"),
    NOT_CONNECT_API("404"),
    REQUEST_TIMEOUT("408"),
    MANY_REQUESTS("MANY_REQUESTS"),
    INVALIDLOGINEXCEPTION("INVALIDLOGINEXCEPTION"),
    READY_EXISTS_FOR_ANOTHER_CONTAC_TEXCEPTION("READY_EXISTS_FOR_ANOTHER_CONTAC_TEXCEPTION");

    private String label;
    ResultCode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
