# Java AI Tasks - University Assignment

This project is developed as part of a university assignment focusing on implementing various AI and machine learning concepts using Java. The project demonstrates practical applications of computer vision, natural language processing, and machine learning algorithms.

## Features

1. **Face Detection with Fun Filters**
   - Detects faces in images using OpenCV
   - Adds fun accessories (sunglasses, mustache)
   - Real-time image processing

2. **Smart Text Translation**
   - Machine learning-based translation
   - Multiple language support
   - Fun robotic phrases added randomly

3. **Mood Analysis with GIFs**
   - Sentiment analysis of text
   - Matching GIF responses
   - Real-time mood detection

## Technologies Used

- Java 11
- OpenCV 4.6.0
- Deep Java Library (DJL)
- Apache OpenNLP
- Maven

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- OpenCV 4.6.0

### Installation

1. Clone the repository:
```bash
git clone https://github.com/kirikcialprsln/java-ai-tasks.git
```

2. Navigate to project directory:
```bash
cd java-ai-tasks
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
java -jar target/java-tasks-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── aitasks/
│               ├── detector/      # Face detection
│               ├── translator/    # Translation service
│               └── analyzer/      # Mood analysis
└── test/
    └── java/
        └── com/
            └── aitasks/
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

[Your Name]
[University Name]
[Course Name]
[Assignment Date] 
