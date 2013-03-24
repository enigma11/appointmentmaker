package com.gdiama.domain;

public enum AppointmentCategory {
    PASSPORT(4, "Greek Passports"),
    MILITARY(6, "Military Affairs / Permanent Residence Certificates"),
    CERTIFICATES(5, "Certificates (Births - Deaths Marriages)"),
    PERMANENT_RESIDENCE(2, "Permanent Residence Certificates (Not Military)");

    private final int optionValue;
    private final String description;

    AppointmentCategory(int optionValue, String description) {
        this.optionValue = optionValue;
        this.description = description;
    }

    public int optionValue() {
        return optionValue;
    }

    public String description() {
        return description;
    }
}
