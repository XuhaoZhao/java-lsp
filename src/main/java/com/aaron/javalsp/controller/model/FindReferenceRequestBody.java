package com.aaron.javalsp.controller.model;


import lombok.Data;

@Data
public class FindReferenceRequestBody {

    private String path;

    private int line;

    private int character;
}
