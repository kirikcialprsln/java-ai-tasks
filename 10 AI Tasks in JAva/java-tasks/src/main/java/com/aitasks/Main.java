package com.aitasks;

import com.aitasks.detector.FunkyFaceDetector;
import com.aitasks.translator.RoboTranslator;
import com.aitasks.analyzer.MoodDetector;

/**
 * Main application class that demonstrates the functionality of various AI tasks.
 * This class serves as the entry point for the application and showcases
 * face detection, translation, and mood analysis capabilities.
 * 
 * @author [Your Name]
 * @version 1.0
 */
public class Main {
    /**
     * The main method that demonstrates the functionality of all AI components.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting AI Tasks Demo...\n");

        // Test FunkyFaceDetector
        System.out.println("Testing Face Detection and Decoration:");
        FunkyFaceDetector detector = new FunkyFaceDetector();
        detector.addFunkyAccessories("input.jpg", "output_funky.jpg");
        System.out.println("Face detection and decoration complete!\n");
        
        // Test RoboTranslator
        System.out.println("Testing Translation Service:");
        RoboTranslator translator = new RoboTranslator();
        String translation = translator.translate("Hello world!", true);
        System.out.println("Translation: " + translation + "\n");
        
        // Test MoodDetector
        System.out.println("Testing Mood Analysis:");
        MoodDetector moodDetector = new MoodDetector();
        String mood = moodDetector.analyzeMood("I'm having such a wonderful day!");
        System.out.println(mood + "\n");

        System.out.println("AI Tasks Demo completed successfully!");
    }
} 