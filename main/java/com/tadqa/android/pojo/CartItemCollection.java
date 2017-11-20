package com.tadqa.android.pojo;

import java.util.List;

/**
 * Created by AW-04 on 5/4/2016.
 */
public class CartItemCollection {
    public CartItemCollection() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public List<CheckoutModel> getCheckList() {
        return list;
    }

    public void setCheckList(List<CheckoutModel> chklist) {
        this.list = chklist;
    }

    public String Title;

    public List<CheckoutModel> list;


    public CartItemCollection(String title, List<CheckoutModel> chklist) {
        this.Title = title;
        this.list = chklist;
    }
}
