window.onload = function () {
    let options = {
        strings: ['Your next-gen <a class="text-decoration-none" style="color: #ff983d;">pastebin</a>.'],
        showCursor: true,
        cursorChar: '_',
        autoInsertCss: true,
        contentType: 'html',
        typeSpeed: 80,
        backSpeed: 30,
        backDelay: 2000,
        loop: false
    };

    let typed = new Typed('#anim', options);

    const area = document.getElementById('input2');
    area.addEventListener('dragover', handleDragOver, false);
    area.addEventListener('drop', handleFileSelect, false);
}

function handleFileSelect(evt) {
    evt.stopPropagation();
    evt.preventDefault();

    const files = evt.dataTransfer.files
    let reader = new FileReader();
    reader.onload = function (event) {
        document.getElementById('input2').value = event.target.result;
    }
    reader.readAsText(files[0], "UTF-8");
}

function handleDragOver(evt) {
    evt.stopPropagation();
    evt.preventDefault();
    evt.dataTransfer.dropEffect = 'copy';
}