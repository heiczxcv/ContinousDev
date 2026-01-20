package com.heic.zxcv.controller;

import com.heic.zxcv.model.Person;
import com.heic.zxcv.service.FaceRecognitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FaceRecognitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    private byte[] createTestImage() throws Exception {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 100, 100);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    @Test
    void testDetectFacesEndpoint() throws Exception {
        byte[] imageData = createTestImage();
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageData);

        mockMvc.perform(multipart("/api/faces/detect")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facesDetected").exists());
    }

    @Test
    void testRegisterPersonEndpoint() throws Exception {
        byte[] imageData = createTestImage();
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageData);

        // May return error if no face detected in simple test image
        mockMvc.perform(multipart("/api/faces/register")
                .file(file)
                .param("name", "Test Person"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetAllPersonsEndpoint() throws Exception {
        mockMvc.perform(get("/api/faces/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPersons").exists())
                .andExpect(jsonPath("$.persons").isArray());
    }

    @Test
    void testGetPersonEndpoint() throws Exception {
        // Test with non-existent person
        mockMvc.perform(get("/api/faces/persons/nonexistent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePersonEndpoint() throws Exception {
        // Test with non-existent person
        mockMvc.perform(delete("/api/faces/persons/nonexistent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDetectFacesWithoutImage() throws Exception {
        mockMvc.perform(multipart("/api/faces/detect"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testRegisterPersonWithoutName() throws Exception {
        byte[] imageData = createTestImage();
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageData);

        mockMvc.perform(multipart("/api/faces/register")
                .file(file))
                .andExpect(status().is4xxClientError());
    }
}
