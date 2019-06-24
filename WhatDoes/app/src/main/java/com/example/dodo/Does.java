package com.example.dodo;

public class Does {

    public String title;
    public String desc;
    public String date;



    public Does()
    {

    }

    public Does(String title, String desc, String date) {
        this.title = title;
        this.desc = desc;
        this.date = date;

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
