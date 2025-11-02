package com.example.demo.model;

import jakarta.persistence.*;

import java.util.UUID;
import jakarta.persistence.*;


import java.util.UUID;

@Entity
@Table(name = "recordType")
public class RecordType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false,unique = true)
    private String recordTypeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Type getType() {
        return this.type;
    }

    public User getUser(){
        System.out.println("Record type id " + this.recordTypeId);
        return this.user;
    }
    public void setType(Type type) {
        this.type=type;
    }

    public void setName(String name) {
        this.name=name;
    }

    public void setUser(User user) {
        this.user = user;
    }
}


