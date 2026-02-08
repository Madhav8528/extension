package com.example.whatsapp;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WhatsAppAutomation {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    public static void main(String[] args) {
        String phoneNumber = requiredEnv("WHATSAPP_PHONE");
        String message = requiredEnv("WHATSAPP_MESSAGE");
        String attachmentPath = System.getenv("WHATSAPP_FILE_PATH");

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);

        try {
            openChat(driver, phoneNumber, message);
            waitForChatReady(wait);
            sendMessage(wait, message);

            if (attachmentPath != null && !attachmentPath.isBlank()) {
                attachFile(wait, attachmentPath);
            }
        } finally {
            driver.quit();
        }
    }

    private static void openChat(WebDriver driver, String phoneNumber, String message) {
        String encodedMessage = java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
        String url = String.format("https://web.whatsapp.com/send?phone=%s&text=%s", phoneNumber, encodedMessage);
        driver.get(url);
    }

    private static void waitForChatReady(WebDriverWait wait) {
        wait.until(ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[contenteditable='true']")),
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("canvas"))
        ));

        if (!waitUntilChatLoaded(wait)) {
            throw new IllegalStateException("WhatsApp Web did not load. Ensure you scanned the QR code and have access.");
        }
    }

    private static boolean waitUntilChatLoaded(WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[contenteditable='true']")));
            return true;
        } catch (org.openqa.selenium.TimeoutException ex) {
            return false;
        }
    }

    private static void sendMessage(WebDriverWait wait, String message) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[contenteditable='true']")));
        input.sendKeys(message);
        input.sendKeys(Keys.ENTER);
    }

    private static void attachFile(WebDriverWait wait, String attachmentPath) {
        Path filePath = Path.of(attachmentPath);
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Attachment file not found: " + attachmentPath);
        }

        WebElement attachButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("span[data-icon='plus']")));
        attachButton.click();

        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='file']")));
        fileInput.sendKeys(filePath.toAbsolutePath().toString());

        WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("span[data-icon='send']")));
        sendButton.click();
    }

    private static String requiredEnv(String key) {
        String value = System.getenv(key);
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException("Missing required environment variable: " + key);
        }
        return value;
    }
}
