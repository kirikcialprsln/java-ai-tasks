package com.aitasks.models.translation;

import com.aitasks.core.BaseAITask;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;

import java.util.Random;

public class RoboTranslator extends BaseAITask {
    private Predictor<String, String> translator;
    private final Random random = new Random();
    private final SillyPhraseGenerator sillyPhraseGenerator;
    
    public RoboTranslator() {
        super();
        this.sillyPhraseGenerator = new SillyPhraseGenerator();
    }
    
    @Override
    public void initialize() throws Exception {
        logger.info("Initializing RoboTranslator...");
        try {
            Criteria<String, String> criteria = Criteria.builder()
                .setTypes(String.class, String.class)
                .optEngine("MXNet")
                .optModelUrls(config.getProperty("model.translator.path"))
                .build();

            ZooModel<String, String> model = criteria.loadModel();
            translator = model.newPredictor();
            initialized = true;
            logger.info("RoboTranslator initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize RoboTranslator", e);
            throw e;
        }
    }
    
    public TranslationResult translate(String text, String targetLanguage) {
        checkInitialized();
        try {
            String translation = translator.predict(text);
            
            // Add silly phrase if probability threshold is met
            double sillyProb = Double.parseDouble(
                config.getProperty("translator.silly.probability", "0.2"));
            if (random.nextDouble() < sillyProb) {
                translation += " " + sillyPhraseGenerator.getRandomPhrase();
            }
            
            return new TranslationResult(text, translation, targetLanguage);
        } catch (TranslateException e) {
            logger.error("Translation failed", e);
            return new TranslationResult(text, "Translation failed: " + e.getMessage(), targetLanguage);
        }
    }
    
    @Override
    public void cleanup() throws Exception {
        if (translator != null) {
            translator.close();
        }
        initialized = false;
        logger.info("RoboTranslator cleaned up");
    }
    
    @Override
    public String getTaskName() {
        return "Robo-Translator";
    }
    
    @Override
    public String getTaskDescription() {
        return "Translates text between languages with occasional silly phrases";
    }
} 