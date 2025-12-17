package com.example.api_backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptGen {
    public static void main(String[] args) {
        String raw = "admin123";
        String encoded = new BCryptPasswordEncoder().encode(raw);
        System.out.println(encoded);
        // Test passs
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean match = encoder.matches("admin123", "$2a$10$RVrJLavmEFe7PNavSofb9.xF5oBQmYqIYmh8ssNld7AfEW97dDk3G");
        System.out.println(match);

    }
}
