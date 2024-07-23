package com.hacklicious.pocketblackjack.service;

import com.hacklicious.pocketblackjack.enumeration.Decision;

import java.util.Random;

public class DecisionService {
    public Decision generateRiskDecision(double minimumPercentage) {
        double convertedMinimumPercentage = Math.floor(minimumPercentage * 100);

        Random random = new Random();
        int generatedPercentage = random.nextInt(100);

        if (generatedPercentage >= convertedMinimumPercentage) {
            return Decision.YES;
        }
        else {
            return Decision.NO;
        }
    }
}
