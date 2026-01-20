package com.heic.zxcv.service;

import com.heic.zxcv.model.FaceDetectionResult;
import com.heic.zxcv.model.Person;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for face detection and recognition
 * Similar to iPhone Photos app face recognition
 */
@Service
public class FaceRecognitionService {
    
    private final Map<String, Person> personDatabase;
    private CascadeClassifier faceDetector;
    private static final double FACE_MATCH_THRESHOLD = 0.6;
    
    static {
        // Load OpenCV native library
        nu.pattern.OpenCV.loadLocally();
    }
    
    public FaceRecognitionService() {
        this.personDatabase = new ConcurrentHashMap<>();
        initializeFaceDetector();
    }
    
    private void initializeFaceDetector() {
        try {
            // Load Haar Cascade classifier for face detection
            InputStream is = getClass().getClassLoader().getResourceAsStream("haarcascade_frontalface_default.xml");
            if (is == null) {
                throw new RuntimeException("Could not load face detection model");
            }
            
            File tempFile = File.createTempFile("haarcascade", ".xml");
            tempFile.deleteOnExit();
            
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
            faceDetector = new CascadeClassifier(tempFile.getAbsolutePath());
            
            if (faceDetector.empty()) {
                throw new RuntimeException("Failed to load cascade classifier");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error initializing face detector", e);
        }
    }
    
    /**
     * Detect faces in an image
     */
    public List<FaceDetectionResult> detectFaces(byte[] imageData) {
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new IllegalArgumentException("Could not decode image");
        }
        
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayImage, grayImage);
        
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faces, 1.1, 3, 0, new Size(30, 30), new Size());
        
        List<FaceDetectionResult> results = new ArrayList<>();
        for (Rect rect : faces.toArray()) {
            FaceDetectionResult result = new FaceDetectionResult(
                rect.x, rect.y, rect.width, rect.height, 1.0
            );
            
            // Extract face encoding
            Mat faceROI = new Mat(grayImage, rect);
            double[] encoding = extractFaceEncoding(faceROI);
            
            // Try to match with known faces
            Person matchedPerson = findMatchingPerson(encoding);
            if (matchedPerson != null) {
                result.setPersonId(matchedPerson.getId());
                result.setPersonName(matchedPerson.getName());
            }
            
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * Extract face encoding (simplified feature vector)
     */
    private double[] extractFaceEncoding(Mat face) {
        // Resize face to standard size
        Mat resizedFace = new Mat();
        Imgproc.resize(face, resizedFace, new Size(64, 64));
        
        // Simple encoding: flatten the resized image and normalize
        int size = (int) (resizedFace.total() * resizedFace.channels());
        double[] encoding = new double[size];
        resizedFace.get(0, 0, encoding);
        
        // Normalize
        double sum = 0;
        for (double val : encoding) {
            sum += val * val;
        }
        double norm = Math.sqrt(sum);
        if (norm > 0) {
            for (int i = 0; i < encoding.length; i++) {
                encoding[i] /= norm;
            }
        }
        
        return encoding;
    }
    
    /**
     * Calculate similarity between two face encodings
     * Since encodings are pre-normalized, this is just the dot product
     */
    private double calculateSimilarity(double[] encoding1, double[] encoding2) {
        if (encoding1.length != encoding2.length) {
            return 0.0;
        }
        
        // Calculate dot product (encodings are already normalized)
        double dotProduct = 0.0;
        for (int i = 0; i < encoding1.length; i++) {
            dotProduct += encoding1[i] * encoding2[i];
        }
        
        return dotProduct;
    }
    
    /**
     * Find a matching person for a face encoding
     */
    private Person findMatchingPerson(double[] encoding) {
        Person bestMatch = null;
        double bestSimilarity = 0.0;
        
        for (Person person : personDatabase.values()) {
            for (double[] knownEncoding : person.getFaceEncodings()) {
                double similarity = calculateSimilarity(encoding, knownEncoding);
                if (similarity > bestSimilarity && similarity > FACE_MATCH_THRESHOLD) {
                    bestSimilarity = similarity;
                    bestMatch = person;
                }
            }
        }
        
        return bestMatch;
    }
    
    /**
     * Register a new person with their face
     */
    public Person registerPerson(String name, byte[] imageData) {
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new IllegalArgumentException("Could not decode image");
        }
        
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayImage, grayImage);
        
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faces, 1.1, 3, 0, new Size(30, 30), new Size());
        
        if (faces.toArray().length == 0) {
            throw new IllegalArgumentException("No face detected in image");
        }
        
        // Use the first detected face
        Rect faceRect = faces.toArray()[0];
        Mat faceROI = new Mat(grayImage, faceRect);
        double[] encoding = extractFaceEncoding(faceROI);
        
        String personId = UUID.randomUUID().toString();
        Person person = new Person(personId, name);
        person.addFaceEncoding(encoding);
        
        personDatabase.put(personId, person);
        
        return person;
    }
    
    /**
     * Get all registered persons
     */
    public List<Person> getAllPersons() {
        return new ArrayList<>(personDatabase.values());
    }
    
    /**
     * Get a person by ID
     */
    public Person getPerson(String personId) {
        return personDatabase.get(personId);
    }
    
    /**
     * Delete a person
     */
    public boolean deletePerson(String personId) {
        return personDatabase.remove(personId) != null;
    }
    
    /**
     * Add additional face encoding for an existing person
     */
    public Person addFaceForPerson(String personId, byte[] imageData) {
        Person person = personDatabase.get(personId);
        if (person == null) {
            throw new IllegalArgumentException("Person not found: " + personId);
        }
        
        Mat image = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new IllegalArgumentException("Could not decode image");
        }
        
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayImage, grayImage);
        
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faces, 1.1, 3, 0, new Size(30, 30), new Size());
        
        if (faces.toArray().length == 0) {
            throw new IllegalArgumentException("No face detected in image");
        }
        
        Rect faceRect = faces.toArray()[0];
        Mat faceROI = new Mat(grayImage, faceRect);
        double[] encoding = extractFaceEncoding(faceROI);
        
        person.addFaceEncoding(encoding);
        
        return person;
    }
}
