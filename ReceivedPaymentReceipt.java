package com.example.companyXXX;

public class ReceivedPaymentReceipt {

    private String amount;
    private String sender;

    ReceivedPaymentReceipt(){
    }

    ReceivedPaymentReceipt(String amount,String sender){
        this.amount = amount;
        this.sender = sender;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        sender = sender;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        amount = amount;
    }

}
