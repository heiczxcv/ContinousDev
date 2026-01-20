package com.heic.zxcv.controller;

import com.heic.zxcv.model.FaceDetectionResult;
import com.heic.zxcv.model.Person;
import com.heic.zxcv.service.FaceRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API for face recognition
 * Similar to iPhone Photos app functionality
 */
@RestController
@RequestMapping("/api/faces")
public class FaceRecognitionController {
    
    @Autowired
    private FaceRecognitionService faceRecognitionService;
    
    /**
     * Detect faces in an uploaded image
     * POST /api/faces/detect
     */
    @PostMapping("/detect")
    public ResponseEntity<?> detectFaces(@RequestParam("image") MultipartFile image) {
        try {
            byte[] imageData = image.getBytes();
            List<FaceDetectionResult> faces = faceRecognitionService.detectFaces(imageData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("facesDetected", faces.size());
            response.put("faces", faces);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to read image: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error processing image: " + e.getMessage()));
        }
    }
    
    /**
     * Register a new person with their face
     * POST /api/faces/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerPerson(
            @RequestParam("name") String name,
            @RequestParam("image") MultipartFile image) {
        try {
            byte[] imageData = image.getBytes();
            Person person = faceRecognitionService.registerPerson(name, imageData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("personId", person.getId());
            response.put("personName", person.getName());
            response.put("message", "Person registered successfully");
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to read image: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error registering person: " + e.getMessage()));
        }
    }
    
    /**
     * Add additional face image for an existing person
     * POST /api/faces/{personId}/add
     */
    @PostMapping("/{personId}/add")
    public ResponseEntity<?> addFaceForPerson(
            @PathVariable String personId,
            @RequestParam("image") MultipartFile image) {
        try {
            byte[] imageData = image.getBytes();
            Person person = faceRecognitionService.addFaceForPerson(personId, imageData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("personId", person.getId());
            response.put("personName", person.getName());
            response.put("totalFaces", person.getFaceEncodings().size());
            response.put("message", "Face added successfully");
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to read image: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error adding face: " + e.getMessage()));
        }
    }
    
    /**
     * Get all registered persons
     * GET /api/faces/persons
     */
    @GetMapping("/persons")
    public ResponseEntity<?> getAllPersons() {
        try {
            List<Person> persons = faceRecognitionService.getAllPersons();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalPersons", persons.size());
            response.put("persons", persons.stream().map(p -> {
                Map<String, Object> personInfo = new HashMap<>();
                personInfo.put("id", p.getId());
                personInfo.put("name", p.getName());
                personInfo.put("faceCount", p.getFaceEncodings().size());
                return personInfo;
            }).toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error retrieving persons: " + e.getMessage()));
        }
    }
    
    /**
     * Get a specific person by ID
     * GET /api/faces/persons/{personId}
     */
    @GetMapping("/persons/{personId}")
    public ResponseEntity<?> getPerson(@PathVariable String personId) {
        try {
            Person person = faceRecognitionService.getPerson(personId);
            
            if (person == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Person not found"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", person.getId());
            response.put("name", person.getName());
            response.put("faceCount", person.getFaceEncodings().size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error retrieving person: " + e.getMessage()));
        }
    }
    
    /**
     * Delete a person
     * DELETE /api/faces/persons/{personId}
     */
    @DeleteMapping("/persons/{personId}")
    public ResponseEntity<?> deletePerson(@PathVariable String personId) {
        try {
            boolean deleted = faceRecognitionService.deletePerson(personId);
            
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Person not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Person deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error deleting person: " + e.getMessage()));
        }
    }
}
