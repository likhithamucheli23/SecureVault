package com.securevault.dto;

import java.util.List;

public class PasswordStrengthResponse {
    private String strength;
    private int score;
    private List<String> suggestions;

    public PasswordStrengthResponse() {}

    public PasswordStrengthResponse(String strength, int score, List<String> suggestions) {
        this.strength = strength;
        this.score = score;
        this.suggestions = suggestions;
    }

    public static PasswordStrengthResponseBuilder builder() {
        return new PasswordStrengthResponseBuilder();
    }

    public static class PasswordStrengthResponseBuilder {
        private String strength;
        private int score;
        private List<String> suggestions;

        public PasswordStrengthResponseBuilder strength(String strength) { this.strength = strength; return this; }
        public PasswordStrengthResponseBuilder score(int score) { this.score = score; return this; }
        public PasswordStrengthResponseBuilder suggestions(List<String> suggestions) { this.suggestions = suggestions; return this; }

        public PasswordStrengthResponse build() {
            return new PasswordStrengthResponse(strength, score, suggestions);
        }
    }

    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}
