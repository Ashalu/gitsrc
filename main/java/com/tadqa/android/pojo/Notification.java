package com.tadqa.android.pojo;

import java.io.Serializable;

public class Notification implements Serializable {

    int id;
    String message = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
