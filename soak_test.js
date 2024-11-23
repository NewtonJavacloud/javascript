const puppeteer = require('puppeteer');

(async () => {
    // Launch Puppeteer
    const browser = await puppeteer.launch({
        headless: false, // Set to true if you don't need a visible UI
        args: ['--autoplay-policy=no-user-gesture-required'] // Allow autoplay
    });

    const page = await browser.newPage();

    // Set the viewport size
    await page.setViewport({ width: 1280, height: 720 });

    // Load the HTML file locally or from a server
    const url = 'https://github.com/NewtonJavacloud/NewtonTale'; // Use this if hosted
    await page.goto(url); // Or replace with `url` if it's hosted

    console.log('Page loaded, waiting for autoplay events...');

    // Observe replay counter changes
    let lastReplayCount = 0;

    // Function to monitor changes in replay counter
    const monitorReplays = async () => {
        const currentReplayCount = await page.evaluate(() => {
            return parseInt(document.getElementById('replay-counter').textContent, 10);
        });

        // Check if the replay count has changed
        if (currentReplayCount !== lastReplayCount) {
            console.log(`Replay triggered! New count: ${currentReplayCount}`);
            lastReplayCount = currentReplayCount;

            // Log the details from the replay list
            const replayDetails = await page.evaluate(() => {
                const replayList = document.querySelectorAll('#replay-list .replay-item');
                return Array.from(replayList).map(item => item.textContent);
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
})();
