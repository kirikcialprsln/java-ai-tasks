import java.util.*;

public class RecipeRecommender {
    private List<Recipe> recipes;
    private final String[] funnyMissingIngredientResponses = {
        "Uh-oh! We're out of %s. All hope is lost! 😱",
        "No %s? What kind of kitchen is this? 😅",
        "Missing %s... Time to raid the neighbor's pantry! (just kidding) 🏃",
        "Alert! Alert! %s not found! Culinary disaster incoming! 🚨"
    };

    public RecipeRecommender() {
        initializeRecipes();
    }

    private void initializeRecipes() {
        recipes = new ArrayList<>();
        
        // Add some sample recipes
        recipes.add(new Recipe(
            "Classic Spaghetti",
            Arrays.asList("pasta", "tomato", "garlic", "onion", "olive oil"),
            "1. Boil pasta\n2. Make sauce\n3. Combine and enjoy!"
        ));
        
        recipes.add(new Recipe(
            "Simple Pancakes",
            Arrays.asList("flour", "milk", "egg", "butter", "sugar"),
            "1. Mix ingredients\n2. Cook on griddle\n3. Serve with syrup!"
        ));
        
        recipes.add(new Recipe(
            "Chicken Stir-Fry",
            Arrays.asList("chicken", "rice", "soy sauce", "vegetables", "oil"),
            "1. Cook rice\n2. Stir-fry chicken and veggies\n3. Add sauce!"
        ));
        
        recipes.add(new Recipe(
            "Vegetable Soup",
            Arrays.asList("carrot", "potato", "onion", "celery", "broth"),
            "1. Chop vegetables\n2. Simmer in broth\n3. Season and serve!"
        ));
    }

    public List<RecipeRecommendation> recommendRecipes(Set<String> availableIngredients) {
        List<RecipeRecommendation> recommendations = new ArrayList<>();
        Random rand = new Random();

        for (Recipe recipe : recipes) {
            Set<String> missingIngredients = new HashSet<>(recipe.getIngredients());
            missingIngredients.removeAll(availableIngredients);
            
            double matchScore = 1.0 - ((double) missingIngredients.size() / recipe.getIngredients().size());
            
            String funnyMessage = null;
            if (!missingIngredients.isEmpty()) {
                String missingIngredient = missingIngredients.iterator().next();
                funnyMessage = String.format(
                    funnyMissingIngredientResponses[rand.nextInt(funnyMissingIngredientResponses.length)],
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

    // Inner classes
    public static class Recipe {
        private final String name;
        private final List<String> ingredients;
        private final String instructions;

        public Recipe(String name, List<String> ingredients, String instructions) {
            this.name = name;
            this.ingredients = ingredients;
            this.instructions = instructions;
        }

        public String getName() { return name; }
        public List<String> getIngredients() { return ingredients; }
        public String getInstructions() { return instructions; }
    }

    public static class RecipeRecommendation {
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

        public Recipe getRecipe() { return recipe; }
        public double getMatchScore() { return matchScore; }
        public Set<String> getMissingIngredients() { return missingIngredients; }
        public String getFunnyMessage() { return funnyMessage; }
    }
} 