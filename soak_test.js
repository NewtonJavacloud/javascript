const puppeteer = require('puppeteer');

(async () => {
    try {
        // Launch Puppeteer
        const browser = await puppeteer.launch({
            headless: true, // Use true for a non-UI browser
            args: [
                '--autoplay-policy=no-user-gesture-required', // Allow autoplay
                '--no-sandbox', // Add for running in Jenkins or Docker environments
                '--disable-setuid-sandbox' // Add for additional security context
            ]
        });

        const page = await browser.newPage();

        // Set the viewport size
        await page.setViewport({ width: 1280, height: 720 });

        // Load the URL
        const url = 'https://github.com/NewtonJavacloud/NewtonTale'; // Ensure this URL is accessible
        await page.goto(url, { waitUntil: 'domcontentloaded' }); // Wait for DOM to load

        console.log('Page loaded, waiting for autoplay events...');

        // Observe replay counter changes
        let lastReplayCount = 0;

        // Function to monitor changes in replay counter
        const monitorReplays = async () => {
            const currentReplayCount = await page.evaluate(() => {
                const replayCounterElement = document.getElementById('replay-counter');
                if (replayCounterElement) {
                    return parseInt(replayCounterElement.textContent, 10) || 0;
                }
                return 0; // Default to 0 if element or content is not found
            });

            // Check if the replay count has changed
            if (currentReplayCount !== lastReplayCount) {
                console.log(`Replay triggered! New count: ${currentReplayCount}`);
                lastReplayCount = currentReplayCount;

                // Log the details from the replay list
                const replayDetails = await page.evaluate(() => {
                    const replayList = document.querySelectorAll('#replay-list .replay-item');
                    return Array.from(replayList).map(item => item.textContent.trim());
                });

                console.log('Replay details:', replayDetails);
            }
        };

        // Monitor the page for 5 minutes
        const testDuration = 5 * 60 * 1000; // 5 minutes in milliseconds
        const startTime = Date.now();

        while (Date.now() - startTime < testDuration) {
            await monitorReplays();
            await new Promise(resolve => setTimeout(resolve, 1000)); // Check every second
        }

        console.log('Soak test completed.');

        // Close the browser
        await browser.close();
    } catch (error) {
        console.error('Error during soak test:', error);
        process.exit(1); // Exit with error code for CI/CD environments
    }
})();
