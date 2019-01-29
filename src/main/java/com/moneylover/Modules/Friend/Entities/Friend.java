package com.moneylover.Modules.Friend.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

public class Friend extends BaseModel {
    private int userId;

    private String name;

    private String image;

    public Friend() {}

    public Friend(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public static String getTable() {
        return "friends";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
