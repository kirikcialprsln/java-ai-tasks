package com.aitasks.models.spam;

import com.aitasks.core.BaseAITask;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.*;

public class SpamClassifier extends BaseAITask {
    private Classifier classifier;
    private StringToWordVector filter;
    private Instances trainingHeader;
    private final Random random = new Random();
    
    private final String[] wittyResponses = {
        "🌭 Sausage inbound! Another spam caught in our gourmet filter!",
        "🥓 Mmm... Smells like processed meat... I mean, spam!",
        "🍖 This message is as suspicious as mystery meat!",
        "🌭 Hot dog! We've got ourselves some spam!",
        "🥪 This sandwich looks suspicious... definitely spam!"
    };
    
    @Override
    public void initialize() throws Exception {
        logger.info("Initializing SpamClassifier...");
        try {
            // Create attributes (message content and class)
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(new Attribute("content", (ArrayList<String>) null));
            
            ArrayList<String> classValues = new ArrayList<>();
            classValues.add("ham");
            classValues.add("spam");
            attributes.add(new Attribute("@@class@@", classValues));
            
            // Create dataset structure
            trainingHeader = new Instances("SpamClassification", attributes, 0);
            trainingHeader.setClassIndex(1);
            
            // Initialize filter
            filter = new StringToWordVector();
            filter.setAttributeIndices("first");
            filter.setWordsToKeep(1000);
            filter.setLowerCaseTokens(true);
            
            // Initialize classifier
            classifier = new NaiveBayes();
            
            // Train with initial data
            trainWithDefaultData();
            
            initialized = true;
            logger.info("SpamClassifier initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize SpamClassifier", e);
            throw e;
        }
    }
    
    private void trainWithDefaultData() throws Exception {
        // Sample training data
        List<EmailMessage> trainingData = new ArrayList<>();
        
        // Ham examples
        trainingData.add(new EmailMessage(
            "Meeting tomorrow at 2pm",
            EmailType.HAM
        ));
        trainingData.add(new EmailMessage(
            "Please review the attached document",
            EmailType.HAM
        ));
        trainingData.add(new EmailMessage(
            "Your quarterly report is ready",
            EmailType.HAM
        ));
        
        // Spam examples
        trainingData.add(new EmailMessage(
            "CONGRATULATIONS! You've won $1,000,000!!!",
            EmailType.SPAM
        ));
        trainingData.add(new EmailMessage(
            "Buy now! Limited time offer!!!",
            EmailType.SPAM
        ));
        trainingData.add(new EmailMessage(
            "You are selected for a free iPhone",
            EmailType.SPAM
        ));
        
        train(trainingData);
    }
    
    public void train(List<EmailMessage> trainingData) throws Exception {
        Instances trainingInstances = new Instances(trainingHeader);
        
        for (EmailMessage email : trainingData) {
            Instance instance = createInstance(email.getContent(), 
                email.getType().toString().toLowerCase(), trainingInstances);
            trainingInstances.add(instance);
        }
        
        // Apply filter
        filter.setInputFormat(trainingInstances);
        Instances filteredData = Filter.useFilter(trainingInstances, filter);
        
        // Train classifier
        classifier.buildClassifier(filteredData);
        logger.info("Classifier trained with {} instances", trainingData.size());
    }
    
    public ClassificationResult classify(String emailContent) {
        checkInitialized();
        try {
            // Create test instance
            Instance instance = createInstance(emailContent, "ham", trainingHeader);
            
            // Filter instance
            Instances testData = new Instances(trainingHeader, 0);
            testData.add(instance);
            Instances filteredTest = Filter.useFilter(testData, filter);
            
            // Get prediction
            double[] distribution = classifier.distributionForInstance(filteredTest.firstInstance());
            double spamProbability = distribution[1]; // Probability of being spam
            
            boolean isSpam = spamProbability > 0.5;
            String wittyResponse = isSpam ? getRandomWittyResponse() : "Looks legitimate! 📧";
            
            return new ClassificationResult(
                emailContent,
                isSpam,
                spamProbability,
                wittyResponse
            );
            
        } catch (Exception e) {
            logger.error("Classification failed", e);
            return new ClassificationResult(
                emailContent,
                false,
                0.0,
                "Classification failed: " + e.getMessage()
            );
        }
    }
    
    private Instance createInstance(String text, String classValue, Instances dataset) {
        Instance instance = new DenseInstance(2);
        instance.setDataset(dataset);
        instance.setValue(0, text);
        instance.setValue(1, classValue);
        return instance;
    }
    
    private String getRandomWittyResponse() {
        return wittyResponses[random.nextInt(wittyResponses.length)];
    }
    
    @Override
    public void cleanup() {
        initialized = false;
        logger.info("SpamClassifier cleaned up");
    }
    
    @Override
    public String getTaskName() {
        return "Spam or Sausage Classifier";
    }
    
    @Override
    public String getTaskDescription() {
        return "Classifies emails as spam or ham with witty sausage-themed responses";
    }
} 