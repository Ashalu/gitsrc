package com.tadqa.android.pojo;

/**
 * Created by AW-14 on 2/2/2016.
 */
public class FaqCollection {


    public FaqCollection() {
    }

    public String getTitle() {
        return FaqQuestion;
    }

    public void setTitle(String title) {
        this.FaqQuestion = title;
    }

    public String getDescription() {
        return FaqAnswer;
    }

    public void setDescription(String description) {
        this.FaqAnswer = description;
    }

    public String FaqQuestion;

    public String FaqAnswer;


    public FaqCollection(String title, String description) {
        this.FaqQuestion = title;

        this.FaqAnswer = description;

    }



}

