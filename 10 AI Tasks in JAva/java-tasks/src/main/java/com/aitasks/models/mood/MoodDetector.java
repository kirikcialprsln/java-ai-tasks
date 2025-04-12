package com.aitasks.models.mood;

import com.aitasks.core.BaseAITask;
import opennlp.tools.sentiment.SentimentModel;
import opennlp.tools.sentiment.SentimentAnalyzer;

public class MoodDetector extends BaseAITask {
    private SentimentAnalyzer analyzer;
    private final GifResponseGenerator gifGenerator;
    
    public MoodDetector() {
        super();
        this.gifGenerator = new GifResponseGenerator();
    }
    
    @Override
    public void initialize() throws Exception {
        logger.info("Initializing MoodDetector...");
        try {
            SentimentModel model = new SentimentModel(
                getClass().getResourceAsStream(config.getProperty("model.sentiment.path")));
            analyzer = new SentimentAnalyzer(model);
            initialized = true;
            logger.info("MoodDetector initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize MoodDetector", e);
            throw e;
        }
    }
    
    public MoodResult analyzeMood(String text) {
        checkInitialized();
        try {
            double sentiment = analyzer.getSentiment(text);
            Mood mood = getMoodFromScore(sentiment);
            String gif = gifGenerator.getGifForMood(mood);
            return new MoodResult(text, mood, gif);
        } catch (Exception e) {
            logger.error("Mood analysis failed", e);
            return new MoodResult(text, Mood.NEUTRAL, "Error analyzing mood 😅");
        }
    }
    
    private Mood getMoodFromScore(double score) {
        if (score > 0.5) return Mood.HAPPY;
        if (score < -0.5) return Mood.SAD;
        return Mood.NEUTRAL;
    }
    
    @Override
    public void cleanup() throws Exception {
        initialized = false;
        logger.info("MoodDetector cleaned up");
    }
    
    @Override
    public String getTaskName() {
        return "Mood Detector";
    }
    
    @Override
    public String getTaskDescription() {
        return "Analyzes text sentiment and responds with humorous GIFs";
    }
} 