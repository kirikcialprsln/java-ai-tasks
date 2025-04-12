package com.aitasks.detector;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * A class that detects faces in images and adds funky accessories like sunglasses and mustaches.
 * This implementation uses OpenCV for face detection and image processing.
 * 
 * @author [Your Name]
 * @version 1.0
 */
public class FunkyFaceDetector {
    private CascadeClassifier faceDetector;
    private Mat sunglasses;
    private Mat mustache;
    
    /**
     * Initializes the FunkyFaceDetector with necessary resources.
     * Loads OpenCV library, face detection model, and accessory images.
     */
    public FunkyFaceDetector() {
        // Load OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        // Initialize the face detector
        faceDetector = new CascadeClassifier();
        faceDetector.load("haarcascade_frontalface_default.xml");
        
        // Load accessories
        sunglasses = Imgcodecs.imread("resources/sunglasses.png", Imgcodecs.IMREAD_UNCHANGED);
        mustache = Imgcodecs.imread("resources/mustache.png", Imgcodecs.IMREAD_UNCHANGED);
    }
    
    /**
     * Processes an image by detecting faces and adding funky accessories.
     * 
     * @param inputImagePath Path to the input image
     * @param outputImagePath Path where the processed image will be saved
     */
    public void addFunkyAccessories(String inputImagePath, String outputImagePath) {
        Mat image = Imgcodecs.imread(inputImagePath);
        MatOfRect faceDetections = new MatOfRect();
        
        // Detect faces
        faceDetector.detectMultiScale(image, faceDetections);
        
        // Add accessories to each detected face
        for (Rect rect : faceDetections.toArray()) {
            // Add sunglasses
            addAccessory(image, sunglasses, rect, 0.3, 0.2);
            
            // Add mustache
            addAccessory(image, mustache, rect, 0.6, 0.3);
        }
        
        // Save the result
        Imgcodecs.imwrite(outputImagePath, image);
    }
    
    /**
     * Adds an accessory to a detected face in the image.
     * 
     * @param image The image to modify
     * @param accessory The accessory image to add
     * @param face The detected face rectangle
     * @param verticalPosition Vertical position of the accessory (0.0 to 1.0)
     * @param scale Scale factor for the accessory
     */
    private void addAccessory(Mat image, Mat accessory, Rect face, 
                            double verticalPosition, double scale) {
        // Calculate position and size for the accessory
        int accessoryWidth = (int) (face.width * scale);
        int accessoryHeight = accessoryWidth * accessory.rows() / accessory.cols();
        
        Point position = new Point(
            face.x + (face.width - accessoryWidth) / 2,
            face.y + face.height * verticalPosition
        );
        
        // Resize accessory
        Mat resizedAccessory = new Mat();
        Imgproc.resize(accessory, resizedAccessory, 
                      new Size(accessoryWidth, accessoryHeight));
        
        // Overlay accessory on the image
        overlayImage(image, resizedAccessory, position);
    }
    
    /**
     * Overlays a foreground image on a background image using alpha blending.
     * 
     * @param background The background image
     * @param foreground The foreground image to overlay
     * @param location The position where to overlay the foreground
     */
    private void overlayImage(Mat background, Mat foreground, Point location) {
        // Implementation of alpha blending for PNG overlay
        for (int y = 0; y < foreground.rows(); y++) {
            for (int x = 0; x < foreground.cols(); x++) {
                double[] bgColor = background.get((int)location.y + y, (int)location.x + x);
                double[] fgColor = foreground.get(y, x);
                
                if (fgColor[3] > 0) { // If not completely transparent
                    double alpha = fgColor[3] / 255.0;
                    for (int c = 0; c < 3; c++) {
                        bgColor[c] = bgColor[c] * (1 - alpha) + fgColor[c] * alpha;
                    }
                    background.put((int)location.y + y, (int)location.x + x, bgColor);
                }
            }
        }
    }
} 