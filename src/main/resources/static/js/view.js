let pgs;
let lang;

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

function getOrDefault(item, def) {
    let result = window.localStorage.getItem(item);

    if (result == null) {
        result = def;
        window.localStorage.setItem(item, def);
    }
    return result;
}

function getParagraphs() {
    let paragraphs = Array.from(document.getElementsByTagName('p'));
    for (let i of paragraphs) {
        if (!i.id.startsWith("L")) {
            paragraphs = paragraphs.filter(item => item !== i);
        }
    }
    return paragraphs;
}

function removeHighlights(paragraphs) {
    for (let p of paragraphs) {
        p.style.removeProperty("color");
        p.querySelector("span").innerHTML = p.querySelector("span").innerText;
    }
}

function handleChecker(elem) {
    removeHighlights(pgs);
    if (elem.checked) {
        document.getElementById("checker").checked = true;
        window.localStorage.setItem("terminal", "true");
        termHighlight(pgs);
    } else {
        window.localStorage.setItem("terminal", "false");
        for (let i = 0; i < pgs.length; i++) {
            pgs[i].querySelector("span").innerHTML = hljs.highlight(pgs[i].querySelector("span").innerHTML, {language: lang}).value;
        }
    }
}

window.onload = function () {
    pgs = getParagraphs();
    //config
    let textList = [];
    for (let i = 0; i < pgs.length; i++) {
        // noinspection JSUnfilteredForInLoop
        textList.push(pgs[i].querySelector("span").innerText);
    }
    lang = hljs.highlightAuto(textList.join("\n")).language;
    //config end

    if (window.location.hash !== "") {
        console.log("line hash found, scrolling to line " + window.location.hash.replace("#L", ""));
        scrollAndHighlight(window.location.hash.replace("#", ""));
    }

    if (getOrDefault("terminal", "false") === "true") {
        document.getElementById("checker").checked = true;
        termHighlight(pgs);
    } else {
        for (let i = 0; i < pgs.length; i++) {
            pgs[i].querySelector("span").innerHTML = hljs.highlight(pgs[i].querySelector("span").innerHTML, {language: lang}).value;
        }
    }
}