package com.example.csempebolt;

public class ShoppingItem {
    private String name;
    private String info;
    private String price;
    private final int imageResource;

    public ShoppingItem(String name, String info, String price, int imageResource) {
        this.imageResource = imageResource;
        this.price = price;
        this.info = info;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }

}
