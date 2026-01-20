# ContinousDev - Face Recognition Application

A Spring Boot application featuring a face recognition system similar to the iPhone Photos app. Detect faces in images, register persons, and automatically classify and identify people in photos.

## 🎯 Features

### Face Recognition System
- **Face Detection**: Detect faces in uploaded images using OpenCV's Haar Cascade classifier
- **Person Registration**: Register new persons with their face images
- **Face Recognition**: Automatically identify and classify detected faces
- **Person Management**: 
  - Add multiple face samples for improved recognition accuracy
  - List all registered persons
  - Get person details
  - Delete persons and their data

### Technology Stack
- **Java 17**
- **Spring Boot 3.2.4** - REST API framework
- **OpenCV 4.7.0** - Computer vision and face detection
- **Deep Java Library (DJL) 0.31.1** - Machine learning infrastructure
- **Gradle** - Build automation
- **JUnit 5** - Testing framework

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Gradle (wrapper included)

### Building the Project

```bash
# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build the project
./gradlew build

# Run tests
./gradlew test
```

### Running the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/faces
```

### Endpoints

#### 1. Detect Faces
Detect and identify faces in an image.

```bash
POST /api/faces/detect
Content-Type: multipart/form-data

curl -X POST http://localhost:8080/api/faces/detect \
  -F "image=@path/to/photo.jpg"
```

**Response:**
```json
{
  "facesDetected": 2,
  "faces": [
    {
      "x": 100,
      "y": 150,
      "width": 200,
      "height": 200,
      "confidence": 1.0,
      "personName": "John Doe",
      "personId": "uuid-here"
    }
  ]
}
```

#### 2. Register Person
Register a new person with their face image.

```bash
POST /api/faces/register
Content-Type: multipart/form-data

curl -X POST http://localhost:8080/api/faces/register \
  -F "name=John Doe" \
  -F "image=@path/to/john.jpg"
```

**Response:**
```json
{
  "success": true,
  "personId": "uuid-here",
  "personName": "John Doe",
  "message": "Person registered successfully"
}
```

#### 3. Add Face for Person
Add additional face samples for an existing person.

```bash
POST /api/faces/{personId}/add
Content-Type: multipart/form-data

curl -X POST http://localhost:8080/api/faces/{personId}/add \
  -F "image=@path/to/another-photo.jpg"
```

#### 4. List All Persons
Get a list of all registered persons.

```bash
GET /api/faces/persons

curl http://localhost:8080/api/faces/persons
```

**Response:**
```json
{
  "totalPersons": 5,
  "persons": [
    {
      "id": "uuid-here",
      "name": "John Doe",
      "faceCount": 3
    }
  ]
}
```

#### 5. Get Person Details
Get details of a specific person.

```bash
GET /api/faces/persons/{personId}

curl http://localhost:8080/api/faces/persons/{personId}
```

#### 6. Delete Person
Delete a person and their face data.

```bash
DELETE /api/faces/persons/{personId}

curl -X DELETE http://localhost:8080/api/faces/persons/{personId}
```

## 🧪 Testing

The project includes comprehensive test coverage:

- **Service Layer Tests**: Face detection, registration, and person management
- **Controller Layer Tests**: All API endpoints
- **16 tests total**, all passing

Run tests with:
```bash
./gradlew test
```

## 🔒 Security

- Uses patched DJL version (0.31.1) to fix path traversal vulnerabilities
- Proper input validation and error handling
- CodeQL security analysis: **0 vulnerabilities found**
- No hardcoded secrets or sensitive data

## 📖 How It Works

1. **Face Detection**: Uses OpenCV's Haar Cascade classifier to detect face regions in images
2. **Feature Extraction**: Extracts normalized feature vectors from detected face regions
3. **Face Matching**: Compares new faces with stored face encodings using dot product similarity (cosine similarity on normalized vectors)
4. **Classification**: Identifies faces that match registered persons above a confidence threshold (60%)

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/heic/zxcv/
│   │   ├── ZxcvApplication.java           # Main application
│   │   ├── controller/
│   │   │   └── FaceRecognitionController.java  # REST API endpoints
│   │   ├── service/
│   │   │   └── FaceRecognitionService.java     # Face recognition logic
│   │   └── model/
│   │       ├── Person.java                 # Person data model
│   │       └── FaceDetectionResult.java    # Detection result model
│   └── resources/
│       ├── application.properties
│       └── haarcascade_frontalface_default.xml  # Face detection model
└── test/
    └── java/com/heic/zxcv/
        ├── controller/
        │   └── FaceRecognitionControllerTest.java
        └── service/
            └── FaceRecognitionServiceTest.java
```

## ⚠️ Limitations & Future Enhancements

### Current Limitations
- Face data stored in-memory (lost on restart)
- Simplified face encoding for demonstration
- Basic Haar Cascade face detection

### Planned Enhancements
- [ ] Persistent database storage
- [ ] Advanced deep learning face recognition models
- [ ] Face clustering for automatic person grouping
- [ ] Real-time video face detection
- [ ] Face verification (1:1 matching)
- [ ] Face image quality checks
- [ ] Batch processing support

## 📄 License

See [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Support

For detailed API documentation, see [FACE_RECOGNITION_README.md](FACE_RECOGNITION_README.md)

---

Built with ❤️ using Spring Boot, OpenCV, and Deep Java Library
