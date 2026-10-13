package com.securevault.dto;

public class PasswordGeneratorOptions {
    private int length = 16;
    private boolean includeUppercase = true;
    private boolean includeLowercase = true;
    private boolean includeNumbers = true;
    private boolean includeSpecial = true;

    public PasswordGeneratorOptions() {}

    public PasswordGeneratorOptions(int length, boolean includeUppercase, boolean includeLowercase, boolean includeNumbers, boolean includeSpecial) {
        this.length = length;
        this.includeUppercase = includeUppercase;
        this.includeLowercase = includeLowercase;
        this.includeNumbers = includeNumbers;
        this.includeSpecial = includeSpecial;
    }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
    public boolean isIncludeUppercase() { return includeUppercase; }
    public void setIncludeUppercase(boolean includeUppercase) { this.includeUppercase = includeUppercase; }
    public boolean isIncludeLowercase() { return includeLowercase; }
    public void setIncludeLowercase(boolean includeLowercase) { this.includeLowercase = includeLowercase; }
    public boolean isIncludeNumbers() { return includeNumbers; }
    public void setIncludeNumbers(boolean includeNumbers) { this.includeNumbers = includeNumbers; }
    public boolean isIncludeSpecial() { return includeSpecial; }
    public void setIncludeSpecial(boolean includeSpecial) { this.includeSpecial = includeSpecial; }
}
