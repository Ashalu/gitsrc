package com.tadqa.android.pojo;

import java.util.HashMap;

public class FoodItem {

    private String name = "";
    private String id = "";
    private String fullPrice = "";
    private String halfPrice = "";
    private String qtrPrice = "";
    private String category = "";
    private String imageUrl = "";
    private String chefSpecial = "";
    private String isAvailable = "";

    public String getChefSpecial() {
        return chefSpecial;
    }

    public void setChefSpecial(String chefSpecial) {
        this.chefSpecial = chefSpecial;
    }

    public String getQtrPrice() {
        return qtrPrice;
    }

    public void setQtrPrice(String qtrPrice) {
        this.qtrPrice = qtrPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(String fullPrice) {
        this.fullPrice = fullPrice;
    }

    public String getHalfPrice() {
        return halfPrice;
    }

    public void setHalfPrice(String halfPrice) {
        this.halfPrice = halfPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    HashMap<String, String> Allprices = new HashMap<>();

    public HashMap<String, String> getAllPrices() {
        return Allprices;
    }

    public void setAllPrices(HashMap<String, String> prices) {
        this.Allprices = prices;
    }


}
