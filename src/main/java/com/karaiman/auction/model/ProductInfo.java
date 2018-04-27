package com.karaiman.auction.model;

import com.karaiman.auction.entity.Product;

public class ProductInfo {
    private String code;
    private String name;
    private double price;
    private String bidderCode;
 
    public ProductInfo() {
    }
 
    public ProductInfo(Product product) {
        this.code = product.getCode();
        this.name = product.getName();
        this.price = product.getPrice();
        this.bidderCode = product.getBidderCode();
    }
 
    // Using in JPA/Hibernate query
    public ProductInfo(String code, String name, double price, String bidderCode) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.bidderCode = bidderCode;
    }
 
    public String getCode() {
        return code;
    }
 
    public void setCode(String code) {
        this.code = code;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
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