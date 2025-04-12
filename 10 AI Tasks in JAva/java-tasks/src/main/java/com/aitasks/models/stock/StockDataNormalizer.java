package com.aitasks.models.stock;

public class StockDataNormalizer {
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    
    public double normalize(double value) {
        updateMinMax(value);
        return (value - min) / (max - min);
    }
    
    public double denormalize(double normalizedValue) {
        return normalizedValue * (max - min) + min;
    }
    
    private void updateMinMax(double value) {
        min = Math.min(min, value);
        max = Math.max(max, value);
    }
} 