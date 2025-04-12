package com.aitasks.models.chat;

import com.aitasks.core.BaseAITask;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import java.util.Random;

public class ChatBuddy extends BaseAITask {
    private Predictor<String, String> chatModel;
    private final Random random = new Random();
    private final PizzaTopicGenerator pizzaGenerator;
    private int messageCount = 0;
    
    private static final int PIZZA_THRESHOLD = 5; // Messages before pizza topic change
    
    public ChatBuddy() {
        super();
        this.pizzaGenerator = new PizzaTopicGenerator();
    }
    
    @Override
    public void initialize() throws Exception {
        logger.info("Initializing ChatBuddy...");
        try {
            // Initialize DialoGPT model
            Criteria<String, String> criteria = Criteria.builder()
                .setTypes(String.class, String.class)
                .optEngine("PyTorch")
                .optModelUrls(config.getProperty("model.chat.path"))
                .build();
            
            ZooModel<String, String> model = criteria.loadModel();
            chatModel = model.newPredictor();
            
            initialized = true;
            logger.info("ChatBuddy initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize ChatBuddy", e);
            throw e;
        }
    }
    
    public ChatResponse chat(String userMessage) {
        checkInitialized();
        try {
            messageCount++;
            
            // Check if it's time to change topic to pizza
            if (shouldChangeToPizza()) {
                return new ChatResponse(
                    userMessage,
                    pizzaGenerator.generatePizzaQuestion(),
                    true,
                    "Suddenly craving pizza! 🍕"
                );
            }
            
            // Normal chat response
            String response = chatModel.predict(userMessage);
            return new ChatResponse(
                userMessage,
                response,
                false,
                null
            );
            
        } catch (Exception e) {
            logger.error("Chat failed", e);
            return new ChatResponse(
                userMessage,
                "Oops, my circuits are a bit tangled! 🤖",
                false,
                "Error: " + e.getMessage()
            );
        }
    }
    
    private boolean shouldChangeToPizza() {
        // Change topic to pizza randomly after PIZZA_THRESHOLD messages
        if (messageCount >= PIZZA_THRESHOLD) {
            double probability = Double.parseDouble(
                config.getProperty("chat.topic.change.probability", "0.3")
            );
            if (random.nextDouble() < probability) {
                messageCount = 0; // Reset counter
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void cleanup() {
        if (chatModel != null) {
            chatModel.close();
        }
        initialized = false;
        logger.info("ChatBuddy cleaned up");
    }
    
    @Override
    public String getTaskName() {
        return "Chat-Buddy";
    }
    
    @Override
    public String getTaskDescription() {
        return "Interactive chatbot that randomly changes topics to pizza";
    }
} 