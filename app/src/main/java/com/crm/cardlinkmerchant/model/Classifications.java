package com.crm.cardlinkmerchant.model;

import java.util.ArrayList;

public class Classifications extends ArrayList<ClassificationResponse> {
    private ArrayList<ClassificationResponse> content = new ArrayList<>();
    public ArrayList<ClassificationResponse> getContent(){return  content;}
}
