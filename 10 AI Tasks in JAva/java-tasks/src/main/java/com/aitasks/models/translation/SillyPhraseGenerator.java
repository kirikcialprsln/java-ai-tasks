package com.aitasks.models.translation;

import java.util.Random;

public class SillyPhraseGenerator {
    private final String[] sillyPhrases = {
        "banana error!",
        "oops, my circuits!",
        "robot sneeze detected!",
        "beep boop translation!",
        "quantum translation fluctuation!"
    };
    private final Random random = new Random();
    
    public String getRandomPhrase() {
        return sillyPhrases[random.nextInt(sillyPhrases.length)];
    }
} 