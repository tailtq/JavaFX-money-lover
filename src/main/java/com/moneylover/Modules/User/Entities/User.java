package com.moneylover.Modules.User.Entities;

import com.moneylover.Infrastructure.Models.BaseModel;

import java.util.Date;

public class User extends BaseModel {
    private String name;

    private String email;

    private String password;

    private Date birthday;

    private String phone;

    public static String getTable() {
        return "users";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
