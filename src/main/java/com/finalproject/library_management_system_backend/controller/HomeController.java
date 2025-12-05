package com.finalproject.library_management_system_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Provides a basic endpoint for testing or confirming
 * that the application is running.
 */
@RestController
public class HomeController {

        /**
     * Returns a simple greeting message.
     *
     * @return a plain text "Hello World" string
     */
    @GetMapping("/home")
    public  String home() {
        return "Hello World";
    }
}
