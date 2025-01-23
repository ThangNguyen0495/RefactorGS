package utility;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class ScreenshotUtils {
    /**
     * Takes a screenshot of the current page and saves it to the default 'debug' folder.
     *
     * @param driver The WebDriver instance used to take the screenshot.
     */
    @SneakyThrows
    public static void takeScreenshot(WebDriver driver) {
        // Ensure the debug directory exists
        File debugDir = new File("./debug/");
        if (!debugDir.exists()) {
            boolean created = debugDir.mkdirs();
            LogManager.getLogger().info(created ? "Created 'debug' folder" : "Failed to create 'debug' folder");
        }

        // Define the file path for the screenshot
        String path = "./debug/%s_%s.png".formatted("debug", LocalDateTime.now().toString().substring(0, 19))
                .replace("/", File.separator);
        FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), new File(path));
    }

    /**
     * Takes a screenshot of the current page and saves it to a specified folder with a specified file name.
     *
     * @param driver     The WebDriver instance used to take the screenshot.
     * @param folderName The name of the folder where the screenshot will be saved.
     * @param fileName   The name of the file to save the screenshot as.
     */
    @SneakyThrows
    public static void takeScreenshot(WebDriver driver, String folderName, String fileName) {
        // Ensure the specified folder exists
        File folder = new File("./debug/%s/".formatted(folderName));
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            LogManager.getLogger().info(created ? "Created folder '" + folderName + "'" : "Failed to create folder '" + folderName + "'");
        }

        // Define the file path for the screenshot
        String path = "./debug/%s/%s.png".formatted(folderName, fileName);
        FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), new File(path));
    }

    // Define the constant paths for the images

    /**
     * Takes a screenshot of a specific WebElement and saves it to the 'element_image' folder.
     *
     * @param element The WebElement to take a screenshot of.
     */
    @SneakyThrows
    public void takeElementScreenShot(String imagePath, WebElement element) {
        // Capture screenshot of the WebElement
        File screenshot = element.getScreenshotAs(OutputType.FILE);

        // Ensure the checkbox folder exists
        String directoryPath = imagePath.substring(0, imagePath.lastIndexOf("/"));
        File elementImageDir = new File(directoryPath);
        if (!elementImageDir.exists()) {
            boolean created = elementImageDir.mkdirs();
            LogManager.getLogger().info(created ? "Created 'element_image' folder" : "Failed to create 'element_image' folder");
        }

        // Define the destination file path
        File destination = new File(imagePath);
        FileUtils.copyFile(screenshot, destination);

    }

    /**
     * Compares the provided image by calculating the percentage of white pixels.
     * If the percentage of white pixels is greater than or equal to 75%,
     * the method returns false (indicating unchecked); otherwise, it returns true.
     *
     * @param actualImagePath The file path of the image to analyze.
     * @return {@code true} if the percentage of white pixels is less than 75%, {@code false} otherwise.
     * @throws IOException If there is an issue reading the image file.
     */
    public boolean compareImages(String actualImagePath) throws IOException {
        // Load the image
        BufferedImage img = ImageIO.read(new File(actualImagePath));

        // Initialize pixel counters
        int totalPixels = img.getHeight() * img.getWidth();
        int whitePixels = 0;

        // Loop through each pixel in the image
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color color = new Color(img.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Check if the pixel is considered white
                if (isWhitePixel(red, green, blue)) {
                    whitePixels++;
                }
            }
        }

        // Calculate the percentage of white pixels
        float whitePercentage = (float) whitePixels / totalPixels;

        // Log the calculated percentage
        LogManager.getLogger().info("White percentage: {}%", whitePercentage * 100);

        // Return true if white percentage is less than 75%, false otherwise
        return whitePercentage < 0.75;
    }

    /**
     * Determines if a pixel is white based on its RGB values.
     *
     * @param red   The red component of the pixel.
     * @param green The green component of the pixel.
     * @param blue  The blue component of the pixel.
     * @return {@code true} if the pixel is white, {@code false} otherwise.
     */
    private boolean isWhitePixel(int red, int green, int blue) {
        return red >= 240 && green >= 240 && blue >= 240;
    }
}
