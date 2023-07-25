package com.crm.cardlinkmerchant.model;

public class AccountResponse {
    private String id;
    private String number;
    private String life_cycle_state;
    private String currency_code;
    private Boolean is_primary;
    private String name;
    private Double balance;
    private AccountWalletResponse wallet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLife_cycle_state() {
        return life_cycle_state;
    }

    public void setLife_cycle_state(String life_cycle_state) {
        this.life_cycle_state = life_cycle_state;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public Boolean getIs_primary() {
        return is_primary;
    }

    public void setIs_primary(Boolean is_primary) {
        this.is_primary = is_primary;
    }

    public AccountWalletResponse getWallet() {
        return wallet;
    }

    public void setWallet(AccountWalletResponse wallet) {
        this.wallet = wallet;
    }
}
