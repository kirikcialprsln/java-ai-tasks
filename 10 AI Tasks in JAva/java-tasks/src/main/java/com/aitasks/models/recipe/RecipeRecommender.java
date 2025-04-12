package com.aitasks.models.recipe;

import com.aitasks.core.BaseAITask;
import java.util.*;

public class RecipeRecommender extends BaseAITask {
    private final List<Recipe> recipeDatabase;
    private final Random random = new Random();
    
    private final String[] missingIngredientResponses = {
        "Uh-oh! We're out of %s. All hope is lost! 😱",
        "No %s? What kind of kitchen is this? 😅",
        "Missing %s... Time to raid the neighbor's pantry! (just kidding) 🏃",
        "Alert! Alert! %s not found! Culinary disaster incoming! 🚨",
        "Without %s, this recipe is like a pizza without cheese! 🍕"
    };
    
    public RecipeRecommender() {
        super();
        this.recipeDatabase = new ArrayList<>();
    }
    
    @Override
    public void initialize() throws Exception {
        logger.info("Initializing RecipeRecommender...");
        try {
            // Load default recipes
            loadDefaultRecipes();
            initialized = true;
            logger.info("RecipeRecommender initialized with {} recipes", recipeDatabase.size());
        } catch (Exception e) {
            logger.error("Failed to initialize RecipeRecommender", e);
            throw e;
        }
    }
    
    private void loadDefaultRecipes() {
        // Add some sample recipes
        recipeDatabase.add(new Recipe(
            "Classic Spaghetti",
            Arrays.asList("pasta", "tomato", "garlic", "onion", "olive oil"),
            "1. Boil pasta\n2. Make sauce\n3. Combine and enjoy!",
            Cuisine.ITALIAN
        ));
        
        recipeDatabase.add(new Recipe(
            "Chocolate Chip Cookies",
            Arrays.asList("flour", "chocolate", "butter", "sugar", "eggs"),
            "1. Mix ingredients\n2. Bake\n3. Try not to eat them all at once!",
            Cuisine.DESSERT
        ));
        
        recipeDatabase.add(new Recipe(
            "Stir-Fry Vegetables",
            Arrays.asList("broccoli", "carrot", "soy sauce", "ginger", "garlic"),
            "1. Chop veggies\n2. Stir fry\n3. Season and serve",
            Cuisine.ASIAN
        ));
    }
    
    public List<RecipeRecommendation> recommendRecipes(Set<String> availableIngredients) {
        checkInitialized();
        List<RecipeRecommendation> recommendations = new ArrayList<>();
        
        for (Recipe recipe : recipeDatabase) {
            // Find missing ingredients
            Set<String> missingIngredients = new HashSet<>(recipe.getIngredients());
            missingIngredients.removeAll(availableIngredients);
            
            // Calculate match score
            double matchScore = calculateMatchScore(recipe, availableIngredients);
            
            // Generate funny message if ingredients are missing
            String funnyMessage = null;
            if (!missingIngredients.isEmpty()) {
                String missingIngredient = missingIngredients.iterator().next();
                funnyMessage = String.format(
                    missingIngredientResponses[random.nextInt(missingIngredientResponses.length)],
                    missingIngredient
                );
            }
            
            recommendations.add(new RecipeRecommendation(
                recipe,
                matchScore,
                missingIngredients,
                funnyMessage
            ));
        }
        
        // Sort by match score (descending)
        recommendations.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        
        return recommendations;
    }
    
    private double calculateMatchScore(Recipe recipe, Set<String> availableIngredients) {
        Set<String> requiredIngredients = new HashSet<>(recipe.getIngredients());
        int totalRequired = requiredIngredients.size();
        requiredIngredients.removeAll(availableIngredients);
        int missing = requiredIngredients.size();
        
        return 1.0 - ((double) missing / totalRequired);
    }
    
    public void addRecipe(Recipe recipe) {
        recipeDatabase.add(recipe);
        logger.info("Added new recipe: {}", recipe.getName());
    }
    
    @Override
    public void cleanup() {
        recipeDatabase.clear();
        initialized = false;
        logger.info("RecipeRecommender cleaned up");
    }
    
    @Override
    public String getTaskName() {
        return "Recipe Recommender";
    }
    
    @Override
    public String getTaskDescription() {
        return "Suggests recipes based on available ingredients with humorous feedback";
    }
} 