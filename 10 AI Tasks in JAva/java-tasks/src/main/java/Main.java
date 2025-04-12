import java.util.*;

public class Main {
    public static void main(String[] args) {
        // ... existing test code ...
        
        // Test RecipeRecommender
        System.out.println("\nTesting Recipe Recommender:");
        RecipeRecommender recommender = new RecipeRecommender();
        
        // Test with some available ingredients
        Set<String> availableIngredients = new HashSet<>(Arrays.asList(
            "pasta", "tomato", "garlic", "flour", "milk", "egg"
        ));
        
        System.out.println("Available ingredients: " + availableIngredients);
        List<RecipeRecommender.RecipeRecommendation> recommendations = 
            recommender.recommendRecipes(availableIngredients);
        
        for (RecipeRecommender.RecipeRecommendation recommendation : recommendations) {
            System.out.println("\nRecipe: " + recommendation.getRecipe().getName());
            System.out.printf("Match Score: %.2f\n", recommendation.getMatchScore());
            
            if (!recommendation.getMissingIngredients().isEmpty()) {
                System.out.println("Missing ingredients: " + recommendation.getMissingIngredients());
                if (recommendation.getFunnyMessage() != null) {
                    System.out.println(recommendation.getFunnyMessage());
                }
            }
        }
        
        // Test Neural Style Transfer
        System.out.println("\nTesting Neural Style Transfer:");
        NeuralStyleTransfer styleTransfer = new NeuralStyleTransfer();
        
        String contentImage = "src/main/resources/images/content.jpg";
        String styleImage = "src/main/resources/images/style.jpg";
        String outputImage = "output_styled.png";
        
        System.out.println("Applying style transfer...");
        styleTransfer.transferStyle(contentImage, styleImage, outputImage);
        
        // Test ChatBuddy
        System.out.println("\nTesting ChatBuddy:");
        try (ChatBuddy chatBuddy = new ChatBuddy();
             Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("Chat with me! (type 'exit' to end)");
            
            while (true) {
                System.out.print("\nYou: ");
                String userInput = scanner.nextLine();
                
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
                
                String response = chatBuddy.respond(userInput);
                System.out.println("ChatBuddy: " + response);
            }
        }
        
        // Test AudioMoodLight
        System.out.println("\nTesting Audio Mood Light:");
        try (AudioMoodLight moodLight = new AudioMoodLight();
             Scanner scanner = new Scanner(System.in)) {
            
            moodLight.start();
            System.out.println("Audio Mood Light is running! Press Enter to stop...");
            scanner.nextLine();
        }
        
        // Test TextSummarizer
        System.out.println("\nTesting Text Summarizer:");
        TextSummarizer summarizer = new TextSummarizer();
        
        String longText = "Artificial Intelligence (AI) has revolutionized the way we live and work. " +
            "Machine learning algorithms can now recognize patterns in vast amounts of data. " +
            "Deep learning, a subset of AI, has shown remarkable results in image and speech recognition. " +
            "Natural Language Processing has enabled machines to understand and generate human language. " +
            "The future of AI looks promising, with potential applications in healthcare and education. " +
            "However, there are also concerns about AI's impact on employment and privacy. " +
            "Researchers are working to address these ethical challenges. " +
            "In conclusion, AI will continue to shape our future in profound ways.";
        
        System.out.println("Original text:\n" + longText + "\n");
        System.out.println("Summary:\n" + summarizer.summarize(longText));
        
        // Test StockMarketFortuneTeller
        System.out.println("\nTesting Stock Market Fortune Teller:");
        StockMarketFortuneTeller fortuneTeller = new StockMarketFortuneTeller();
        
        // Sample historical data
        double[] historicalPrices = {
            100.0, 102.5, 101.8, 103.2, 105.0, 104.8, 106.3, 105.9, 107.2, 106.8,
            108.5, 109.2, 110.5, 109.8, 111.2
        };
        
        System.out.println("Training the model with historical data...");
        fortuneTeller.train(historicalPrices);
        
        // Get the last 10 prices for prediction
        double[] recentPrices = Arrays.copyOfRange(historicalPrices, 
            historicalPrices.length - 10, historicalPrices.length);
        
        System.out.println("\nPredicting next price...");
        StockMarketFortuneTeller.PricePrediction prediction = 
            fortuneTeller.predictNextPrice(recentPrices);
        
        System.out.println(prediction);
    }
} 