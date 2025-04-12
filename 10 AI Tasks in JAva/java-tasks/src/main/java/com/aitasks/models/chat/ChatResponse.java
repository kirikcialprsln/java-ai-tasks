package com.aitasks.models.chat;

public class ChatResponse {
    private final String userMessage;
    private final String botResponse;
    private final boolean isPizzaRelated;
    private final String additionalInfo;
    
    public ChatResponse(String userMessage, String botResponse, 
                       boolean isPizzaRelated, String additionalInfo) {
        this.userMessage = userMessage;
        this.botResponse = botResponse;
        this.isPizzaRelated = isPizzaRelated;
        this.additionalInfo = additionalInfo;
    }
    
    // Getters
    public String getUserMessage() { return userMessage; }
    public String getBotResponse() { return botResponse; }
    public boolean isPizzaRelated() { return isPizzaRelated; }
    public String getAdditionalInfo() { return additionalInfo; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("👤 You: ").append(userMessage).append("\n");
        sb.append("🤖 Bot: ").append(botResponse).append("\n");
        
        if (isPizzaRelated) {
            sb.append("🍕 [Pizza Mode Activated!]\n");
        }
        
        if (additionalInfo != null) {
            sb.append("ℹ️ ").append(additionalInfo).append("\n");
        }
        
        return sb.toString();
    }
} 