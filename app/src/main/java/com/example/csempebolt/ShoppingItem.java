package com.example.csempebolt;

public class ShoppingItem {
    private String id;
    private String name;
    private String info;
    private String price;
    private int imageResource;
    private int cartedCount;

    public ShoppingItem() {
    }

    public ShoppingItem(String name, String info, String price, int imageResource, int cartedCount) {
        this.imageResource = imageResource;
        this.price = price;
        this.info = info;
        this.name = name;
        this.cartedCount = cartedCount;
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

    public int getCartedCount() {
        return cartedCount;
    }
    public String _getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
