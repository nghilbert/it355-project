package edu.ilstu.recordvault.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SafeLogger {

    private static final Path LOG_FILE = Paths.get("data", "recordvault.log");
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final SafeLogger INSTANCE = new SafeLogger();

    private BufferedWriter writer;

    public static SafeLogger getInstance() {
        return INSTANCE;
    }

    private SafeLogger() {
        try {
            // FIO02-J (Driss): detect and handle file-related errors safely
            Path parent = LOG_FILE.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            writer = Files.newBufferedWriter(
                    LOG_FILE,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            // ERR02-J (Driss): logging setup failure must not break the program
            writer = null;

            // ERR01-J (Driss): print only a safe message without exposing internal details
            System.err.println("[SafeLogger] Logging to file is unavailable. Using stderr.");
        }
    }

    public void log(String message) {
        // ERR08-J (Driss): check for null explicitly before use
        if (message == null) {
            message = "(null message)";
        }

        // IDS01-J (Driss): normalize untrusted input before validation/sanitization
        String normalized = Normalizer.normalize(message, Form.NFKC);
        String safeMessage = normalized.replace('\n', ' ')
                                       .replace('\r', ' ')
                                       .replace('\0', ' ');

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = "[" + timestamp + "] " + safeMessage;

        if (writer != null) {
            try {
                writer.write(entry);
                writer.newLine();
                writer.flush();

            // ERR02-J (Driss): catch logging failures so they do not interfere with the program
            } catch (IOException e) {

                // ERR01-J (Driss): print only a safe message without exposing internal details
                System.err.println("[SafeLogger] Log write failed. Using stderr fallback.");
                System.err.println(entry);
            }
        } else {
            System.err.println(entry);
        }
    }

    public void close() {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
                writer = null;

            // ERR02-J (Driss): catch IOException so close() does not throw and logging
            // cleanup cannot break the program.
            } catch (IOException e) {

                // ERR01-J (Driss): print only a safe message without exposing internal details
                System.err.println("[SafeLogger] Warning: could not close log file cleanly.");
            }
        }
    }
}