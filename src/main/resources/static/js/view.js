function scrollToLine(elem) {
    // document.getElementById(elem).scrollIntoView(true);
    window.scroll(0, window.scrollY + document.getElementById(elem).getBoundingClientRect().top - 64);
}

function highlightLine(elem) {
    document.getElementById(elem).classList.toggle("highlighted");
    document.getElementById(elem).classList.toggle("d-inline");
}

function scrollAndHighlight(elem) {
    scrollToLine(elem);
    highlightLine(elem);
}

function pickLine(elem) {
    if (window.location.hash !== "") {
        highlightLine(window.location.hash.replace("#", ""));
    }
    window.location.hash = '#' + elem;
    scrollAndHighlight(elem);
}

function termHighlight(paragraphs) {
    for (let i = 0; i < paragraphs.length; i++) {
        let elem = paragraphs[i];
        if (elem.innerHTML.includes("WARN")) {
            elem.style.setProperty("color", "yellow", "important");
        } else if (elem.innerHTML.includes("ERROR") || elem.innerHTML.includes("SEVERE")) {
            elem.style.setProperty("color", "red", "important");
        } else {
            const result = [...elem.innerHTML.matchAll(/[\u001b\u009b][[()#;?]*(?:[0-9]{1,4}(?:;[0-9]{0,4})*)?[0-9A-ORZcf-nqry=><]/g)];
            if (result.length > 0) {
                let match = result[0][0];
                if (match.includes("30")) {
                    elem.style.setProperty("color", "black", "important");
                } else if (match.includes("31")) {
                    elem.style.setProperty("color", "red", "important");
                } else if (match.includes("32")) {
                    elem.style.setProperty("color", "green", "important");
                } else if (match.includes("33")) {
                    elem.style.setProperty("color", "yellow", "important");
                } else if (match.includes("34")) {
                    elem.style.setProperty("color", "blue", "important");
                } else if (match.includes("35")) {
                    elem.style.setProperty("color", "magenta", "important");
                } else if (match.includes("36")) {
                    elem.style.setProperty("color", "cyan", "important");
                } else if (match.includes("37")) {
                    elem.style.setProperty("color", "white", "important");
                }
                elem.innerHTML = elem.innerHTML.replace(match, "<span class='hidden'>" + match + "</span>>")
            }
        }
    }
}

function normalHighlight(paragraphs) {
    let text = paragraphs.map(x => x.getElementsByTagName("span")[0].innerText).join('\n');
    let lang = hljs.highlightAuto(text).lang;
}

function getOrDefault(item, def) {
    let result = window.localStorage.getItem(item);

    if (result == null) {
        result = def;
        window.localStorage.setItem(item, def);
    }
    return result;
}

function getParagraphs() {
    let paragraphs = document.getElementsByTagName("p");
    for (let p of paragraphs) {
        if (!p.id.startsWith("L")) {
            paragraphs = removeItemOnce(paragraphs, p);
        }
    }
    return paragraphs;
}

function removeHighlights(paragraphs) {
    for (let p of paragraphs) {
        p.removeAttribute("color");
        p.getElementsByTagName("span")[0].innerHTML = p.getElementsByTagName("span")[0].innerText;
    }
}

function removeItemOnce(arr, value) {
    let index = arr.indexOf(value);
    if (index > -1) {
        arr.splice(index, 1);
    }
    return arr;
}

window.onload = function () {
    if (window.location.hash !== "") {
        console.log("line hash found, scrolling to line " + window.location.hash.replace("#L", ""));
        scrollAndHighlight(window.location.hash.replace("#", ""));
    }

    let paragraphs = getParagraphs();
    if (getOrDefault("terminal", "false") === "true") {
        termHighlight(paragraphs);
    } else {

    }
}