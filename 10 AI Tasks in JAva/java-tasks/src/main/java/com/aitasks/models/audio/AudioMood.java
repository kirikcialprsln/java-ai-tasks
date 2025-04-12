package com.aitasks.models.audio;

import java.awt.Color;

public enum AudioMood {
    HAPPY(new Color(255, 255, 0), "😊"),    // Yellow
    EXCITED(new Color(255, 128, 0), "🤩"),  // Orange
    CALM(new Color(0, 255, 255), "😌"),     // Cyan
    ANGRY(new Color(255, 0, 0), "😠"),      // Red
    NEUTRAL(new Color(255, 255, 255), "😐"); // White
    
    private final Color color;
    private final String emoji;
    
    AudioMood(Color color, String emoji) {
        this.color = color;
        this.emoji = emoji;
    }
    
    public Color getColor() { return color; }
    public String getEmoji() { return emoji; }
} 