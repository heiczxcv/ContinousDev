# Getting Started

## Face Recognition API

This application now includes a **Face Recognition System** similar to the iPhone Photos app!

For detailed documentation on the Face Recognition API, see [FACE_RECOGNITION_README.md](FACE_RECOGNITION_README.md)

### Quick Start

1. **Build the project:**
   ```bash
   ./gradlew build
   ```

2. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

3. **Access the API:**
   The application will start on `http://localhost:8080`

### Face Recognition Features

- **Detect faces** in uploaded images
- **Register persons** with their face samples
- **Recognize and classify** faces automatically
- **Manage persons** - add multiple face samples, list all, delete

### Example API Calls

**Detect faces in an image:**
```bash
curl -X POST http://localhost:8080/api/faces/detect -F "image=@photo.jpg"
```

**Register a new person:**
```bash
curl -X POST http://localhost:8080/api/faces/register \
  -F "name=John Doe" \
  -F "image=@john.jpg"
```

**List all registered persons:**
```bash
curl http://localhost:8080/api/faces/persons
```

See [FACE_RECOGNITION_README.md](FACE_RECOGNITION_README.md) for complete API documentation.

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.4/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.4/gradle-plugin/reference/html/#build-image)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

