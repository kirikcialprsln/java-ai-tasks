package com.aitasks.models.stock;

import com.aitasks.core.BaseAITask;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.dataset.DataSet;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class StockMarketFortuneTeller extends BaseAITask {
    private MultiLayerNetwork model;
    private final Random random = new Random();
    private final StockDataNormalizer normalizer;
    
    private final String[] downTrendMessages = {
        "📉 Better luck tomorrow! Maybe try investing in chocolate? Always goes up in my heart!",
        "📉 Stocks are like my jokes - sometimes they fall flat!",
        "📉 Time to consider a career in comedy instead of trading!",
        "📉 Don't worry, my prediction accuracy is about as reliable as a chocolate teapot!",
        "📉 Looks like the market needs a hug... and maybe some coffee ☕"
    };
    
    private final String[] upTrendMessages = {
        "📈 To the moon! (Disclaimer: I failed astronomy)",
        "📈 Stonks only go up! (Sometimes... Maybe... Who knows?)",
        "📈 Time to buy that yacht! (A toy one, let's be realistic)",
        "📈 My circuits are tingling with bullish energy!",
        "📈 Warning: Excessive optimism detected! Side effects may include spontaneous dancing 💃"
    };
    
    public StockMarketFortuneTeller() {
        super();
        this.normalizer = new StockDataNormalizer();
    }
    
    @Override
    public void initialize() throws Exception {
        logger.info("Initializing StockMarketFortuneTeller...");
        try {
            // Configure LSTM network
            int inputSize = 5;  // OHLCV data
            int lstmLayer1Size = 50;
            int lstmLayer2Size = 25;
            
            model = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(123)
                .list()
                .layer(new LSTM.Builder()
                    .nIn(inputSize)
                    .nOut(lstmLayer1Size)
                    .build())
                .layer(new LSTM.Builder()
                    .nIn(lstmLayer1Size)
                    .nOut(lstmLayer2Size)
                    .build())
                .layer(new RnnOutputLayer.Builder()
                    .nIn(lstmLayer2Size)
                    .nOut(1)
                    .build())
                .build());
            
            model.init();
            
            // Load pre-trained weights if available
            loadPretrainedModel();
            
            initialized = true;
            logger.info("StockMarketFortuneTeller initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize StockMarketFortuneTeller", e);
            throw e;
        }
    }
    
    public PredictionResult predictNextDay(List<StockData> historicalData) {
        checkInitialized();
        try {
            // Prepare data
            INDArray input = prepareInputData(historicalData);
            
            // Make prediction
            INDArray prediction = model.output(input);
            double predictedPrice = normalizer.denormalize(prediction.getDouble(0));
            double lastPrice = historicalData.get(historicalData.size() - 1).getClose();
            
            // Calculate trend
            boolean isUptrend = predictedPrice > lastPrice;
            double changePercent = ((predictedPrice - lastPrice) / lastPrice) * 100;
            
            // Generate fortune message
            String message = generateFortuneMessage(isUptrend, changePercent);
            
            return new PredictionResult(
                lastPrice,
                predictedPrice,
                changePercent,
                isUptrend,
                message
            );
            
        } catch (Exception e) {
            logger.error("Prediction failed", e);
            return new PredictionResult(
                0.0,
                0.0,
                0.0,
                false,
                "Failed to predict future: Crystal ball needs debugging! 🔮"
            );
        }
    }
    
    private INDArray prepareInputData(List<StockData> data) {
        // Normalize and prepare data for LSTM
        List<Double> normalizedData = new ArrayList<>();
        for (StockData point : data) {
            normalizedData.add(normalizer.normalize(point.getClose()));
        }
        
        // Create input array
        INDArray input = Nd4j.create(normalizedData);
        return input.reshape(1, data.size(), 1);
    }
    
    private String generateFortuneMessage(boolean isUptrend, double changePercent) {
        String[] messages = isUptrend ? upTrendMessages : downTrendMessages;
        String message = messages[random.nextInt(messages.length)];
        
        // Add extra flair for significant changes
        if (Math.abs(changePercent) > 5.0) {
            message += "\n🎭 Plot twist: This is not financial advice!";
        }
        
        return message;
    }
    
    private void loadPretrainedModel() {
        // Load pre-trained weights (simplified)
        logger.info("Using default model weights");
    }
    
    @Override
    public void cleanup() {
        if (model != null) {
            model.clear();
        }
        initialized = false;
        logger.info("StockMarketFortuneTeller cleaned up");
    }
    
    @Override
    public String getTaskName() {
        return "Stock Market Fortune Teller";
    }
    
    @Override
    public String getTaskDescription() {
        return "Predicts stock prices with a dash of humor and questionable financial advice";
    }
} 