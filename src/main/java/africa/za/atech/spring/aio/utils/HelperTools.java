package africa.za.atech.spring.aio.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HelperTools {

    // Regular expression patterns for password validation
    private static final String UPPER_CASE_PATTERN = ".*[A-Z].*";
    private static final String DIGIT_PATTERN = ".*\\d.*";
    private static final String SPECIAL_CHARACTER_PATTERN = ".*[!@#%&\\*_\\?\\|\\(\\)\\[\\]\\{\\}W1].*";

    public static String wrapVar(String text) {
        return "[" + text + "]";
    }

    @SneakyThrows
    public static String getString(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        return IOUtils.toString(resource.getInputStream(), Charset.defaultCharset());
    }

    public static JsonArray getJsonArray(String json) {
        return JsonParser.parseString(json).getAsJsonArray();
    }

    public static JsonArray getJsonArray(String json, String node) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (jsonObject.has(node)) {
            try {
                return jsonObject.getAsJsonArray(node);
            } catch (ClassCastException e) {
                throw new ClassCastException("JsonObject found at node: '" + node + "'");
            }
        } else {
            throw new RuntimeException("Node '" + node + "' does not exist at root");
        }
    }

    @SneakyThrows
    public static List<String[]> readCsv(File csvFile) {
        // https://www.bezkoder.com/thymeleaf-file-upload/#Define_Data_Model
        CSVReader reader = new CSVReader(new FileReader(csvFile));
        return reader.readAll();
    }

    @SneakyThrows
    public static List<String[]> readCsv(MultipartFile csvFile, String tempPath) {
        return readCsv(toFile(csvFile, tempPath));
    }

    @SuppressWarnings("all")
    public static String toHtml(String text) {
        text = "\n\n" + text;
        // Create a new HTML document
        Document doc = Jsoup.parse("<div></div>");
        // Get the <div> element from the document
        Element div = doc.select("div").first();
        // Split the response into paragraphs using "\n\n" as the delimiter
        String[] paragraphs = text.split("\n\n");
        // Iterate through each paragraph
        for (String paragraph : paragraphs) {
            // Check if the paragraph starts with "\n- " to start a list item
            if (paragraph.startsWith("\n- ")) {
                // If it's the start of a list item, split it into individual items
                String[] listItems = paragraph.split("\n- ");
                // Start the unordered list
                div.append("<ul>");
                // Iterate through each list item
                for (String item : listItems) {
                    // Skip the first element if it's empty
                    if (!item.isEmpty()) {
                        // Append each list item wrapped in <li> tags
                        div.append("<li>" + formatText(item) + "</li>");
                    }
                }
                // End the unordered list
                div.append("</ul>");
            } else {
                // If it's not a list item, format as a regular paragraph
                div.append("<p>" + formatText(paragraph) + "</p>");
            }
        }
        // Return the HTML representation of the document
        return doc.toString();
    }

    // Function to format text (e.g., bold)
    private static String formatText(String text) {
        // Replace "**text**" with "<b>text</b>"
        text = text.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
        return text;
    }

    public static File toFile(MultipartFile multipartFile, String location) {
        File destFile = new File(location + "/" + multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(destFile);
        } catch (IOException e) {
            log.warn("Unable to transfer multipart FILE to: {}", location);
        }
        return destFile;
    }

    // Method to enforce password strength
    public static List<String> validatePassword(String password) {
        List<String> failures = new ArrayList<>();

        if (password.length() < 8) {
            failures.add("Password length must be at least 9 characters");
        }
        if (!matchesPattern(password, UPPER_CASE_PATTERN)) {
            failures.add("Password must contain at least one uppercase letter");
        }
        if (!matchesPattern(password, DIGIT_PATTERN)) {
            failures.add("Password must contain at least one digit");
        }
        if (!matchesPattern(password, SPECIAL_CHARACTER_PATTERN)) {
            failures.add("Password must contain at least one special character");
        }

        return failures;
    }

    // Method to generate a strong random password with specified parameters
    public static String generatePassword(int length, int numberOfDigits, int numberOfSpecialCharacters) {
        if (length < 3 || numberOfDigits + numberOfSpecialCharacters > length - 2) {
            throw new IllegalArgumentException("Invalid parameters. Length must be at least 3, and total number of digits and special characters must not exceed length - 2.");
        }

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = upperCase.toLowerCase();
        String digits = "0123456789";
        String specialCharacters = "!@#%&*_?|()[]{}";
        StringBuilder allCharacters = new StringBuilder(upperCase + lowerCase);

        // Add digits
        for (int i = 0; i < numberOfDigits; i++) {
            allCharacters.append(digits);
        }

        // Add special characters
        for (int i = 0; i < numberOfSpecialCharacters; i++) {
            allCharacters.append(specialCharacters);
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one uppercase letter, one digit, and one special character
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        // Generate remaining characters
        for (int i = 0; i < length - 3; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        // Shuffle the password
        char[] passwordChars = password.toString().toCharArray();
        for (int i = passwordChars.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = passwordChars[index];
            passwordChars[index] = passwordChars[i];
            passwordChars[i] = temp;
        }

        return new String(passwordChars);
    }

    // Method to check if a string matches a regular expression pattern
    private static boolean matchesPattern(String input, String pattern) {
        return input.matches(pattern);
    }

    public static String getLoggedInUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static String getLoggedInRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
    }
}