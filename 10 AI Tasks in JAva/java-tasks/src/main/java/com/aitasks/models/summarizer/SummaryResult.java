package com.aitasks.models.summarizer;

public class SummaryResult {
    private final String originalText;
    private final String summary;
    private final double compressionRatio;
    private final String message;
    
    public SummaryResult(String originalText, String summary, 
                        double compressionRatio, String message) {
        this.originalText = originalText;
        this.summary = summary;
        this.compressionRatio = compressionRatio;
        this.message = message;
    }
    
    // Getters
    public String getOriginalText() { return originalText; }
    public String getSummary() { return summary; }
    public double getCompressionRatio() { return compressionRatio; }
    public String getMessage() { return message; }
    
    @Override
    public String toString() {
        return String.format("""
            📝 Text Summary:
            Original Length: %d chars
            Summary Length: %d chars
            Compression: %.1f%%
            
            Summary:
            %s
            
            %s
            """,
            originalText.length(),
            summary.length(),
            (1 - compressionRatio) * 100,
            summary,
            message
        );
    }
} 