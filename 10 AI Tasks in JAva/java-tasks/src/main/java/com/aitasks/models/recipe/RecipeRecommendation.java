package com.aitasks.models.recipe;

import java.util.Set;

public class RecipeRecommendation {
    private final Recipe recipe;
    private final double matchScore;
    private final Set<String> missingIngredients;
    private final String funnyMessage;
    
    public RecipeRecommendation(Recipe recipe, double matchScore, 
                               Set<String> missingIngredients, String funnyMessage) {
        this.recipe = recipe;
        this.matchScore = matchScore;
        this.missingIngredients = missingIngredients;
        this.funnyMessage = funnyMessage;
    }
    
    // Getters
    public Recipe getRecipe() { return recipe; }
    public double getMatchScore() { return matchScore; }
    public Set<String> getMissingIngredients() { return missingIngredients; }
    public String getFunnyMessage() { return funnyMessage; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("🍳 ").append(recipe.getName()).append("\n");
        sb.append("Match Score: ").append(String.format("%.1f%%", matchScore * 100)).append("\n");
        
        if (!missingIngredients.isEmpty()) {
            sb.append("Missing Ingredients: ").append(missingIngredients).append("\n");
            if (funnyMessage != null) {
                sb.append(funnyMessage).append("\n");
            }
        } else {
            sb.append("You have all ingredients! Let's cook! 🎉\n");
        }
        
        return sb.toString();
    }
} 