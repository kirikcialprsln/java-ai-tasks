import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;
import java.util.Random;

public class SpamClassifier {
    private Classifier classifier;
    private StringToWordVector filter;
    private Instances trainingHeader;
    private final String[] wittyResponses = {
        "Sausage inbound! 🌭",
        "Spam alert! Time to get the spam filter cooking! 🍖",
        "This message smells... like processed meat! 🥓",
        "Beep boop - spam detected in the wild! 🤖"
    };
    
    public SpamClassifier() {
        try {
            initializeClassifier();
            trainClassifier();
        } catch (Exception e) {
            System.err.println("Failed to initialize classifier: " + e.getMessage());
        }
    }
    
    private void initializeClassifier() throws Exception {
        // Create attributes (message content and class)
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("content", (ArrayList<String>) null));
        
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("ham");
        classValues.add("spam");
        attributes.add(new Attribute("@@class@@", classValues));
        
        // Create dataset structure
        trainingHeader = new Instances("MessageClassification", attributes, 0);
        trainingHeader.setClassIndex(1);
        
        // Initialize filter
        filter = new StringToWordVector();
        filter.setAttributeIndices("first");
        filter.setWordsToKeep(1000);
        filter.setLowerCaseTokens(true);
        
        // Initialize classifier
        classifier = new NaiveBayes();
    }
    
    private void trainClassifier() throws Exception {
        // Sample training data
        String[] hamExamples = {
            "Meeting at 3pm tomorrow",
            "Please review the attached document",
            "Happy birthday! Have a great day",
            "The project deadline is next week"
        };
        
        String[] spamExamples = {
            "CONGRATULATIONS! You've won $1,000,000",
            "Buy now! Limited time offer!!!",
            "Increase your followers instantly",
            "You are selected for a free iPhone"
        };
        
        // Create training instances
        Instances trainingData = new Instances(trainingHeader);
        
        // Add ham examples
        for (String text : hamExamples) {
            Instance instance = createInstance(text, "ham", trainingData);
            trainingData.add(instance);
        }
        
        // Add spam examples
        for (String text : spamExamples) {
            Instance instance = createInstance(text, "spam", trainingData);
            trainingData.add(instance);
        }
        
        // Apply filter
        filter.setInputFormat(trainingData);
        Instances filteredData = Filter.useFilter(trainingData, filter);
        
        // Train classifier
        classifier.buildClassifier(filteredData);
    }
    
    private Instance createInstance(String text, String classValue, Instances dataset) {
        Instance instance = new DenseInstance(2);
        instance.setDataset(dataset);
        instance.setValue(0, text);
        instance.setValue(1, classValue);
        return instance;
    }
    
    public String classifyMessage(String message) {
        try {
            // Create test instance
            Instance instance = createInstance(message, "ham", trainingHeader);
            
            // Filter instance
            Instances testData = new Instances(trainingHeader, 0);
            testData.add(instance);
            Instances filteredTest = Filter.useFilter(testData, filter);
            
            // Get prediction
            double prediction = classifier.classifyInstance(filteredTest.firstInstance());
            String classification = trainingHeader.classAttribute().value((int) prediction);
            
            if (classification.equals("spam")) {
                Random rand = new Random();
                return wittyResponses[rand.nextInt(wittyResponses.length)];
            } else {
                return "Looks like a legitimate message! 📬";
            }
            
        } catch (Exception e) {
            return "Classification failed: " + e.getMessage();
        }
    }
} 