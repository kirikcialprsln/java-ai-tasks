package com.aitasks.models.spam;

public class EmailMessage {
    private final String content;
    private final EmailType type;
    
    public EmailMessage(String content, EmailType type) {
        this.content = content;
        this.type = type;
    }
    
    public String getContent() { return content; }
    public EmailType getType() { return type; }
} 