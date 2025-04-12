package com.aitasks;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import com.aitasks.common.ResourceManager;

public class AudioMoodLight implements ResourceManager {
    private AudioDispatcher dispatcher;
    private TargetDataLine line;
    private Thread audioThread;
    private boolean initialized = false;
    
    @Override
    public void initialize() throws Exception {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                throw new LineUnavailableException("Line not supported");
            }
            
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            initialized = true;
        } catch (Exception e) {
            throw new Exception("Failed to initialize audio: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public void cleanup() throws Exception {
        if (dispatcher != null) {
            dispatcher.stop();
        }
        if (audioThread != null) {
            audioThread.interrupt();
            audioThread.join(1000);
        }
        if (line != null) {
            line.close();
        }
        initialized = false;
    }
    
    @Override
    public void close() throws Exception {
        cleanup();
    }
} 