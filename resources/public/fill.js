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

    return [start, end];
}

const $ = x => document.querySelector(x);

function listenTextDisp() {
    const div = document.getElementById("text-disp");

    div.addEventListener("mouseup", () => {
        const offsets = getSelectionOffsets(div);
        if (offsets) {
            $('#i1').value = offsets[0];
            $('#i2').value = offsets[1];
        }
        console.log(offsets);
        $('#append').click();
    });
}

document.addEventListener('keydown', (event) => {
    if (event.key === 'z') {
        $('#drop').click();
    }
});
