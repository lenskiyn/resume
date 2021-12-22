package com.sbrf.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private long id;
    private String name;
    private boolean isAdmin;
    private Boolean isNotified;

    protected User() {
    }

    public User(long id, String name) {
        this.id = id;
        this.name = name;
        this.isAdmin = id == 188014382L;
        this.isNotified = false;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }

    @Override
    public String toString() {
        return "|" + name +
                "| id=" + id +
                " admin=" + isAdmin +
                " notified=" + isNotified +
                '}';
    }
}
