package example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class SeleniumTest {

    @Test
    public void testPageLoad() {
        // Your test code here
        WebDriverManager.chromedriver().driverVersion("131").setup();

        // Set Chrome options
        ChromeOptions options = new ChromeOptions();
          options.addArguments("--headless"); // Headless mode (non-GUI)
          // options.addArguments("--autoplay-policy=no-user-gesture-required");
          // options.addArguments("--no-sandbox");
          // options.addArguments("--disable-setuid-sandbox");

        // Initialize the WebDriver
        WebDriver driver = new ChromeDriver(options);

        try {
            // Set the viewport size (if necessary)
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1280, 720));

            // Load the URL
            String url = "https://newtonjavacloud.github.io/NewtonTale/";
            driver.get(url);

            // Wait until the DOM is loaded (wait for specific elements if necessary)
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            System.out.println("Page loaded, waiting for autoplay events...");

            // Monitor replay counter changes
            int lastReplayCount = 0;

            // Monitor function
            long testDuration = 5 * 60 * 1000; // 5 minutes in milliseconds
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < testDuration) {
                // Get the current replay count
                int currentReplayCount = 0;
                try {
                    WebElement replayCounterElement = driver.findElement(By.id("replay-counter"));
                    if (replayCounterElement != null) {
                        currentReplayCount = Integer.parseInt(replayCounterElement.getText());
                    }
                } catch (Exception e) {
                    // Handle case where element is not found
                    currentReplayCount = 0;
                }

                // Check if the replay count has changed
                if (currentReplayCount != lastReplayCount) {
                    System.out.println("Replay triggered! New count: " + currentReplayCount);
                    lastReplayCount = currentReplayCount;

                    // Log the details from the replay list
                    try {
                        WebElement replayList = driver.findElement(By.id("replay-list"));
                        if (replayList != null) {
                            for (WebElement replayItem : replayList.findElements(By.className("replay-item"))) {
                                System.out.println("Replay details: " + replayItem.getText().trim());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Replay list not found.");
                    }
                }

                // Wait for 1 second before checking again
                Thread.sleep(1000);
            }

            System.out.println("Soak test completed.");

        } catch (Exception e) {
            System.err.println("Error during soak test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
