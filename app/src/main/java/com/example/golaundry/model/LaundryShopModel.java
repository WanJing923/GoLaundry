package com.example.golaundry.model;

import java.util.List;

public class LaundryShopModel {
    private String laundryId;
    private String images;
    private List<String> allTimeRanges;

    public LaundryShopModel() {
    }

    public LaundryShopModel(String laundryId, String images, List<String> allTimeRanges) {
        this.laundryId = laundryId;
        this.images = images;
        this.allTimeRanges = allTimeRanges;
    }

    public String getLaundryId() {
        return laundryId;
    }

    public void setLaundryId(String laundryId) {
        this.laundryId = laundryId;
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
