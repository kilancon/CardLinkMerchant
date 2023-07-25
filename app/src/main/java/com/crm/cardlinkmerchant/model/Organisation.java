package com.crm.cardlinkmerchant.model;

public class Organisation {
    String name;
    String external_id;
    String org_type;

    public Organisation(){}
    public Organisation(String name, String external_id, String org_type){
        this.name = name;
        this.external_id = external_id;
        this.org_type = org_type;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getExternal_id(){
        return external_id;
    }
    public void setExternal_id(String external_id){
        this.external_id = external_id;
    }
    public String getOrg_type(){
        return org_type;
    }
    public void setOrg_type(String org_type){
        this.org_type = org_type;
    }

    @Override
    public String toString() {
        return name;
    }
}
