package utility;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    /**
     * Takes a screenshot of the current page and saves it to the default 'debug' folder.
     *
     * @param driver The WebDriver instance used to take the screenshot.
     */
    @SneakyThrows
    public void takeScreenshot(WebDriver driver) {
        // Ensure the debug directory exists
        File debugDir = new File("./debug/");
        if (!debugDir.exists()) {
            boolean created = debugDir.mkdirs();
            LogManager.getLogger().info(created ? "Created 'debug' folder" : "Failed to create 'debug' folder");
        }

        // Define the file path for the screenshot
        String path = "./debug/%s_%s.png".formatted("debug", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
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
    public void takeScreenshot(WebDriver driver, String folderName, String fileName) {
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

    /**
     * Takes a screenshot of a specific WebElement and saves it to the 'element_image' folder.
     *
     * @param element The WebElement to take a screenshot of.
     * @return The ScreenshotUtils instance for method chaining.
     */
    @SneakyThrows
    public ScreenshotUtils takeScreenshot(WebElement element) {
        // Capture screenshot of the WebElement
        File screenshot = element.getScreenshotAs(OutputType.FILE);

        // Ensure the 'element_image' folder exists
        File elementImageDir = new File("./src/main/resources/files/element_image");
        if (!elementImageDir.exists()) {
            boolean created = elementImageDir.mkdirs();
            LogManager.getLogger().info(created ? "Created 'element_image' folder" : "Failed to create 'element_image' folder");
        }

        // Define the destination file path
        File destination = new File("src/main/resources/files/element_image/el_image.png");
        FileUtils.copyFile(screenshot, destination);

        return this;
    }

    /**
     * Compares two images pixel by pixel to determine if they are identical.
     *
     * @return true if the images are identical, false otherwise.
     */
    @SneakyThrows
    public boolean compareImages() {
        // Load the images to compare
        BufferedImage img1 = ImageIO.read(new File("src/main/resources/files/element_image/checked.png"));
        BufferedImage img2 = ImageIO.read(new File("src/main/resources/files/element_image/el_image.png"));

        // Compare images pixel by pixel
        int totalPixels = img1.getHeight() * img1.getWidth() / 4;
        int matchingPixels = 0;
        for (int height = 0; height < img1.getHeight() / 2; height++) {
            for (int width = 0; width < img1.getWidth() / 2; width++) {
                if (img1.getRGB(width, height) == img2.getRGB(width, height)) {
                    matchingPixels++;
                }
            }
        }

        // Return true if all compared pixels match
        return Math.round((float) matchingPixels / totalPixels) == 1;
    }
}
