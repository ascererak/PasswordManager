package edu.khai.csn.abondar.passwordmanager.Model.Entities;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{
    private int userID;
    private String username;
    private String name;
    private String email;
    private String masterPassword;
    private ArrayList<Password> passwords;

    public User(){}

    public User(int userID, String username, String name, String email, String masterPassword, ArrayList<Password> passwords) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.name = name;
        this.masterPassword = masterPassword;
        this.passwords = passwords;
    }

    public void setPasswords(ArrayList<Password> passwords) {
        this.passwords = passwords;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
