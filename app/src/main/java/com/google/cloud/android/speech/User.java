package com.google.cloud.android.speech;

import java.util.List;

/**
 * Created by ASUS on 11/05/2019.
 */

public class User {

    private String name;
    private String email;
    private String mlanguage;
    private String Subtitles;
    private String password;
    private String created_at;
    private String newPassword;
    private String token;
    private List<Product> list;

    public String getNewPassword() {
        return newPassword;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setLanguage(String name) {
        this.mlanguage = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
public void setSubtitles (String Subtitles) {
    this.Subtitles = Subtitles;
}

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public String getSubtitles() {
        return Subtitles;
    }
    public String getEmail() {
        return email;
    }
    public String getLanguage() {
        return mlanguage;
    }
    public String getCreated_at() {
        return created_at;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }
}