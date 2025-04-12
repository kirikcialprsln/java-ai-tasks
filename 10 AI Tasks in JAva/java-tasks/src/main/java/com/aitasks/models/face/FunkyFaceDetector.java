package com.aitasks.models.face;

import com.aitasks.core.BaseAITask;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;
import java.util.List;

public class FunkyFaceDetector extends BaseAITask {
    private CascadeClassifier faceDetector;
    private Mat sunglasses;
    private Mat mustache;
    private boolean initialized = false;

    @Override
    public void initialize() throws Exception {
        logger.info("Initializing FunkyFaceDetector...");
        try {
            // Load OpenCV native library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            
            // Initialize face detector
            String cascadePath = config.getProperty("model.face.cascade");
            faceDetector = new CascadeClassifier(cascadePath);
            
            // Load accessories
            String sunglassesPath = config.getProperty("image.accessories.sunglasses");
            String mustachePath = config.getProperty("image.accessories.mustache");
            
            sunglasses = Imgcodecs.imread(sunglassesPath, Imgcodecs.IMREAD_UNCHANGED);
            mustache = Imgcodecs.imread(mustachePath, Imgcodecs.IMREAD_UNCHANGED);
            
            if (faceDetector.empty()) {
                throw new Exception("Failed to load face cascade classifier");
            }
            
            initialized = true;
            logger.info("FunkyFaceDetector initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize FunkyFaceDetector", e);
            throw e;
        }
    }

    public FaceDetectionResult detectAndDecorate(String imagePath) {
        checkInitialized();
        try {
            Mat image = Imgcodecs.imread(imagePath);
            if (image.empty()) {
                throw new Exception("Failed to load image: " + imagePath);
            }

            // Detect faces
            MatOfRect faces = new MatOfRect();
            faceDetector.detectMultiScale(image, faces);
            
            List<FaceLocation> faceLocations = new ArrayList<>();
            
            // Add accessories to each face
            for (Rect face : faces.toArray()) {
                addAccessories(image, face);
                faceLocations.add(new FaceLocation(face.x, face.y, face.width, face.height));
            }

            // Save result
            String outputPath = generateOutputPath(imagePath);
            Imgcodecs.imwrite(outputPath, image);

            return new FaceDetectionResult(
                faceLocations.size(),
                faceLocations,
                outputPath,
                "Found " + faceLocations.size() + " faces and made them funky! 😎"
            );

        } catch (Exception e) {
            logger.error("Face detection failed", e);
            return new FaceDetectionResult(
                0,
                new ArrayList<>(),
                null,
                "Failed to process image: " + e.getMessage() + " 😢"
            );
        }
    }

    private void addAccessories(Mat image, Rect face) {
        // Add sunglasses
        double eyeRegionTop = face.y + (face.height * 0.2);
        double eyeRegionHeight = face.height * 0.3;
        addAccessory(image, sunglasses, face, eyeRegionTop, eyeRegionHeight);

        // Add mustache
        double mustacheRegionTop = face.y + (face.height * 0.6);
        double mustacheRegionHeight = face.height * 0.2;
        addAccessory(image, mustache, face, mustacheRegionTop, mustacheRegionHeight);
    }

    private void addAccessory(Mat image, Mat accessory, Rect face, 
                            double regionTop, double regionHeight) {
        // Calculate accessory size and position
        int accessoryWidth = (int)(face.width * 0.8);
        int accessoryHeight = (int)(accessoryWidth * accessory.rows() / accessory.cols());
        
        Point position = new Point(
            face.x + (face.width - accessoryWidth) / 2,
            regionTop
        );

        // Resize accessory
        Mat resizedAccessory = new Mat();
        Size size = new Size(accessoryWidth, accessoryHeight);
        Imgproc.resize(accessory, resizedAccessory, size);

        // Overlay accessory with alpha blending
        overlayImage(image, resizedAccessory, position);
    }

    private void overlayImage(Mat background, Mat foreground, Point location) {
        for (int y = 0; y < foreground.rows(); y++) {
            for (int x = 0; x < foreground.cols(); x++) {
                double[] fgColor = foreground.get(y, x);
                if (fgColor[3] > 0) { // If pixel is not transparent
                    double alpha = fgColor[3] / 255.0;
                    double[] bgColor = background.get(
                        (int)location.y + y, 
                        (int)location.x + x
                    );
                    for (int c = 0; c < 3; c++) {
                        bgColor[c] = bgColor[c] * (1 - alpha) + fgColor[c] * alpha;
                    }
                    background.put((int)location.y + y, (int)location.x + x, bgColor);
                }
            }
        }
    }

    private String generateOutputPath(String inputPath) {
        return inputPath.replaceAll("\\.(?=[^\\.]+$)", "_funky.");
    }

    @Override
    public void cleanup() {
        if (sunglasses != null) sunglasses.release();
        if (mustache != null) mustache.release();
        initialized = false;
        logger.info("FunkyFaceDetector cleaned up");
    }

    @Override
    public String getTaskName() {
        return "Funky Face Detector";
    }

    @Override
    public String getTaskDescription() {
        return "Detects faces and adds funky accessories like sunglasses and mustaches";
    }
} 