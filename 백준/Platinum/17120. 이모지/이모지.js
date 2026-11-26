const fs = require("fs");

const CAT = "\u{1F431}";

const NAME_CHAR = /^[a-zA-Z0-9àáâäãèéêëíîïóöôõœúùûüÿçßñÀÁÂÄÃÈÉÊËÍÎÏÓÖÔÕŒÚÙÛÜŸÇÑ\u3041-\u3096\u30A0-\u30FF\uFF61-\uFF9F\u3400-\u4DB5\u4E00-\u9FCB\u8C48-\uFA6A\u2E80-\u2FD5\uFF41-\uFF5A\uFF10-\uFF19_+'\uFF3F\uFF0B\u30FC\u3005-]$/;

const LINK = /<([^>\r\n]*)>/g;

const URL = /(?:ftp|https?|file):\/\/[a-z0-9.-]+[a-z]+(?::[a-z0-9]*)?\/?[@a-z0-9._?,'()*/\\+&;%:!$#=~-]*|\bwww\.[a-z0-9.-]+[a-z]+(?::[a-z0-9]*)?\/?[@a-z0-9._?,'()*/\\+&;%:!$#=~-]*/i;

const IPV6 = /(?:^|[^:A-F0-9])(?:[A-F0-9]{1,4}:){7}[A-F0-9]{1,4}(?:[^:A-F0-9]|$)/gi;

// 코드 블록 제거 (줄바꿈과 실제 고양이 이모지 유지)
function maskFencedCode(input) {
    return input.replace(/```[\s\S]+?`{3,}/g, block =>
        block.replace(/🐱|[^\n]/gu, ch => ch === CAT ? ch : " "));
}

// 일반 문자를 객체로 저장
function appendText(nodes, text) {
    for (let i = 0; i < text.length; i++) {
        nodes.push({ type: "text", value: text[i] });
    }
}

// 링크의 표시 문구 처리
function parseLink(content) {
    const pipe = content.indexOf("|");
    const address = pipe === -1 ? content : content.slice(0, pipe);

    let label = pipe === -1 ? address : content.slice(pipe + 1);

    if (!label) label = address;

    label = label.trim();

    // 특수 명령어 및 멘션 처리
    if (/^[#@!%]/.test(address) || address.startsWith("<")) {
        return {
            type: "link",
            cat: label.includes(CAT),
            consumed: false
        };
    }

    // 주소와 표시 문구가 동일한 경우
    const addressWithoutProtocol = address.replace(/^\w+:\/\//, "");

    if (label === address || label === addressWithoutProtocol) {
        return {
            type: "link",
            cat: label.includes(CAT),
            consumed: false
        };
    }

    label = label.replace(URL, " ");

    return {
        type: "link",
        cat: hasCat(label, false),
        consumed: false
    };
}

// 일반 문자와 링크를 구분하여 저장
function tokenize(text, parseLinks) {
    const nodes = [];

    if (!parseLinks) {
        appendText(nodes, text);
        return nodes;
    }

    let last = 0;

    for (const match of text.matchAll(LINK)) {
        appendText(nodes, text.slice(last, match.index));

        nodes.push(parseLink(match[1]));

        last = match.index + match[0].length;
    }

    appendText(nodes, text.slice(last));

    return nodes;
}

// 인라인 코드 및 IPv6 처리
function markCode(nodes) {
    const view = nodes.map(node =>
        node.type === "text" ? node.value : "\uFFFC"
    ).join("");

    const inline = /(?:^|(?<=[\s~_*\\/?.,\-!^;:{(\[%$#+=\u2000-\u206F\u2E00-\u2E7F"\uFFFC]))(?<!`)`\s*\S[^`]*?`(?!`)|(?<!｀)｀\s*\S[^｀]*?｀(?!｀)/g;

    for (const match of view.matchAll(inline)) {
        for (let i = match.index; i < match.index + match[0].length; i++) {
            if (nodes[i].type === "text") {
                nodes[i].type = "code";
            }
        }
    }

    const plainView = nodes.map(node =>
        node.type === "text" ? node.value : " "
    ).join("");

    for (const match of plainView.matchAll(IPV6)) {
        for (let i = match.index; i < match.index + match[0].length; i++) {
            if (nodes[i].type === "text") {
                nodes[i].type = "code";
            }
        }
    }
}

function isColon(node) {
    return node && node.type === "text" && node.value === ":";
}

function isNameChar(node) {
    return node && (
        node.type === "link" ||
        (node.type === "text" && NAME_CHAR.test(node.value))
    );
}

// 고양이 이모지 검사
function hasCat(text, parseLinks = true) {
    // 유니코드 줄바꿈 처리
    if (/[\u2028\u2029]/.test(text)) {
        return text.split(/[\u2028\u2029]/)
            .some(part => hasCat(part, parseLinks));
    }

    const nodes = tokenize(text, parseLinks);

    // 이하 기존 코드 유지

    markCode(nodes);

    // 실제 고양이 이모지는 코드 내부에서도 유지
    const originalText = nodes.map(n => n.value || " ").join("");

    if (originalText.includes(CAT)) return true;

    for (let i = 0; i < nodes.length; i++) {
        if (!isColon(nodes[i])) continue;

        let end = i + 1;

        while (isNameChar(nodes[end])) {
            end++;
        }

        if (end === i + 1 || !isColon(nodes[end])) continue;

        const nameNodes = nodes.slice(i + 1, end);

        const name = nameNodes.map(node => node.value || "").join("");

        const linkNodes = nameNodes.filter(node => node.type === "link");

        if (!linkNodes.length && name.toLowerCase() === "cat") {
            return true;
        }

        // 이모지 이름에 포함된 링크 처리
        for (const node of linkNodes) {
            node.consumed = true;
        }

        i = end;
    }

    return nodes.some(node =>
        node.type === "link" && !node.consumed && node.cat
    );
}

function main() {
    const input = fs.readFileSync(0, "utf8")
        .replace(/\r\n|\r/g, "\n")
        .replace(/\n$/, "");

    const lines = maskFencedCode(input).split("\n");

    console.log(
        lines.map(line => hasCat(line) ? "YES" : "NO").join("\n")
    );
}

main();