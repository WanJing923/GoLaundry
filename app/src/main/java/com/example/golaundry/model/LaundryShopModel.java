package com.example.golaundry.model;

import java.util.List;

public class LaundryShopModel {

    private String images;
    private List<String> allTimeRanges;

    public LaundryShopModel() {
    }

    public LaundryShopModel(String images, List<String> allTimeRanges) {
        this.images = images;
        this.allTimeRanges = allTimeRanges;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public List<String> getAllTimeRanges() {
        return allTimeRanges;
    }

    public void setAllTimeRanges(List<String> allTimeRanges) {
        this.allTimeRanges = allTimeRanges;
    }
}
