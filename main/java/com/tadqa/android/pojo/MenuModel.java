package com.tadqa.android.pojo;

/**
 * Created by Aashish on 10/5/2016.
 */

public class MenuModel {
    private String name;
    private String Url;

    public String isId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    String  Id;
    String isvisible;
    public String getVisibility() {
        return isvisible;
    }

    public void setVisibility(String visible) {
        isvisible=visible;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
