package com.smartwallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AIAssistantRequest {

    @NotBlank(message = "Question cannot be empty")
    @Size(
            max = 500,
            message = "Question must not exceed 500 characters"
    )
    private String question;

    public AIAssistantRequest() {
    }

    public AIAssistantRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}