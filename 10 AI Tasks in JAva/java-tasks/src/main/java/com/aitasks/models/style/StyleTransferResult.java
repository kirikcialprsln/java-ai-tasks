package com.aitasks.models.style;

public class StyleTransferResult {
    private final String outputPath;
    private final String message;
    private final boolean success;
    
    public StyleTransferResult(String outputPath, String message, boolean success) {
        this.outputPath = outputPath;
        this.message = message;
        this.success = success;
    }
    
    // Getters
    public String getOutputPath() { return outputPath; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
    
    @Override
    public String toString() {
        if (success) {
            return String.format("""
                🎨 Style Transfer Complete!
                Output: %s
                %s
                """,
                outputPath,
                message
            );
        } else {
            return String.format("❌ Style Transfer Failed: %s", message);
        }
    }
} 