package com.securevault.service;

import com.securevault.dto.PasswordGeneratorOptions;
import com.securevault.dto.PasswordStrengthResponse;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordService {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private final SecureRandom random = new SecureRandom();

    public String generatePassword(PasswordGeneratorOptions options) {
        StringBuilder charPool = new StringBuilder();
        List<Character> mandatoryChars = new ArrayList<>();

        if (options.isIncludeUppercase()) {
            charPool.append(UPPERCASE);
            mandatoryChars.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        }
        if (options.isIncludeLowercase()) {
            charPool.append(LOWERCASE);
            mandatoryChars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }
        if (options.isIncludeNumbers()) {
            charPool.append(NUMBERS);
            mandatoryChars.add(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        if (options.isIncludeSpecial()) {
            charPool.append(SPECIAL);
            mandatoryChars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        }

        if (charPool.length() == 0) {
            charPool.append(LOWERCASE).append(NUMBERS);
            mandatoryChars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }

        int targetLength = Math.max(options.getLength(), mandatoryChars.size());
        StringBuilder passwordBuilder = new StringBuilder();

        for (char c : mandatoryChars) {
            passwordBuilder.append(c);
        }

        for (int i = mandatoryChars.size(); i < targetLength; i++) {
            int randomIndex = random.nextInt(charPool.length());
            passwordBuilder.append(charPool.charAt(randomIndex));
        }

        // Shuffle characters
        char[] passwordArray = passwordBuilder.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    public PasswordStrengthResponse analyzeStrength(String password) {
        if (password == null || password.isEmpty()) {
            return PasswordStrengthResponse.builder()
                    .strength("WEAK")
                    .score(0)
                    .suggestions(List.of("Enter a password to evaluate strength."))
                    .build();
        }

        int score = 0;
        List<String> suggestions = new ArrayList<>();

        // Length checks
        if (password.length() >= 16) {
            score += 40;
            suggestions.add("✓ Excellent password length (16+ chars)");
        } else if (password.length() >= 12) {
            score += 25;
            suggestions.add("✓ Good password length (12+ chars)");
        } else if (password.length() >= 8) {
            score += 15;
            suggestions.add("Consider extending length to at least 12 characters.");
        } else {
            score += 5;
            suggestions.add("Password is too short. Use at least 12 characters.");
        }

        // Character type checks
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*");

        if (hasUpper) {
            score += 15;
            suggestions.add("✓ Includes uppercase letters");
        } else {
            suggestions.add("Add uppercase letters.");
        }

        if (hasLower) {
            score += 15;
            suggestions.add("✓ Includes lowercase letters");
        } else {
            suggestions.add("Add lowercase letters.");
        }

        if (hasDigit) {
            score += 15;
            suggestions.add("✓ Includes numbers");
        } else {
            suggestions.add("Add numbers.");
        }

        if (hasSpecial) {
            score += 15;
            suggestions.add("✓ Includes special characters");
        } else {
            suggestions.add("Add special characters (e.g. !@#$%).");
        }

        String strength;
        if (score >= 80) {
            strength = "STRONG";
        } else if (score >= 50) {
            strength = "MEDIUM";
        } else {
            strength = "WEAK";
        }

        return PasswordStrengthResponse.builder()
                .strength(strength)
                .score(score)
                .suggestions(suggestions)
                .build();
    }
}
