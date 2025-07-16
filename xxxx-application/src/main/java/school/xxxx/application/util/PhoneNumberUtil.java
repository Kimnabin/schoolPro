package school.xxxx.application.util;

import java.util.regex.Pattern;

public class PhoneNumberUtil {

    // Các pattern validation cho phone number
    private static final Pattern VIETNAM_PHONE_PATTERN =
            Pattern.compile("^(\\+84|0)\\d{9,10}$");

    private static final Pattern INTERNATIONAL_PHONE_PATTERN =
            Pattern.compile("^\\+\\d{1,3}\\d{7,14}$");

    private static final Pattern GENERAL_PHONE_PATTERN =
            Pattern.compile("^(\\+\\d{1,3}[- ]?)?\\d{7,14}$");

    /**
     * Validate phone number Vietnam
     */
    public static boolean isValidVietnamPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return VIETNAM_PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

    /**
     * Validate international phone number
     */
    public static boolean isValidInternationalPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return INTERNATIONAL_PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

    /**
     * Validate general phone number (flexible)
     */
    public static boolean isValidPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return GENERAL_PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

    /**
     * Normalize phone number (remove spaces, dashes)
     */
    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return phoneNumber.replaceAll("[\\s-()]", "").trim();
    }

    /**
     * Convert Vietnam phone to international format
     */
    public static String toInternationalFormat(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }

        String normalized = normalizePhoneNumber(phoneNumber);

        // Nếu bắt đầu bằng 0 (số VN), chuyển thành +84
        if (normalized.startsWith("0") && normalized.length() >= 10) {
            return "+84" + normalized.substring(1);
        }

        // Nếu bắt đầu bằng 84 (không có +), thêm +
        if (normalized.startsWith("84") && normalized.length() >= 11) {
            return "+" + normalized;
        }

        return normalized;
    }

    /**
     * Format phone number for display
     */
    public static String formatForDisplay(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }

        String normalized = normalizePhoneNumber(phoneNumber);

        // Format Vietnam phone: 0123 456 789
        if (normalized.startsWith("0") && normalized.length() == 10) {
            return normalized.substring(0, 4) + " " +
                    normalized.substring(4, 7) + " " +
                    normalized.substring(7);
        }

        // Format international: +84 123 456 789
        if (normalized.startsWith("+84") && normalized.length() == 12) {
            return normalized.substring(0, 3) + " " +
                    normalized.substring(3, 6) + " " +
                    normalized.substring(6, 9) + " " +
                    normalized.substring(9);
        }

        return phoneNumber; // Return as-is if no specific format
    }

    /**
     * Examples of valid phone numbers
     */
    public static String[] getExamplePhoneNumbers() {
        return new String[]{
                "0123456789",           // Vietnam local
                "+84123456789",         // Vietnam international
                "0084123456789",        // Vietnam with 00
                "+1234567890",          // US
                "+44123456789",         // UK
                "+81123456789",         // Japan
                "+86123456789"          // China
        };
    }
}