import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

import javax.sound.sampled.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AudioMoodLight implements AutoCloseable {
    private AudioDispatcher dispatcher;
    private final AtomicBoolean isRunning;
    private final AtomicReference<MoodColor> currentMood;
    private Thread audioThread;
    
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 1024;
    private static final int OVERLAP = 0;
    
    public enum MoodColor {
        HAPPY(255, 223, 0, "Yellow - Happy and energetic! 🌟"),
        CALM(0, 150, 255, "Blue - Peaceful and serene... 🌊"),
        ANGRY(255, 0, 0, "Red - Intense and powerful! 🔥"),
        MELANCHOLIC(128, 0, 128, "Purple - Deep and emotional... 💜"),
        NEUTRAL(255, 255, 255, "White - Balanced and clear ✨");
        
        final int r, g, b;
        final String description;
        
        MoodColor(int r, int g, int b, String description) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.description = description;
        }
    }
    
    public AudioMoodLight() {
        isRunning = new AtomicBoolean(false);
        currentMood = new AtomicReference<>(MoodColor.NEUTRAL);
    }
    
    public void start() {
        try {
            // Set up audio input
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            
            if (!AudioSystem.isLineSupported(info)) {
                throw new LineUnavailableException("Line not supported");
            }
            
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            
            AudioInputStream audioStream = new AudioInputStream(line);
            JVMAudioInputStream audioInputStream = new JVMAudioInputStream(audioStream);
            
            // Create dispatcher
            dispatcher = new AudioDispatcher(audioInputStream, BUFFER_SIZE, OVERLAP);
            
            // Add pitch detection
            dispatcher.addAudioProcessor(new PitchProcessor(
                PitchEstimationAlgorithm.YIN, 
                SAMPLE_RATE, 
                BUFFER_SIZE, 
                new PitchDetectionHandler() {
                    @Override
                    public void handlePitch(PitchDetectionResult result, AudioEvent event) {
                        processPitch(result.getPitch(), event.getRMS());
                    }
                }
            ));
            
            // Add energy/volume processor
            dispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    processEnergy(audioEvent.getRMS());
                    return true;
                }
                
                @Override
                public void processingFinished() {}
            });
            
            // Start processing in separate thread
            isRunning.set(true);
            audioThread = new Thread(dispatcher, "Audio Processing Thread");
            audioThread.start();
            
            System.out.println("Audio Mood Light is listening! 🎵");
            
        } catch (Exception e) {
            System.err.println("Failed to start audio processing: " + e.getMessage());
        }
    }
    
    private void processPitch(float pitch, float rms) {
        if (pitch == -1 || rms < 0.01) return; // Skip silence
        
        // Analyze pitch and volume to determine mood
        MoodColor newMood;
        
        if (pitch > 300 && rms > 0.2) {
            newMood = MoodColor.HAPPY; // High pitch, loud = happy
        } else if (pitch > 200 && rms < 0.1) {
            newMood = MoodColor.CALM; // Medium pitch, soft = calm
        } else if (pitch < 150 && rms > 0.3) {
            newMood = MoodColor.ANGRY; // Low pitch, loud = angry
        } else if (pitch < 200 && rms < 0.15) {
            newMood = MoodColor.MELANCHOLIC; // Low-medium pitch, soft = melancholic
        } else {
            newMood = MoodColor.NEUTRAL;
        }
        
        if (currentMood.get() != newMood) {
            currentMood.set(newMood);
            System.out.println(newMood.description);
            updateLight(newMood);
        }
    }
    
    private void processEnergy(float rms) {
        // Additional energy-based processing could be added here
    }
    
    private void updateLight(MoodColor mood) {
        // In a real implementation, this would control actual RGB lights
        // For now, we'll just print the RGB values
        System.out.printf("Setting light to RGB(%d, %d, %d)\n", 
                         mood.r, mood.g, mood.b);
    }
    
    public MoodColor getCurrentMood() {
        return currentMood.get();
    }
    
    public void stop() {
        isRunning.set(false);
        if (dispatcher != null) {
            dispatcher.stop();
        }
        if (audioThread != null) {
            try {
                audioThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Override
    public void close() {
        stop();
    }
} 