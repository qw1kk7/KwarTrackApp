package com.mycompany.labopr.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserAuth {
    private static final String FILE = "users.txt";

    // --- Public methods ---
    public static boolean signUp(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) return false;
        if (findUser(username) != null) return false; // already exists

        String hashed = hashPassword(password);
        return writeUser(username, hashed);
    }

    public static boolean login(String username, String password) {
        String hashed = hashPassword(password);
        String storedHash = findUser(username);
        return hashed.equals(storedHash);
    }

    // --- Private helpers ---
    private static String findUser(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2 && parts[0].equals(username)) {
                    return parts[1];
                }
            }
        } catch (IOException ignored) {
            // file may not exist yet
        }
        return null;
    }

    private static boolean writeUser(String username, String hashed) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, true))) {
            bw.write(username + ":" + hashed);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}