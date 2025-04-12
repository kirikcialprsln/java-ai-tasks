public class NeuralStyleTransfer implements ResourceManager {
    private ComputationGraph vgg16;
    private boolean initialized = false;
    
    @Override
    public void initialize() throws Exception {
        try {
            System.out.println("Loading VGG-16 model...");
            ZooModel<?> zooModel = VGG16.builder().build();
            vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
            initialized = true;
        } catch (Exception e) {
            throw new Exception("Failed to initialize model: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public void cleanup() throws Exception {
        if (vgg16 != null) {
            vgg16.close();
        }
        initialized = false;
    }
    
    @Override
    public void close() throws Exception {
        cleanup();
    }
} 