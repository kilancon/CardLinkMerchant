package com.crm.cardlinkmerchant.model;

public class PurchaseContentResponse implements Comparable<PurchaseContentResponse>{
    private AccountResponse account = new AccountResponse();
    private ContactResponse contact = new ContactResponse();
    private OrganisationResponse organisation = new OrganisationResponse();
    private ClassificationResponse classification = new ClassificationResponse();
    private String id;
    private String reference_number;
    private String number;
    private String state;
    private String reduction_method;
    private Double total_net_amount;
    private Double total_tax_amount;
    private Double discount_amount;
    private Double total_amount;
    private Double created_date;
    private String activity_type = "";
    private String life_cycle_state;
    private Double performed_on;

    public static final String STATUS_VOIDED = "VOIDED";
    public static final String STATUS_POSTED = "POSTED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public PurchaseContentResponse() {
    }

    public PurchaseContentResponse(String id, ContactResponse contact, String reference_number, String state, Double total_amount, Double created_date) {
        this.id = id;
        this.contact = contact;
        this.reference_number = reference_number;
        this.state = state;
        this.total_amount = total_amount;
        this.created_date = created_date;
        this.performed_on = created_date;
        this.life_cycle_state = state;
    }

    public AccountResponse getAccount() {
        return account;
    }

    public void setAccount(AccountResponse account) {
        this.account = account;
    }

    public ContactResponse getContact() {
        return contact;
    }

    public void setContact(ContactResponse contact) {
        this.contact = contact;
    }

    public OrganisationResponse getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationResponse organisation) {
        this.organisation = organisation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference_number() {
        if(activity_type.equals("TOP_UP"))
            return number;
        else
            return reference_number;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
    }

    public String getState() {
        if(activity_type.equals("TOP_UP"))
            return state;
        else
            return life_cycle_state;
    }

    public void setState(String state) {
        if(activity_type.equals("TOP_UP"))
            this.state = state;
        else
            this.life_cycle_state = state;

    }

    public String getReduction_method() {
        return reduction_method;
    }

    public void setReduction_method(String reduction_method) {
        this.reduction_method = reduction_method;
    }

    public ClassificationResponse getClassification() {
        return classification;
    }

    public void setClassification(ClassificationResponse classification) {
        this.classification = classification;
    }

    public Double getTotal_net_amount() {
        return total_net_amount;
    }

    public void setTotal_net_amount(Double total_net_amount) {
        this.total_net_amount = total_net_amount;
    }

    public Double getTotal_tax_amount() {
        return total_tax_amount;
    }

    public void setTotal_tax_amount(Double total_tax_amount) {
        this.total_tax_amount = total_tax_amount;
    }

    public Double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public Double getCreated_date() {
        if(activity_type.equals("TOP_UP"))
            return created_date;
        else
            return performed_on;
    }

    public void setCreated_date(Double created_date) {
        this.created_date = created_date;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }



    @Override
    public int compareTo(PurchaseContentResponse purchaseContentResponse) {
        if (getCreated_date() == null || purchaseContentResponse.getCreated_date() == null) {
            return 0;
        }
        return purchaseContentResponse.getCreated_date().compareTo(getCreated_date());
    }
}
