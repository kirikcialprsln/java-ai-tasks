import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;

import java.util.*;

public class ChatBuddy implements AutoCloseable {
    private Predictor<String, String> chatModel;
    private HuggingFaceTokenizer tokenizer;
    private Random random;
    private int messageCount;
    private double userSentiment;
    private List<String> conversationHistory;
    private static final int HISTORY_SIZE = 5;
    private static final int TOPIC_CHANGE_THRESHOLD = 5;
    
    private final Map<Topic, String[]> topicResponses = new EnumMap<>(Topic.class);
    
    private enum Topic {
        PIZZA,
        MOVIES,
        TECH,
        PETS,
        WEATHER
    }
    
    public ChatBuddy() {
        try {
            System.out.println("Initializing ChatBuddy...");
            initializeModel();
            initializeTopics();
            random = new Random();
            messageCount = 0;
            userSentiment = 0.0;
            conversationHistory = new ArrayList<>();
            System.out.println("ChatBuddy is ready to chat!");
        } catch (Exception e) {
            System.err.println("Failed to initialize ChatBuddy: " + e.getMessage());
        }
    }
    
    private void initializeModel() throws Exception {
        // Initialize model and tokenizer
        Criteria<String, String> criteria = Criteria.builder()
            .setTypes(String.class, String.class)
            .optEngine("PyTorch")
            .optModelUrls("djl://ai.djl.huggingface.pytorch/microsoft/DialoGPT-small")
            .optProgress(new ProgressBar())
            .build();
        
        ZooModel<String, String> model = criteria.loadModel();
        chatModel = model.newPredictor();
        tokenizer = HuggingFaceTokenizer.newInstance("microsoft/DialoGPT-small");
    }
    
    private void initializeTopics() {
        // Pizza topics
        topicResponses.put(Topic.PIZZA, new String[]{
            "Speaking of which, what's your favorite pizza topping? 🍕",
            "You know what would be great right now? A pizza! 🍕",
            "This reminds me of that time I had pineapple on pizza... 🍍",
            "Did you know that the world's largest pizza was 122 feet wide? 🍕",
            "Let's talk about something important: Chicago vs New York style pizza! 🍕"
        });
        
        // Movie topics
        topicResponses.put(Topic.MOVIES, new String[]{
            "Have you seen any good movies lately? 🎬",
            "This conversation is better than the last Marvel movie! 🦸",
            "Plot twist! Speaking of those, what's your favorite movie twist? 🎭",
            "If this chat was a movie, what genre would it be? 🎥"
        });
        
        // Tech topics
        topicResponses.put(Topic.TECH, new String[]{
            "Did you hear about the latest AI developments? 🤖",
            "Sometimes I dream in binary... just kidding! 💻",
            "What's your take on quantum computing? 🔮",
            "Speaking of smart things, how smart is your home? 🏠"
        });
        
        // Pet topics
        topicResponses.put(Topic.PETS, new String[]{
            "Do you have any pets? I'm quite fond of robotic dogs! 🐕",
            "If you could have any animal as a pet, what would it be? 🐾",
            "Fun fact: cats spend 70% of their lives sleeping! 😺",
            "I once met a parrot who could code in Python! 🦜"
        });
        
        // Weather topics
        topicResponses.put(Topic.WEATHER, new String[]{
            "Perfect weather for chatting, isn't it? ⛅",
            "Is it just me, or is the weather getting more interesting? 🌈",
            "I heard it's raining somewhere in the world right now! 🌧",
            "What's your favorite season? I like all of them! 🌺"
        });
    }
    
    public String respond(String userInput) {
        try {
            messageCount++;
            updateConversationHistory(userInput);
            updateSentiment(userInput);
            
            // Check for topic change
            if (shouldChangeTopic()) {
                messageCount = 0;
                return getRandomTopicResponse();
            }
            
            // Generate base response
            String response = generateResponse(userInput);
            
            // Add personality and context
            response = addPersonality(response);
            response = addContextualResponse(response);
            
            return response;
        } catch (Exception e) {
            return "Oops, I got distracted! Can you repeat that? " + 
                   getRandomTopicResponse();
        }
    }
    
    private void updateConversationHistory(String input) {
        conversationHistory.add(input);
        if (conversationHistory.size() > HISTORY_SIZE) {
            conversationHistory.remove(0);
        }
    }
    
    private void updateSentiment(String input) {
        // Simple sentiment analysis based on keywords
        String lowercaseInput = input.toLowerCase();
        if (lowercaseInput.matches(".*(love|great|happy|awesome|excellent).*")) {
            userSentiment += 0.2;
        } else if (lowercaseInput.matches(".*(hate|bad|awful|terrible|worst).*")) {
            userSentiment -= 0.2;
        }
        userSentiment = Math.max(-1.0, Math.min(1.0, userSentiment));
    }
    
    private boolean shouldChangeTopic() {
        return messageCount >= TOPIC_CHANGE_THRESHOLD && 
               random.nextDouble() < 0.3;
    }
    
    private String getRandomTopicResponse() {
        Topic[] topics = Topic.values();
        Topic randomTopic = topics[random.nextInt(topics.length)];
        String[] responses = topicResponses.get(randomTopic);
        return responses[random.nextInt(responses.length)];
    }
    
    private String generateResponse(String userInput) throws Exception {
        // Consider conversation history for context
        String contextInput = String.join(" ", conversationHistory) + " " + userInput;
        return chatModel.predict(contextInput);
    }
    
    private String addPersonality(String response) {
        String[] emojis = {"😊", "🤔", "😄", "🤖", "✨", "💭", "💫", "🌟"};
        String[] expressions = {
            "Hmm... ", "Well... ", "Let me think... ", "Interesting! ",
            "Oh! ", "You know what? ", "Actually... ", "I wonder... "
        };
        
        // Add expression based on sentiment
        if (userSentiment > 0.5) {
            response = "I'm glad you're in a good mood! " + response;
        } else if (userSentiment < -0.5) {
            response = "I hope this cheers you up! " + response;
        }
        
        // Add random expression and emoji
        if (random.nextDouble() < 0.4) {
            response = expressions[random.nextInt(expressions.length)] + response;
        }
        if (random.nextDouble() < 0.5) {
            response += " " + emojis[random.nextInt(emojis.length)];
        }
        
        return response;
    }
    
    private String addContextualResponse(String response) {
        // Add references to previous conversation
        if (!conversationHistory.isEmpty() && random.nextDouble() < 0.2) {
            String previousTopic = conversationHistory.get(conversationHistory.size() - 1);
            response += "\nBy the way, about what you said regarding '" + 
                       previousTopic.substring(0, Math.min(20, previousTopic.length())) +
                       "...', that was interesting!";
        }
        return response;
    }
    
    @Override
    public void close() {
        try {
            if (chatModel != null) {
                chatModel.close();
            }
            if (tokenizer != null) {
                tokenizer.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
} 