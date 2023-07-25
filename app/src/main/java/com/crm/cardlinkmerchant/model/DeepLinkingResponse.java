package com.crm.cardlinkmerchant.model;

public class DeepLinkingResponse {
    private String amountTotal;
    private String bankId;
    private String batchNumber;
    private String Version;

    private String transactionId;

    private String transactionDescriptionType;

    private String transactionDate;

    private String terminalId;

    private String Settled;

    private String receiptNumber;

    private String Rnn;

    private String panCard;

    private String deviceSn;

    private String cardType;

    private String clientTransactionId;

    public String getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(String amountTotal) {
        this.amountTotal = amountTotal;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDescriptionType() {
        return transactionDescriptionType;
    }

    public void setTransactionDescriptionType(String transactionDescriptionType) {
        this.transactionDescriptionType = transactionDescriptionType;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getSettled() {
        return Settled;
    }

    public void setSettled(String settled) {
        Settled = settled;
    }

    public String getRnn() {
        return Rnn;
    }

    public void setRnn(String rnn) {
        Rnn = rnn;
    }

    public String getPanCard() {
        return panCard;
    }

    public void setPanCard(String panCard) {
        this.panCard = panCard;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getClientTransactionId() {
        return clientTransactionId;
    }

    public void setClientTransactionId(String clientTransactionId) {
        this.clientTransactionId = clientTransactionId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
