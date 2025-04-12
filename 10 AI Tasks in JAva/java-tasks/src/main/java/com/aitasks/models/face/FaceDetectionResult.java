package com.aitasks.models.face;

import java.util.List;

public class FaceDetectionResult {
    private final int faceCount;
    private final List<FaceLocation> faceLocations;
    private final String outputImagePath;
    private final String message;

    public FaceDetectionResult(int faceCount, List<FaceLocation> faceLocations, 
                             String outputImagePath, String message) {
        this.faceCount = faceCount;
        this.faceLocations = faceLocations;
        this.outputImagePath = outputImagePath;
        this.message = message;
    }

    // Getters
    public int getFaceCount() { return faceCount; }
    public List<FaceLocation> getFaceLocations() { return faceLocations; }
    public String getOutputImagePath() { return outputImagePath; }
    public String getMessage() { return message; }
} 