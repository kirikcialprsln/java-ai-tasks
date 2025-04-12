package com.aitasks.models.stock;

public class PredictionResult {
    private final double currentPrice;
    private final double predictedPrice;
    private final double changePercent;
    private final boolean isUptrend;
    private final String fortuneMessage;
    
    public PredictionResult(double currentPrice, double predictedPrice,
                          double changePercent, boolean isUptrend,
                          String fortuneMessage) {
        this.currentPrice = currentPrice;
        this.predictedPrice = predictedPrice;
        this.changePercent = changePercent;
        this.isUptrend = isUptrend;
        this.fortuneMessage = fortuneMessage;
    }
    
    // Getters
    public double getCurrentPrice() { return currentPrice; }
    public double getPredictedPrice() { return predictedPrice; }
    public double getChangePercent() { return changePercent; }
    public boolean isUptrend() { return isUptrend; }
    public String getFortuneMessage() { return fortuneMessage; }
    
    @Override
    public String toString() {
        return String.format("""
            🔮 Stock Market Fortune:
            Current Price: $%.2f
            Predicted Price: $%.2f
            Change: %.1f%%
            Trend: %s
            
            Fortune Cookie Says:
            %s
            """,
            currentPrice,
            predictedPrice,
            changePercent,
            isUptrend ? "📈 Up" : "📉 Down",
            fortuneMessage
        );
    }
} 