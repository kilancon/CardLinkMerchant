package com.crm.cardlinkmerchant.model;

import java.util.ArrayList;

public class PurchaseResponse {
    private PaginResponse paging = new PaginResponse();
    private ArrayList<PurchaseContentResponse> content = new ArrayList<>();

    public PaginResponse getPaging() {
        return paging;
    }

    public void setPaging(PaginResponse paging) {
        this.paging = paging;
    }

    public ArrayList<PurchaseContentResponse> getContent() {
        return content;
    }

    public void setContent(ArrayList<PurchaseContentResponse> content) {
        this.content = content;
    }
}
