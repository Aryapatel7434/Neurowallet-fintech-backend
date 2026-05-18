/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartwallet.exception;
import java.time.LocalDateTime;
/**
 *
 * @author ABC
 */
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    
    public ErrorResponse(LocalDateTime timestamp,int status,String message){
        this.timestamp=timestamp;
        this.status=status;
        this.message=message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
    
    
}
