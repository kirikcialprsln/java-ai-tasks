package com.aitasks.models.chat;

import java.util.Random;

public class PizzaTopicGenerator {
    private final Random random = new Random();
    
    private final String[] pizzaQuestions = {
        "Hey, speaking of that... what's your favorite pizza topping? 🍕",
        "That reminds me - pineapple on pizza, yay or nay? 🍍",
        "Random thought: thin crust or thick crust? 🥖",
        "Important question: what's the best pizza place in town? 🏪",
        "Let's settle this - is Chicago or New York style pizza better? 🗽",
        "Did you know that pizza was invented by... wait, who invented pizza? 🤔"
    };
    
    private final String[] pizzaFacts = {
        "Did you know that the world's largest pizza was 122 feet wide? 😮",
        "Fun fact: Americans eat 350 slices of pizza per second! 🍕",
        "Pizza fact: The first pizzeria in America opened in 1905 in NYC! 🗽",
        "Random pizza wisdom: Life is like pizza - even when it's bad, it's good! 😋"
    };
    
    public String generatePizzaQuestion() {
        if (random.nextBoolean()) {
            return pizzaQuestions[random.nextInt(pizzaQuestions.length)];
        } else {
            return pizzaFacts[random.nextInt(pizzaFacts.length)];
        }
    }
} 