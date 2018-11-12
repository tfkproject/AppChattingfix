package com.example.zart.appchattingfix.model;

public class ProfilUser {

    private String phone;
    private String name;
    private String username;
    private String foto;

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getFoto() {
        return foto;
    }

    public ProfilUser(String phone, String name, String username, String foto) {
        this.phone = phone;
        this.name = name;
        this.username = username;
        this.foto = foto;
    }
}
