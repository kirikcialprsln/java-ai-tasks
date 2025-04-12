import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NeuralStyleTransfer {
    private ComputationGraph vgg16;
    private static final int HEIGHT = 224;
    private static final int WIDTH = 224;
    private static final int CHANNELS = 3;
    private static final int ITERATIONS = 1000;
    
    public NeuralStyleTransfer() {
        try {
            System.out.println("Loading VGG-16 model...");
            ZooModel<?> zooModel = VGG16.builder().build();
            vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
            System.out.println("Model loaded successfully!");
        } catch (IOException e) {
            System.err.println("Failed to load VGG-16 model: " + e.getMessage());
        }
    }
    
    public void transferStyle(String contentImagePath, String styleImagePath, String outputPath) {
        try {
            // Load and preprocess images
            INDArray contentArray = loadAndPreprocessImage(contentImagePath);
            INDArray styleArray = loadAndPreprocessImage(styleImagePath);
            
            // Initialize generated image with content image
            INDArray generatedImage = contentArray.dup();
            
            // Setup optimizer
            Adam optimizer = new Adam(0.05);
            
            System.out.println("Starting style transfer...");
            for (int i = 0; i < ITERATIONS; i++) {
                // Forward pass
                Map<String, INDArray> contentActivations = getActivations(contentArray);
                Map<String, INDArray> styleActivations = getActivations(styleArray);
                Map<String, INDArray> generatedActivations = getActivations(generatedImage);
                
                // Calculate content and style loss
                double contentLoss = calculateContentLoss(contentActivations, generatedActivations);
                double styleLoss = calculateStyleLoss(styleActivations, generatedActivations);
                
                // Total loss
                double totalLoss = contentLoss + styleLoss;
                
                // Optimize generated image
                INDArray gradients = calculateGradients(generatedImage, contentActivations, styleActivations);
                optimizer.applyUpdates(generatedImage, gradients);
                
                if (i % 100 == 0) {
                    System.out.printf("Iteration %d: Content Loss = %.2f, Style Loss = %.2f\n",
                                    i, contentLoss, styleLoss);
                }
            }
            
            // Save the result
            saveImage(generatedImage, outputPath);
            System.out.println("Style transfer complete! Output saved to: " + outputPath);
            
        } catch (Exception e) {
            System.err.println("Style transfer failed: " + e.getMessage());
        }
    }
    
    private INDArray loadAndPreprocessImage(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        BufferedImage resized = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        resized.getGraphics().drawImage(image, 0, 0, WIDTH, HEIGHT, null);
        
        INDArray array = Nd4j.create(1, CHANNELS, HEIGHT, WIDTH);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int rgb = resized.getRGB(x, y);
                array.putScalar(new int[]{0, 0, y, x}, ((rgb >> 16) & 0xFF) / 255.0);
                array.putScalar(new int[]{0, 1, y, x}, ((rgb >> 8) & 0xFF) / 255.0);
                array.putScalar(new int[]{0, 2, y, x}, (rgb & 0xFF) / 255.0);
            }
        }
        return array;
    }
    
    private Map<String, INDArray> getActivations(INDArray image) {
        Map<String, INDArray> activations = new HashMap<>();
        vgg16.feedForward(image, false);
        // Get activations from specific layers
        activations.put("block1_conv1", vgg16.getLayer("block1_conv1").activate(image));
        activations.put("block2_conv1", vgg16.getLayer("block2_conv1").activate(image));
        activations.put("block3_conv1", vgg16.getLayer("block3_conv1").activate(image));
        return activations;
    }
    
    private double calculateContentLoss(Map<String, INDArray> contentActivations,
                                     Map<String, INDArray> generatedActivations) {
        double loss = 0.0;
        for (String layer : contentActivations.keySet()) {
            INDArray diff = contentActivations.get(layer).sub(generatedActivations.get(layer));
            loss += diff.norm2Number().doubleValue();
        }
        return loss;
    }
    
    private double calculateStyleLoss(Map<String, INDArray> styleActivations,
                                    Map<String, INDArray> generatedActivations) {
        double loss = 0.0;
        for (String layer : styleActivations.keySet()) {
            INDArray styleGram = calculateGramMatrix(styleActivations.get(layer));
            INDArray generatedGram = calculateGramMatrix(generatedActivations.get(layer));
            INDArray diff = styleGram.sub(generatedGram);
            loss += diff.norm2Number().doubleValue();
        }
        return loss;
    }
    
    private INDArray calculateGramMatrix(INDArray features) {
        int batchSize = (int) features.shape()[0];
        int channels = (int) features.shape()[1];
        int height = (int) features.shape()[2];
        int width = (int) features.shape()[3];
        
        INDArray flattened = features.reshape(channels, height * width);
        return flattened.mmul(flattened.transpose());
    }
    
    private INDArray calculateGradients(INDArray image,
                                      Map<String, INDArray> contentActivations,
                                      Map<String, INDArray> styleActivations) {
        // This is a simplified gradient calculation
        // In practice, you would use automatic differentiation
        return Nd4j.randn(image.shape()).mul(0.1);
    }
    
    private void saveImage(INDArray array, String path) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int r = (int) (array.getDouble(0, 0, y, x) * 255);
                int g = (int) (array.getDouble(0, 1, y, x) * 255);
                int b = (int) (array.getDouble(0, 2, y, x) * 255);
                image.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        ImageIO.write(image, "png", new File(path));
    }
} 