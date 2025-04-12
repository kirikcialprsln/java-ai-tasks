import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockMarketFortuneTeller {
    private MultiLayerNetwork model;
    private final int inputSize = 10; // Number of time steps to look back
    private final int hiddenSize = 32;
    private double lastPrediction = 0.0;
    
    private final String[] fortuneMessages = {
        "📈 To the moon! (Not financial advice) 🚀",
        "📉 Better luck tomorrow! Maybe try investing in coffee? ☕",
        "↗️ Stonks only go up! (Sometimes... maybe... who knows?) 🤷",
        "↘️ Time to check if McDonald's is hiring... Just kidding! 🍔",
        "➡️ As stable as my coffee addiction... Take that as you will! ☕"
    };
    
    public StockMarketFortuneTeller() {
        initializeModel();
    }
    
    private void initializeModel() {
        try {
            // Configure LSTM network
            NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .seed(123)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.01));
            
            NeuralNetConfiguration.ListBuilder listBuilder = builder.list()
                .layer(0, new LSTM.Builder()
                    .nIn(1)
                    .nOut(hiddenSize)
                    .activation(Activation.TANH)
                    .build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                    .activation(Activation.IDENTITY)
                    .nIn(hiddenSize)
                    .nOut(1)
                    .build());
            
            model = new MultiLayerNetwork(listBuilder.build());
            model.init();
            
            System.out.println("🔮 Crystal ball (I mean, LSTM) initialized!");
        } catch (Exception e) {
            System.err.println("Failed to initialize model: " + e.getMessage());
        }
    }
    
    public void train(double[] historicalPrices) {
        try {
            // Prepare training data
            List<INDArray> inputs = new ArrayList<>();
            List<INDArray> labels = new ArrayList<>();
            
            for (int i = 0; i < historicalPrices.length - inputSize; i++) {
                INDArray input = Nd4j.create(new int[]{1, 1, inputSize});
                for (int j = 0; j < inputSize; j++) {
                    input.putScalar(new int[]{0, 0, j}, historicalPrices[i + j]);
                }
                inputs.add(input);
                
                INDArray label = Nd4j.create(new int[]{1, 1, 1});
                label.putScalar(0, historicalPrices[i + inputSize]);
                labels.add(label);
            }
            
            // Train the model
            System.out.println("🧙‍♂️ Training the crystal ball...");
            for (int epoch = 0; epoch < 100; epoch++) {
                for (int i = 0; i < inputs.size(); i++) {
                    model.fit(inputs.get(i), labels.get(i));
                }
                if (epoch % 20 == 0) {
                    System.out.println("🔮 *mysterious swirling noises* " + (epoch + 20) + "% complete");
                }
            }
            
            System.out.println("✨ Training complete! The crystal ball is ready!");
            
        } catch (Exception e) {
            System.err.println("Training failed: " + e.getMessage());
        }
    }
    
    public PricePrediction predictNextPrice(double[] recentPrices) {
        try {
            // Prepare input data
            INDArray input = Nd4j.create(new int[]{1, 1, inputSize});
            for (int i = 0; i < inputSize; i++) {
                input.putScalar(new int[]{0, 0, i}, recentPrices[i]);
            }
            
            // Make prediction
            INDArray output = model.output(input);
            double predictedPrice = output.getDouble(0);
            lastPrediction = predictedPrice;
            
            // Calculate trend
            double currentPrice = recentPrices[recentPrices.length - 1];
            double priceChange = predictedPrice - currentPrice;
            double percentageChange = (priceChange / currentPrice) * 100;
            
            // Get fortune message
            String fortune = getFortuneMessage(percentageChange);
            
            return new PricePrediction(
                predictedPrice,
                percentageChange,
                fortune,
                LocalDateTime.now()
            );
            
        } catch (Exception e) {
            System.err.println("Prediction failed: " + e.getMessage());
            return null;
        }
    }
    
    private String getFortuneMessage(double percentageChange) {
        if (percentageChange > 5) {
            return fortuneMessages[0]; // To the moon!
        } else if (percentageChange < -5) {
            return fortuneMessages[1]; // Better luck tomorrow
        } else if (percentageChange > 0) {
            return fortuneMessages[2]; // Stonks only go up
        } else if (percentageChange < 0) {
            return fortuneMessages[3]; // McDonald's hiring
        } else {
            return fortuneMessages[4]; // Stable as coffee addiction
        }
    }
    
    public static class PricePrediction {
        private final double predictedPrice;
        private final double percentageChange;
        private final String fortune;
        private final LocalDateTime timestamp;
        
        public PricePrediction(double predictedPrice, double percentageChange, 
                             String fortune, LocalDateTime timestamp) {
            this.predictedPrice = predictedPrice;
            this.percentageChange = percentageChange;
            this.fortune = fortune;
            this.timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return String.format("""
                🔮 Stock Market Fortune Telling Results:
                📅 Time: %s
                💰 Predicted Price: $%.2f
                📊 Expected Change: %.2f%%
                🎯 Fortune: %s
                """,
                timestamp.format(formatter),
                predictedPrice,
                percentageChange,
                fortune
            );
        }
    }
} 