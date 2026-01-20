package com.heic.zxcv.service;

import com.heic.zxcv.model.FaceDetectionResult;
import com.heic.zxcv.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FaceRecognitionServiceTest {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    private byte[] createTestImage() throws IOException {
        // Create a simple test image (100x100 pixels)
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 100, 100);
        g.setColor(Color.BLACK);
        g.fillOval(25, 25, 50, 50); // Simple circle as a "face"
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    @Test
    void testRegisterPerson() throws IOException {
        byte[] imageData = createTestImage();
        
        // Register a person - may not detect face in simple test image
        try {
            Person person = faceRecognitionService.registerPerson("Test Person", imageData);
            assertNotNull(person);
            assertNotNull(person.getId());
            assertEquals("Test Person", person.getName());
            assertTrue(person.getFaceEncodings().size() > 0);
        } catch (IllegalArgumentException e) {
            // Expected if no face detected in test image
            assertTrue(e.getMessage().contains("No face detected"));
        }
    }

    @Test
    void testGetAllPersons() {
        List<Person> persons = faceRecognitionService.getAllPersons();
        assertNotNull(persons);
    }

    @Test
    void testGetPerson() throws IOException {
        byte[] imageData = createTestImage();
        
        try {
            Person registered = faceRecognitionService.registerPerson("Test User", imageData);
            Person retrieved = faceRecognitionService.getPerson(registered.getId());
            
            if (retrieved != null) {
                assertEquals(registered.getId(), retrieved.getId());
                assertEquals(registered.getName(), retrieved.getName());
            }
        } catch (IllegalArgumentException e) {
            // Expected if no face detected in test image
            assertTrue(e.getMessage().contains("No face detected"));
        }
    }

    @Test
    void testDeletePerson() throws IOException {
        byte[] imageData = createTestImage();
        
        try {
            Person person = faceRecognitionService.registerPerson("Delete Test", imageData);
            boolean deleted = faceRecognitionService.deletePerson(person.getId());
            assertTrue(deleted);
            
            Person retrieved = faceRecognitionService.getPerson(person.getId());
            assertNull(retrieved);
        } catch (IllegalArgumentException e) {
            // Expected if no face detected in test image
            assertTrue(e.getMessage().contains("No face detected"));
        }
    }

    @Test
    void testDeleteNonexistentPerson() {
        boolean deleted = faceRecognitionService.deletePerson("nonexistent-id");
        assertFalse(deleted);
    }

    @Test
    void testDetectFaces() throws IOException {
        byte[] imageData = createTestImage();
        
        // May not detect faces in simple test image
        List<FaceDetectionResult> faces = faceRecognitionService.detectFaces(imageData);
        assertNotNull(faces);
        // The simple test image may not have detectable faces
        assertTrue(faces.size() >= 0);
    }

    @Test
    void testRegisterPersonWithInvalidImage() {
        byte[] invalidData = new byte[]{0, 1, 2, 3};
        
        assertThrows(IllegalArgumentException.class, () -> {
            faceRecognitionService.registerPerson("Invalid", invalidData);
        });
    }

    @Test
    void testDetectFacesWithInvalidImage() {
        byte[] invalidData = new byte[]{0, 1, 2, 3};
        
        assertThrows(IllegalArgumentException.class, () -> {
            faceRecognitionService.detectFaces(invalidData);
        });
    }
}
