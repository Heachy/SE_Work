package com.cy.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private int code;
    private int data;
    public boolean is_rage;
    private int rage;
    private String message;
}
