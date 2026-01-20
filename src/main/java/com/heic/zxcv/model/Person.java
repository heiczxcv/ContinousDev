package com.heic.zxcv.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a person with their face encodings
 */
public class Person {
    private String id;
    private String name;
    private List<double[]> faceEncodings;
    
    public Person() {
        this.faceEncodings = new ArrayList<>();
    }
    
    public Person(String id, String name) {
        this.id = id;
        this.name = name;
        this.faceEncodings = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<double[]> getFaceEncodings() {
        return faceEncodings;
    }
    
    public void setFaceEncodings(List<double[]> faceEncodings) {
        this.faceEncodings = faceEncodings;
    }
    
    public void addFaceEncoding(double[] encoding) {
        this.faceEncodings.add(encoding);
    }
}
