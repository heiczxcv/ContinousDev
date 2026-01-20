# Face Recognition API

A face recognition service similar to iPhone Photos app that can detect faces in images and classify them by person.

## Features

- **Face Detection**: Detect faces in uploaded images
- **Person Registration**: Register new persons with their face images
- **Face Recognition**: Automatically identify and classify detected faces
- **Person Management**: Add multiple face samples for a person, list all persons, get person details, and delete persons

## Technology Stack

- Spring Boot 3.2.4
- OpenCV 4.7.0 (for face detection using Haar Cascade)
- Deep Java Library (DJL) 0.31.1
- Java 17

## API Endpoints

### 1. Detect Faces
Detect faces in an image and identify known persons.

**Endpoint:** `POST /api/faces/detect`

**Parameters:**
- `image` (multipart/form-data): Image file containing faces

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

### 2. Register Person
Register a new person with their face image.

**Endpoint:** `POST /api/faces/register`

**Parameters:**
- `name` (form parameter): Person's name
- `image` (multipart/form-data): Image file containing the person's face

**Response:**
```json
{
  "success": true,
  "personId": "uuid-here",
  "personName": "John Doe",
  "message": "Person registered successfully"
}
```

### 3. Add Face for Existing Person
Add an additional face image for an existing person to improve recognition.

**Endpoint:** `POST /api/faces/{personId}/add`

**Parameters:**
- `personId` (path variable): Person's UUID
- `image` (multipart/form-data): Image file containing the person's face

**Response:**
```json
{
  "success": true,
  "personId": "uuid-here",
  "personName": "John Doe",
  "totalFaces": 3,
  "message": "Face added successfully"
}
```

### 4. Get All Persons
List all registered persons.

**Endpoint:** `GET /api/faces/persons`

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

### 5. Get Person by ID
Get details of a specific person.

**Endpoint:** `GET /api/faces/persons/{personId}`

**Response:**
```json
{
  "id": "uuid-here",
  "name": "John Doe",
  "faceCount": 3
}
```

### 6. Delete Person
Delete a person and their face data.

**Endpoint:** `DELETE /api/faces/persons/{personId}`

**Response:**
```json
{
  "success": true,
  "message": "Person deleted successfully"
}
```

## How It Works

1. **Face Detection**: Uses OpenCV's Haar Cascade classifier to detect faces in images
2. **Feature Extraction**: Extracts normalized feature vectors from detected faces
3. **Face Matching**: Compares new faces with stored face encodings using cosine similarity
4. **Classification**: Identifies faces that match registered persons above a confidence threshold

## Running the Application

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`

## Usage Examples

### Using cURL

**Detect faces in an image:**
```bash
curl -X POST http://localhost:8080/api/faces/detect \
  -F "image=@/path/to/photo.jpg"
```

**Register a new person:**
```bash
curl -X POST http://localhost:8080/api/faces/register \
  -F "name=John Doe" \
  -F "image=@/path/to/john.jpg"
```

**Get all persons:**
```bash
curl http://localhost:8080/api/faces/persons
```

## Limitations

- The current implementation uses a simplified face encoding method for demonstration purposes
- For production use, consider using more advanced face recognition models
- Face data is stored in-memory and will be lost when the application restarts
- For persistent storage, add a database integration

## Future Enhancements

- Use pre-trained deep learning models for better face recognition accuracy
- Add persistent storage (database) for face data
- Implement face clustering for automatic person grouping
- Add support for real-time video face detection
- Implement face verification (1:1 matching)
- Add face image quality checks
