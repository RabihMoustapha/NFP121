package com.isae.medialibrary.util;

public class ValidationUtil {
    public static boolean isValidYear(int year) {
        return year >= 1900 && year <= 2100;
    }
}