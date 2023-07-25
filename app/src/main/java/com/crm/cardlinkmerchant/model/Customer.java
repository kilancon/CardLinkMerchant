package com.crm.cardlinkmerchant.model;

import java.util.List;

public class Customer {
    private String code;
    private String id;

    private Financial financials;

    public Financial getFinancials() {
        return financials;
    }

    public void setFinancials(Financial financials) {
        this.financials = financials;
    }

    public Customer(){}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public static class Financial{
        private Wallet wallet;

        public Financial() {
        }

        public Wallet getWallet() {
            return wallet;
        }

        public void setWallet(Wallet wallet) {
            this.wallet = wallet;
        }
    }

    public static class Wallet{
        private Double balance = 0.0;
        private String currency_code;

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCurrency_code() {
            return currency_code;
        }

        public void setCurrency_code(String currency_code) {
            this.currency_code = currency_code;
        }

        public Wallet(){}

        public Double getBalance() {
            return balance;
        }

        public void setBalance(Double balance) {
            this.balance = balance;
        }


    }
}
