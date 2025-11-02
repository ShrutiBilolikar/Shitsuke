package com.example.demo.dto;

public class RecordTypeRequest {
    private String name;
    private String type; // Boolean, Number, Text

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}
