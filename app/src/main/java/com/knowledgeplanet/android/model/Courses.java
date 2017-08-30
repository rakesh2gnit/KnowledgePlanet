package com.knowledgeplanet.android.model;

/**
 * Created by Admin on 26-08-2017.
 */

public class Courses {

    private String name;

    public Courses() {
    }

    public Courses(String name) {
        this.name = name;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String name) {
        this.name = name;
    }
}
