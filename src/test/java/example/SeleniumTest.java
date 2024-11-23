package example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SeleniumTest {

    @Test
    public void youtube() {
        WebDriverManager.chromedriver().driverVersion("131").setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Enable headless mode
        options.addArguments("--disable-software-rasterizer"); // Disable software rasterizer
        options.addArguments("--disable-gpu"); // Disable GPU hardware acceleration
        options.addArguments("--no-sandbox"); // Disable sandbox
        options.addArguments("--autoplay-policy=no-user-gesture-required"); // Auto-play videos
        options.addArguments("--start-maximized"); // Auto play videos

        // Initialize the WebDriver
        WebDriver driver = new ChromeDriver(options);

        try {
            // Open the first window with the YouTube video
            driver.get("https://youtu.be/-8YZyg90MPs?si=0Ackk_-WuF5E3bEd");

            // Wait for the page and video to load
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // Set video playback speed to 2x using JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.querySelector('video').playbackRate = 2.0;");

            // Get the handle of the first window
            String mainWindowHandle = driver.getWindowHandle();

            // Open 2 more windows (3 in total)
            for (int i = 0; i < 2; i++) {
                // Open a new tab
                js.executeScript("window.open('https://youtu.be/-8YZyg90MPs?si=0Ackk_-WuF5E3bEd');");
                // Switch to the new tab
                List<String> windowHandles = List.copyOf(driver.getWindowHandles());
                driver.switchTo().window(windowHandles.get(windowHandles.size() - 1));

                // Set video playback speed to 2x on the new tab
                js.executeScript("document.querySelector('video').playbackRate = 2.0;");
            }

            // Continuously track and print the video time for each window
            while (true) {
                int windowNumber = 1; // Counter for window numbers
                // Loop through all open windows and track the video time for each
                for (String windowHandle : driver.getWindowHandles()) {
                    driver.switchTo().window(windowHandle);

                    // Get the current video time in seconds
                    Double currentTime = (Double) js.executeScript("return document.querySelector('video').currentTime;");

                    // Convert Double to Long by rounding
                    Long roundedTime = Math.round(currentTime);

                    System.out.println("Window " + windowNumber + " - Current Video Time: " + roundedTime + " seconds");

                    // Increment the window number
                    windowNumber++;
                }

                // Wait for 30 seconds before printing again
                Thread.sleep(30000); // 30 seconds
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Close the browser after execution
            driver.quit();
        }
    }
}
