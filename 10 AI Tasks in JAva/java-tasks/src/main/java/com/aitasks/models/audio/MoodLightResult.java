package com.aitasks.models.audio;

import java.awt.Color;

public class MoodLightResult {
    private final AudioMood mood;
    private final Color color;
    private final String message;
    
    public MoodLightResult(AudioMood mood, Color color, String message) {
        this.mood = mood;
        this.color = color;
        this.message = message;
    }
    
    // Getters
    public AudioMood getMood() { return mood; }
    public Color getColor() { return color; }
    public String getMessage() { return message; }
    
    @Override
    public String toString() {
        return String.format("""
            🎵 Audio Mood Analysis:
            Mood: %s %s
            Color: RGB(%d, %d, %d)
            %s
            """,
            mood.name(),
            mood.getEmoji(),
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            message
        );
    }
} 