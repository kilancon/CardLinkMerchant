package com.crm.cardlinkmerchant.model;

public class Transaction {
    private String contact_name;
    private String account_number;
    private String contact_id;
    private Boolean status;
    private Double amount;
    private String reference_number;
    private String created_date;

    public Transaction(String contact_name, String account_number, Boolean status, Double amount, String created_date, String contact_id, String reference_number){
        this.contact_name = contact_name;
        this.account_number = account_number;
        this.status = status;
        this.amount = amount;
        this.created_date = created_date;
        this.contact_id = contact_id;
        this.reference_number = reference_number;
    }
    public String getContactName() {
        return contact_name;
    }

    public void setContactName(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getAccountNumber() { return account_number; }

    public void setAccountNumber(String account_number) {
        this.account_number = account_number;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getContactId() {
        return contact_id;
    }

    public void setContactId(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getCreatedDate(){return created_date;}

    public void setCreatedDate(String created_date) {
        this.created_date = created_date;
    }

    public String getReferenceNumber(){return reference_number;}

    public void setReferenceNumber(String reference_number) {
        this.reference_number = reference_number;
    }

}
