package com.antiprocrastinate.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LabApplication {

  public static void main(String[] args) {
    SpringApplication.run(LabApplication.class, args);
  }

}