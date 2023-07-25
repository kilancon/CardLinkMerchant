package com.crm.cardlinkmerchant.model;

import java.util.ArrayList;
import java.util.List;

public class Offer {
    private String id;
    private String name;
    private String short_description;
    private String long_description;
    private String life_cycle_state;
    private List<Award> awards = new ArrayList<>();

    public Offer() {
    }
    public Offer(String id, String name, String short_description,String long_description, String life_cycle_state, Double amount, String amount_type){
        this.id = id;
        this.name = name;
        this.short_description = short_description;
        this.life_cycle_state = life_cycle_state;
//        this.amount = amount;
//        this.amount_type = amount_type;
        this.long_description = long_description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLife_cycle_state() {
        return life_cycle_state;
    }

    public void setLife_cycle_state(String life_cycle_state) {
        this.life_cycle_state = life_cycle_state;
    }

    public List<Award> getAwards() {
        return awards;
    }

    public void setAwards(List<Award> awards) {
        this.awards = awards;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getLong_description() {
        return long_description;
    }

    public void setLong_description(String long_description) {
        this.long_description = long_description;
    }
    public static class Award {
        private Double amount;
        private String amount_type = "";
        private String currency = "";

        public Award(){};

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getAmount_type() {
            return amount_type;
        }

        public void setAmount_type(String amount_type) {
            this.amount_type = amount_type;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        @Override
        public String toString() {
            return "Award{" +
                    "amount=" + amount +
                    ", amount_type='" + amount_type + '\'' +
                    ", currency='" + currency + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
