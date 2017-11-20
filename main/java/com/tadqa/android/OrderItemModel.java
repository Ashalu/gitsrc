package com.tadqa.android;

import android.view.View;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.tadqa.android.pojo.OrderStatus;

import java.io.Serializable;
import java.util.List;

public class OrderItemModel implements Serializable {

    private String ItemName;
    private String DeliveryAddress;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private int itemCounts = 0;

    public int getItemCounts() {
        return itemCounts;
    }

    public void setItemCounts(int itemCounts) {
        this.itemCounts = itemCounts;
    }

    private String id;

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        DeliveryAddress = deliveryAddress;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    private String Price;
    private String Qty;

    public String getQty() {
        return Qty;
    }

    public void setQty(String itemQty) {
        Qty = itemQty;
    }

    private String OrderDateTime;

    public String getOrderDateTime() {
        return OrderDateTime;
    }

    public void setOrderDateTime(String dt) {
        OrderDateTime = dt;
    }

    private String OrdStatus;

    public String getOrdStatus() {
        return OrdStatus;
    }

    public void setOrdStatus(String dt) {
        OrdStatus = dt;
    }


    private String DeliveryDateTime;

    public String getDeliveryDateTime() {
        return DeliveryDateTime;
    }

    public void setDeliveryDateTime(String dt) {
        DeliveryDateTime = dt;
    }

}


