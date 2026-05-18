/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartwallet.exception;

/**
 *
 * @author ABC
 */
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }
    
}
