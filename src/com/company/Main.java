package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Main {
    private Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]){
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("qz.xml");
    }

    public void start()
    {
        logger.info("test start");
        System.out.println("test");

    }
}
