import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class TextSummarizer {
    private SentenceDetectorME sentenceDetector;
    private final double summaryRatio = 0.3; // Summarize to 30% of original length
    private final String[] coffeeBreakMessages = {
        "Whew, that's a lot of words! ☕ Time for a coffee break!",
        "Hold on, let me grab my reading glasses... and a coffee! ☕",
        "This text is longer than my attention span! Coffee time! ☕",
        "Processing... *sips coffee* ... still processing... ☕",
        "Warning: Extra-long text detected! Engaging caffeine boost! ☕"
    };
    
    public TextSummarizer() {
        try {
            // Initialize OpenNLP sentence detector
            SentenceModel model = new SentenceModel(
                getClass().getResourceAsStream("/models/en-sent.bin"));
            sentenceDetector = new SentenceDetectorME(model);
        } catch (Exception e) {
            System.err.println("Failed to initialize summarizer: " + e.getMessage());
        }
    }
    
    public String summarize(String text) {
        try {
            // Check if text is too long
            if (text.length() > 1000) {
                System.out.println(getRandomCoffeeBreakMessage());
            }
            
            // Split into sentences
            String[] sentences = sentenceDetector.sentDetect(text);
            
            if (sentences.length <= 3) {
                return "Text is already concise! 👍\n" + text;
            }
            
            // Calculate sentence scores
            Map<String, Double> sentenceScores = calculateSentenceScores(sentences);
            
            // Select top sentences
            int numSentencesInSummary = Math.max(3, 
                (int) (sentences.length * summaryRatio));
            
            List<String> topSentences = sentenceScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(numSentencesInSummary)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            
            // Reorder sentences in original order
            List<String> orderedSummary = Arrays.stream(sentences)
                .filter(topSentences::contains)
                .collect(Collectors.toList());
            
            // Build summary with statistics
            StringBuilder summary = new StringBuilder();
            summary.append(String.format("📝 Summary (reduced by %d%%)\n\n",
                (int) ((1 - (double) orderedSummary.size() / sentences.length) * 100)));
            
            summary.append(String.join(" ", orderedSummary));
            
            // Add fun statistics
            summary.append("\n\n📊 Fun Stats:\n");
            summary.append(String.format("- Compressed %d sentences into %d\n",
                sentences.length, orderedSummary.size()));
            summary.append(String.format("- Saved you from reading %d words!\n",
                countWords(text) - countWords(String.join(" ", orderedSummary))));
            
            return summary.toString();
            
        } catch (Exception e) {
            return "Summarization failed: " + e.getMessage();
        }
    }
    
    private Map<String, Double> calculateSentenceScores(String[] sentences) {
        Map<String, Double> scores = new HashMap<>();
        Map<String, Integer> wordFrequencies = calculateWordFrequencies(sentences);
        
        for (String sentence : sentences) {
            double score = 0.0;
            List<String> words = tokenize(sentence);
            
            // Score based on word frequency
            for (String word : words) {
                score += wordFrequencies.getOrDefault(word.toLowerCase(), 0);
            }
            
            // Normalize by sentence length
            score = score / Math.max(1, words.size());
            
            // Bonus for sentences with numbers or quotes
            if (sentence.matches(".*\\d+.*")) score *= 1.2;
            if (sentence.contains("\"")) score *= 1.1;
            
            // Bonus for sentences starting with key phrases
            if (sentence.matches("(?i).*(in conclusion|therefore|finally|importantly).*")) {
                score *= 1.3;
            }
            
            scores.put(sentence, score);
        }
        
        return scores;
    }
    
    private Map<String, Integer> calculateWordFrequencies(String[] sentences) {
        Map<String, Integer> frequencies = new HashMap<>();
        
        for (String sentence : sentences) {
            List<String> words = tokenize(sentence);
            for (String word : words) {
                word = word.toLowerCase();
                if (isSignificantWord(word)) {
                    frequencies.merge(word, 1, Integer::sum);
                }
            }
        }
        
        return frequencies;
    }
    
    private List<String> tokenize(String text) {
        return Arrays.asList(text.split("\\s+"));
    }
    
    private boolean isSignificantWord(String word) {
        // Ignore common stop words
        Set<String> stopWords = Set.of("the", "a", "an", "and", "or", "but", "in",
            "on", "at", "to", "for", "is", "are", "was", "were");
        return !stopWords.contains(word.toLowerCase()) && word.length() > 2;
    }
    
    private int countWords(String text) {
        return text.split("\\s+").length;
    }
    
    private String getRandomCoffeeBreakMessage() {
        return coffeeBreakMessages[new Random().nextInt(coffeeBreakMessages.length)];
    }
} 