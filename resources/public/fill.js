function getSelectionOffsets(container) {
    const selection = window.getSelection();
    if (!selection || selection.rangeCount === 0) return null;

    const range = selection.getRangeAt(0);

    // Ensure selection is inside the container
    if (!container.contains(range.commonAncestorContainer)) {
        return null;
    }

    // Clone range to measure text length
    const preRange = range.cloneRange();
    preRange.selectNodeContents(container);
    preRange.setEnd(range.startContainer, range.startOffset);

    const start = preRange.toString().length;
    const end = start + range.toString().length;

    return { start, end };
}

function listenTextDisp() {
    const div = document.getElementById("text-disp");
    console.log('div', div);

    div.addEventListener("mouseup", () => {
        const offsets = getSelectionOffsets(div);
        if (offsets) {
            console.log(offsets.start, offsets.end);
        }
    });
}
