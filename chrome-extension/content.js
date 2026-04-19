function attachListener() {
  console.log('attaching listener');
  document.removeEventListener("keydown", handler, true);
  document.addEventListener("keydown", handler, true);
}

function handler(event) {
  if (event.repeat) return;

  if (event.key === "c") {
    chrome.runtime.sendMessage({ type: "TRIGGER_SCREENSHOT" });
  }
}

// 1. After full load
window.addEventListener("load", () => {
  setTimeout(attachListener, 5000);
});

console.log("Extension running in:", location.href);
