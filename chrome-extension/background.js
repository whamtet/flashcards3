chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.type === "TRIGGER_SCREENSHOT") {
    console.log('fuck');
  }
});

async function handleScreenshot() {
  try {
    // 1. Find matching tab
    const tabs = await chrome.tabs.query({});
    let appendId = null;

    const regex = /Edit Slideshow (\d+)/;

    for (const t of tabs) {
      if (t.title) {
        const match = t.title.match(regex);
        if (match) {
          appendId = match[1];
          break;
        }
      }
    }

    // 2. Capture screenshot
    const dataUrl = await chrome.tabs.captureVisibleTab(null, {
      format: "png"
    });

    const blob = await (await fetch(dataUrl)).blob();

    // 3. Build URL
    const url = new URL("https://flashcards.simpleui.io/api/screenshot");
    if (appendId) {
      url.searchParams.set("append_id", appendId);
    }

    // 4. Send
    const formData = new FormData();
    formData.append("file", blob, "screenshot.png");

    await fetch(url.toString(), {
      method: "POST",
      body: formData
    });

    console.log("Screenshot uploaded", { appendId });

  } catch (err) {
    console.error("Error:", err);
  }
}
