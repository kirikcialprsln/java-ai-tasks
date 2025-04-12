package com.aitasks.models.recipe;

import java.util.List;

public class Recipe {
    private final String name;
    private final List<String> ingredients;
    private final String instructions;
    private final Cuisine cuisine;
    
    public Recipe(String name, List<String> ingredients, String instructions, Cuisine cuisine) {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.cuisine = cuisine;
    }
    
    // Getters
    public String getName() { return name; }
    public List<String> getIngredients() { return ingredients; }
    public String getInstructions() { return instructions; }
    public Cuisine getCuisine() { return cuisine; }
} 