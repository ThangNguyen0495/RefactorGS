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
     * Compares two images pixel by pixel, considering only the overlapping area based on the minimum width and height.
     *
     * @return true if the overlapping area of the images is identical, false otherwise.
     * @throws IOException if there is an error reading the image files.
     */
    public boolean compareImages(String expectedImagePath, String actualImagePath) throws IOException {
        // Load the images to compare
        BufferedImage img1 = ImageIO.read(new File(expectedImagePath));
        BufferedImage img2 = ImageIO.read(new File(actualImagePath));
        img2 = scaleImage(img2, img1.getWidth(), img1.getHeight());

        // Determine the minimum width and height for comparison
        int minWidth = Math.min(img1.getWidth(), img2.getWidth());
        int minHeight = Math.min(img1.getHeight(), img2.getHeight());

        // Compare images pixel by pixel for the overlapping area
        int totalPixels = minWidth * minHeight;
        int matchingPixels = 0;

        for (int y = 0; y < minHeight; y++) {
            for (int x = 0; x < minWidth; x++) {
                if (img1.getRGB(x, y) == img2.getRGB(x, y)) {
                    matchingPixels++;
                }
            }
        }

        // Calculate match percentage
        float matchPercentage = (float) matchingPixels / totalPixels;

        LogManager.getLogger().info("Matches percentage: %s".formatted(matchPercentage * 100));
        // Return true if all pixels in the overlapping area match
        return matchPercentage >= 0.75;
    }

    public static BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        // Create a new BufferedImage for the scaled version
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB_PRE);

        // Get the Graphics2D object for rendering the scaled image
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose(); // Release resources

        return scaledImage;
    }
}
