package com.aitasks.models.style;

import com.aitasks.core.BaseAITask;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class NeuralStyleTransfer extends BaseAITask {
    private ComputationGraph vgg16;
    private final Random random = new Random();
    
    private final String[] artisticComments = {
        "🎨 Van Gogh would be proud... or confused!",
        "🖼️ Picasso just rolled over in his grave... with excitement!",
        "🎭 Is it art? Is it a bug? Who knows!",
        "🖌️ We're basically the next Renaissance... sort of!",
        "🎪 Warning: This art may cause spontaneous gallery openings!"
    };
    
    @Override
    public void initialize() throws Exception {
        logger.info("Initializing NeuralStyleTransfer...");
        try {
            // Load VGG16 model
            VGG16 zooModel = VGG16.builder().build();
            vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
            
            initialized = true;
            logger.info("NeuralStyleTransfer initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize NeuralStyleTransfer", e);
            throw e;
        }
    }
    
    public StyleTransferResult transferStyle(String contentPath, String stylePath, 
                                           int iterations) {
        checkInitialized();
        try {
            // Load and preprocess images
            INDArray content = loadAndPreprocessImage(contentPath);
            INDArray style = loadAndPreprocessImage(stylePath);
            
            // Initialize generated image with content
            INDArray generated = content.dup();
            
            // Style transfer parameters
            double contentWeight = 1e-3;
            double styleWeight = 1e-1;
            
            // Optimization loop
            for (int i = 0; i < iterations; i++) {
                // Forward pass
                INDArray[] contentFeatures = vgg16.feedForward(content, false);
                INDArray[] styleFeatures = vgg16.feedForward(style, false);
                INDArray[] genFeatures = vgg16.feedForward(generated, true);
                
                // Calculate content and style gradients
                INDArray totalGradient = calculateGradients(
                    contentFeatures, styleFeatures, genFeatures,
                    contentWeight, styleWeight
                );
                
                // Update generated image
                generated.subi(totalGradient.mul(0.01));
                
                if (i % 10 == 0) {
                    logger.info("Iteration {}/{}", i, iterations);
                }
            }
            
            // Save result
            String outputPath = generateOutputPath(contentPath);
            saveImage(generated, outputPath);
            
            return new StyleTransferResult(
                outputPath,
                getRandomArtisticComment(),
                true
            );
            
        } catch (Exception e) {
            logger.error("Style transfer failed", e);
            return new StyleTransferResult(
                null,
                "Failed to create masterpiece: " + e.getMessage(),
                false
            );
        }
    }
    
    private INDArray loadAndPreprocessImage(String path) throws Exception {
        BufferedImage image = ImageIO.read(new File(path));
        DataNormalization preprocessor = new VGG16ImagePreProcessor();
        INDArray array = imageToINDArray(image);
        preprocessor.transform(array);
        return array;
    }
    
    private INDArray imageToINDArray(BufferedImage image) {
        // Convert BufferedImage to INDArray (simplified)
        int[] shape = new int[] {1, 3, 224, 224};
        INDArray array = Nd4j.create(shape);
        // ... image conversion code ...
        return array;
    }
    
    private INDArray calculateGradients(INDArray[] contentFeatures, 
                                      INDArray[] styleFeatures,
                                      INDArray[] genFeatures,
                                      double contentWeight,
                                      double styleWeight) {
        // Simplified gradient calculation
        INDArray gradient = Nd4j.create(genFeatures[0].shape());
        // ... gradient calculation code ...
        return gradient;
    }
    
    private void saveImage(INDArray array, String path) throws Exception {
        // Convert INDArray to BufferedImage and save
        BufferedImage image = indArrayToImage(array);
        ImageIO.write(image, "png", new File(path));
    }
    
    private BufferedImage indArrayToImage(INDArray array) {
        // Convert INDArray to BufferedImage (simplified)
        return new BufferedImage(224, 224, BufferedImage.TYPE_INT_RGB);
    }
    
    private String generateOutputPath(String inputPath) {
        return inputPath.replaceAll("\\.(?=[^\\.]+$)", "_styled.");
    }
    
    private String getRandomArtisticComment() {
        return artisticComments[random.nextInt(artisticComments.length)];
    }
    
    @Override
    public void cleanup() {
        if (vgg16 != null) {
            vgg16.clear();
        }
        initialized = false;
        logger.info("NeuralStyleTransfer cleaned up");
    }
    
    @Override
    public String getTaskName() {
        return "Neural Style Transfer";
    }
    
    @Override
    public String getTaskDescription() {
        return "Applies artistic styles to images with a dash of humor";
    }
} 