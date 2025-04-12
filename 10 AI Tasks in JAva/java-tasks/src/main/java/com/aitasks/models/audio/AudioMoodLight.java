package com.aitasks.models.audio;

import com.aitasks.core.BaseAITask;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

import javax.sound.sampled.*;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;

public class AudioMoodLight extends BaseAITask {
    private AudioDispatcher dispatcher;
    private TargetDataLine line;
    private Thread audioThread;
    private final AtomicReference<AudioMood> currentMood;
    
    public AudioMoodLight() {
        super();
        this.currentMood = new AtomicReference<>(AudioMood.NEUTRAL);
    }
    
    @Override
    public void initialize() throws Exception {
        logger.info("Initializing AudioMoodLight...");
        try {
            // Setup audio format
            float sampleRate = Float.parseFloat(config.getProperty("audio.sample.rate", "44100"));
            int bufferSize = Integer.parseInt(config.getProperty("audio.buffer.size", "1024"));
            
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                throw new LineUnavailableException("Line not supported");
            }
            
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            
            // Create audio stream
            AudioInputStream audioStream = new AudioInputStream(line);
            JVMAudioInputStream audioIn = new JVMAudioInputStream(audioStream);
            
            // Setup dispatcher
            dispatcher = new AudioDispatcher(audioIn, bufferSize, 0);
            setupAudioProcessing(dispatcher);
            
            // Start processing in separate thread
            audioThread = new Thread(dispatcher, "Audio Processing Thread");
            audioThread.start();
            
            initialized = true;
            logger.info("AudioMoodLight initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize AudioMoodLight", e);
            throw e;
        }
    }
    
    private void setupAudioProcessing(AudioDispatcher dispatcher) {
        // Add pitch detection
        dispatcher.addAudioProcessor(new PitchProcessor(
            PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
            44100, 1024, new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult result, AudioEvent event) {
                    processPitch(result.getPitch(), event.getRMS());
                }
            }));
    }
    
    private void processPitch(float pitch, float amplitude) {
        // Analyze pitch and amplitude to determine mood
        AudioMood newMood;
        
        if (pitch > 300 && amplitude > 0.2) {
            newMood = AudioMood.EXCITED;
        } else if (pitch > 200 && amplitude > 0.1) {
            newMood = AudioMood.HAPPY;
        } else if (pitch < 150 && amplitude > 0.15) {
            newMood = AudioMood.ANGRY;
        } else if (amplitude < 0.05) {
            newMood = AudioMood.CALM;
        } else {
            newMood = AudioMood.NEUTRAL;
        }
        
        currentMood.set(newMood);
    }
    
    public MoodLightResult getCurrentMood() {
        checkInitialized();
        AudioMood mood = currentMood.get();
        Color color = mood.getColor();
        
        return new MoodLightResult(
            mood,
            color,
            String.format("Mood is %s! Setting lights to RGB(%d, %d, %d) %s",
                mood.name(),
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                mood.getEmoji()
            )
        );
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
            line.stop();
            line.close();
        }
        initialized = false;
        logger.info("AudioMoodLight cleaned up");
    }
    
    @Override
    public String getTaskName() {
        return "Audio Mood Light";
    }
    
    @Override
    public String getTaskDescription() {
        return "Analyzes audio to detect mood and suggest matching colors";
    }
} 