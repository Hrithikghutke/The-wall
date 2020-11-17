package com.Habitat.thewall.models;

import com.google.firebase.database.Exclude;

public class HomeView {
    @Exclude
    public String id;
    public String  title, url;

    @Exclude
    public String homeview;

    @Exclude
    public boolean isFavourites = false;

    public HomeView(String id, String title, String url, String homeview){
        this.id = id;
        this.title = title;
        this.url = url;
        this.homeview = homeview;

    }


}
