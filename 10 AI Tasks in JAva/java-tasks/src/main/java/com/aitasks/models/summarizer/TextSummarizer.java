package com.aitasks.models.summarizer;

import com.aitasks.core.BaseAITask;
import java.util.*;
import java.util.stream.Collectors;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class TextSummarizer extends BaseAITask {
    private SentenceDetectorME sentenceDetector;
    private final Random random = new Random();
    
    private final String[] coffeeBreakMessages = {
        "☕ Whew! That's a lot of words. Time for a coffee break!",
        "☕ My circuits need caffeine after processing all that!",
        "☕ BRB, grabbing a virtual espresso...",
        "☕ Processing complete! Coffee.exe initiated.",
        "☕ Warning: Excessive word count detected. Deploying coffee beans!"
    };
    
    @Override
    public void initialize() throws Exception {
        logger.info("Initializing TextSummarizer...");
        try {
            // Initialize sentence detector
            SentenceModel model = new SentenceModel(
                getClass().getResourceAsStream("/models/en-sent.bin")
            );
            sentenceDetector = new SentenceDetectorME(model);
            
            initialized = true;
            logger.info("TextSummarizer initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize TextSummarizer", e);
            throw e;
        }
    }
    
    public SummaryResult summarize(String text, double ratio) {
        checkInitialized();
        try {
            // Split text into sentences
            String[] sentences = sentenceDetector.sentDetect(text);
            
            if (sentences.length < 3) {
                return new SummaryResult(
                    text,
                    text,
                    1.0,
                    "Text is too short for summarization! 📝"
                );
            }
            
            // Calculate sentence scores
            Map<String, Double> sentenceScores = calculateSentenceScores(sentences);
            
            // Select top sentences
            int numSentences = Math.max(1, (int)(sentences.length * ratio));
            List<String> topSentences = selectTopSentences(sentences, sentenceScores, numSentences);
            
            // Build summary
            String summary = String.join(" ", topSentences);
            
            // Generate message based on text length
            String message = generateMessage(text.length(), sentences.length);
            
            return new SummaryResult(
                text,
                summary,
                (double) topSentences.size() / sentences.length,
                message
            );
            
        } catch (Exception e) {
            logger.error("Summarization failed", e);
            return new SummaryResult(
                text,
                "Failed to generate summary: " + e.getMessage(),
                0.0,
                "Error during summarization 😅"
            );
        }
    }
    
    private Map<String, Double> calculateSentenceScores(String[] sentences) {
        // Create word frequency map
        Map<String, Integer> wordFreq = new HashMap<>();
        for (String sentence : sentences) {
            String[] words = sentence.toLowerCase().split("\\W+");
            for (String word : words) {
                if (isSignificantWord(word)) {
                    wordFreq.merge(word, 1, Integer::sum);
                }
            }
        }
        
        // Score sentences based on word frequency
        Map<String, Double> sentenceScores = new HashMap<>();
        for (String sentence : sentences) {
            String[] words = sentence.toLowerCase().split("\\W+");
            double score = Arrays.stream(words)
                .filter(this::isSignificantWord)
                .mapToDouble(word -> wordFreq.getOrDefault(word, 0))
                .sum() / words.length;
            sentenceScores.put(sentence, score);
        }
        
        return sentenceScores;
    }
    
    private List<String> selectTopSentences(String[] sentences,
                                          Map<String, Double> sentenceScores,
                                          int numSentences) {
        return Arrays.stream(sentences)
            .sorted((s1, s2) -> Double.compare(
                sentenceScores.getOrDefault(s2, 0.0),
                sentenceScores.getOrDefault(s1, 0.0)
            ))
            .limit(numSentences)
            .collect(Collectors.toList());
    }
    
    private boolean isSignificantWord(String word) {
        // Filter out common stop words
        Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "but", "in", "on", "at", "to");
        return word.length() > 2 && !stopWords.contains(word);
    }
    
    private String generateMessage(int textLength, int sentenceCount) {
        if (textLength > 1000 || sentenceCount > 10) {
            return coffeeBreakMessages[random.nextInt(coffeeBreakMessages.length)];
        }
        return "Summary generated successfully! 📝";
    }
    
    @Override
    public void cleanup() {
        sentenceDetector = null;
        initialized = false;
        logger.info("TextSummarizer cleaned up");
    }
    
    @Override
    public String getTaskName() {
        return "Text Summarizer";
    }
    
    @Override
    public String getTaskDescription() {
        return "Summarizes long texts with occasional coffee break suggestions";
    }
} 