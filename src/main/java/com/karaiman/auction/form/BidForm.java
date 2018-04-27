package com.karaiman.auction.form;


public class BidForm {
    private String code;
    private double newPrice;
    private double price;
    private String bidderCode;

    public BidForm() {

    }
 
    public String getCode() {
        return code;
    }
 
    public void setCode(String code) {
        this.code = code;
    }
 
    public double getNewPrice() {
        return newPrice;
    }
 
    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }
 
    public double getPrice() {
        return price;
    }
 
    public void setPrice(double price) {
        this.price = price;
    }

    public String getBidderCode() {
        return bidderCode;
    }

    public void setBidderCode(String bidderCode) {
        this.bidderCode = bidderCode;
    }

}