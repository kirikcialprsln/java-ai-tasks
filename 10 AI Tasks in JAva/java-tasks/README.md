# Java AI Tasks Collection - University Assignment

This project is developed as part of a university assignment focusing on implementing various AI and machine learning concepts using Java. The project demonstrates practical applications of computer vision, natural language processing, and machine learning algorithms.

## Project Overview

This assignment showcases the implementation of three main AI-powered applications:

1. **FunkyFaceDetector**: A computer vision application that detects faces in images and adds decorative elements
2. **RoboTranslator**: A natural language processing application for text translation
3. **MoodDetector**: A sentiment analysis tool that analyzes text to determine emotional content

## Technical Requirements

- Java 11 or higher
- Maven 3.6 or higher
- OpenCV 4.6.0
- Basic understanding of:
  - Computer Vision concepts
  - Natural Language Processing
  - Machine Learning fundamentals

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── aitasks/
│               ├── detector/      # Face detection implementation
│               ├── translator/    # Translation service
│               └── analyzer/      # Mood analysis
└── test/
    └── java/
        └── com/
            └── aitasks/
```

## Implementation Details

### FunkyFaceDetector
- Uses OpenCV for face detection
- Implements image processing techniques
- Adds decorative elements to detected faces

### RoboTranslator
- Implements machine translation algorithms
- Uses Deep Java Library (DJL) for NLP tasks
- Supports multiple language pairs

### MoodDetector
- Utilizes sentiment analysis techniques
- Processes text using natural language processing
- Provides emotional context analysis

## Dependencies

- OpenCV 4.6.0 - For computer vision tasks
- Deep Java Library (DJL) 0.20.0 - For deep learning implementations
- Apache OpenNLP 2.1.0 - For natural language processing
- Weka 3.8.6 - For machine learning algorithms
- Deeplearning4j - For neural network implementations
- TarsosDSP - For audio processing capabilities
- JavaFX - For user interface components

## Building and Running

1. Clone the repository:
```bash
git clone https://github.com/yourusername/java-ai-tasks.git
cd java-ai-tasks
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
java -jar target/java-tasks-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Learning Outcomes

This assignment helps students understand:
- Implementation of AI algorithms in Java
- Integration of various AI libraries and frameworks
- Practical applications of computer vision and NLP
- Software engineering best practices
- Project organization and documentation

## Academic Context

This project is developed as part of the university curriculum to demonstrate practical understanding of:
- Artificial Intelligence concepts
- Java programming
- Software development methodologies
- Documentation and code organization
- Version control systems

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

[Your Name]
[University Name]
[Course Name]
[Assignment Date] 