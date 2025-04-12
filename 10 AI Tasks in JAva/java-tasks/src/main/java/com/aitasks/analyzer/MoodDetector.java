package com.aitasks.analyzer;

import java.util.HashMap;
import java.util.Map;
import opennlp.tools.sentiment.SentimentModel;
import opennlp.tools.sentiment.SentimentAnalyzer;

/**
 * A sentiment analysis tool that detects the mood of text and provides
 * appropriate GIF responses using OpenNLP for sentiment analysis.
 * 
 * @author [Your Name]
 * @version 1.0
 */
public class MoodDetector {
    private SentimentAnalyzer analyzer;
    private Map<String, String> moodGifs;
    
    /**
     * Initializes the MoodDetector with sentiment analysis model and mood-GIF mappings.
     */
    public MoodDetector() {
        initializeMoodGifs();
        // Initialize OpenNLP sentiment analyzer
        try {
            // Load model file
            SentimentModel model = new SentimentModel(
                getClass().getResourceAsStream("/en-sentiment.bin"));
            analyzer = new SentimentAnalyzer(model);
        } catch (Exception e) {
            System.err.println("Failed to initialize sentiment analyzer: " + e.getMessage());
        }
    }
    
    /**
     * Initializes the mapping between moods and their corresponding GIF URLs.
     */
    private void initializeMoodGifs() {
        moodGifs = new HashMap<>();
        moodGifs.put("happy", "https://giphy.com/happy-dance");
        moodGifs.put("sad", "https://giphy.com/sad-cat");
        moodGifs.put("neutral", "https://giphy.com/meh");
    }
    
    /**
     * Analyzes the mood of the given text and returns a mood description with a matching GIF.
     * 
     * @param text The text to analyze
     * @return A string containing the detected mood and a matching GIF URL
     */
    public String analyzeMood(String text) {
        try {
            double sentiment = analyzer.getSentiment(text);
            String mood = getMoodFromScore(sentiment);
            return String.format("Mood: %s\nHere's a fitting GIF: %s", 
                               mood, moodGifs.get(mood));
        } catch (Exception e) {
            return "Failed to analyze mood: " + e.getMessage();
        }
    }
    
    /**
     * Converts a sentiment score to a mood category.
     * 
     * @param score The sentiment score (-1.0 to 1.0)
     * @return The corresponding mood category (happy, sad, or neutral)
     */
    private String getMoodFromScore(double score) {
        if (score > 0.5) return "happy";
        if (score < -0.5) return "sad";
        return "neutral";
    }
} 