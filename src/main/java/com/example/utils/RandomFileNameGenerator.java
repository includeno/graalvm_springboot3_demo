package com.example.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class RandomFileNameGenerator {

    public static void main(String[] args) {
        System.out.println(generateRandomFileName(true, true, true, ".docx"));
        System.out.println(generateRandomFileName("test", true, true, ".docx"));
        System.out.println(generateRandomFileName("test", false, true, ".docx"));
    }

    public static String generateRandomFileName(String baseName, boolean includeTimestamp, boolean includeUUID, String suffix) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(baseName);

        if (includeTimestamp) {
            if (fileName.length() > 0) {
                fileName.append("_");
            }
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = now.format(formatter);
            fileName.append(timestamp);
        }

        if (includeUUID) {
            if (fileName.length() > 0) {
                fileName.append("_");
            }
            UUID uuid = UUID.randomUUID();
            fileName.append(uuid.toString());
        }
        fileName.append(suffix);
        return fileName.toString();
    }

    public static String generateRandomFileName(boolean includeRandomName, boolean includeTimestamp, boolean includeUUID, String suffix) {
        StringBuilder fileName = new StringBuilder();

        if (includeRandomName) {
            SecureRandom random = new SecureRandom();
            byte[] randomBytes = new byte[4];
            random.nextBytes(randomBytes);
            String randomName = bytesToHex(randomBytes);
            fileName.append(randomName);
        }

        if (includeTimestamp) {
            if (fileName.length() > 0) {
                fileName.append("_");
            }
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = now.format(formatter);
            fileName.append(timestamp);
        }

        if (includeUUID) {
            if (fileName.length() > 0) {
                fileName.append("_");
            }
            UUID uuid = UUID.randomUUID();
            fileName.append(uuid.toString());
        }
        fileName.append(suffix);

        return fileName.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
