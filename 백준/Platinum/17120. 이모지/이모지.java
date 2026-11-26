import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        List<String> inputLines = new ArrayList<>(); // 모든 입력 저장 ArrayList
        String inputLine; // EOF 처리를 위한 String

        // 전체 메시지 입력
        while ((inputLine = br.readLine()) != null) {
            inputLines.add(inputLine);
        }

        // 전체 문자열 한 줄로 합침
        String fullInput = String.join("\n", inputLines);

        // 코드 블록 제거 후 줄마다 배열에 저장
        String[] linesWithoutCodeBlocks = removeCodeBlocks(fullInput).split("\n", -1);

        for (String line : linesWithoutCodeBlocks) {
            String processedLine = removeInlineCode(line); // 인라인 코드 제거
            processedLine = removeLinks(processedLine); // 링크 제거

            // ":cat:" 이거나 🐱이면 YES
            if (hasExactWord(processedLine) || processedLine.contains("\uD83D\uDC31")) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }

    // 코드 블록 제거(```:cat:```)
    public static String removeCodeBlocks(String text) {
        return removeCode(text, "```[\\s\\S]+?`{3,}");
    }

    // 인라인 코드 제거(`:cat:`)
    public static String removeInlineCode(String text) {
        return removeCode(text, "(?<!`)`\\s*\\S[^`]*?`(?!`)(?=[\\s.,\\])}!?\\-=]|$)");
    }

    // 백틱 3개로 감싼 코드 블록 제거
    public static String removeCode(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        char[] chars = text.toCharArray();

        while (matcher.find()) {
            for (int i = matcher.start(); i < matcher.end(); i++) {
                // 🐱이거나 줄바꿈은 유지
                if (text.startsWith("\uD83D\uDC31", i)) {
                    i++;
                } else if (chars[i] != '\n') {
                    chars[i] = ' ';
                }
            }
        }
        return new String(chars);
    }

    // 링크 주소 제거 및 표시 문구 유지(https://example.com|:cat: -> :cat:)
    public static String removeLinks(String text) {
        Pattern pattern = Pattern.compile("<([^|>]*)(?:\\|([^>]*))?>");
        Matcher matcher = pattern.matcher(text);

        StringBuilder visibleText = new StringBuilder(); // 링크 제거 및 표시 문구 유지된 문자열 저장 변수

        while (matcher.find()) {
            String linkLabel = matcher.group(2);

            // 표시 문구만 남김(링크의 주소 제거), 주소에 🐱가 있으면 🐱는 남김
            if (linkLabel == null) {
                linkLabel = matcher.group(1).contains("\uD83D\uDC31") ? "\uD83D\uDC31" : "";
            }

            matcher.appendReplacement(visibleText, Matcher.quoteReplacement(" " + linkLabel + " "));
        }

        matcher.appendTail(visibleText);

        return visibleText.toString();
    }

    // 고양이 이모지 찾기(:dog:cat:은 안됨, :dog::cat:은 가능, :아:cat: 안됨)
    public static boolean hasExactWord(String text) {
        // Slack 이모지 이름의 허용 문자
        String emojiChars = "a-zA-Z0-9"
                + "àáâäãèéêëíîïóöôõœúùûüÿçßñÀÁÂÄÃÈÉÊËÍÎÏÓÖÔÕŒÚÙÛÜŸÇÑ"
                + "\\u3041-\\u3096\\u30A0-\\u30FF\\uFF61-\\uFF9F"
                + "\\u3400-\\u4DB5\\u4E00-\\u9FCB\\u8C48-\\uFA6A"
                + "\\u2E80-\\u2FD5\\uFF41-\\uFF5A\\uFF10-\\uFF19"
                + "_+'\\uFF3F\\uFF0B\\u30FC\\u3005\\-"
                + "\\x{10000}-\\x{10FFFF}";

        Pattern pattern = Pattern.compile(":[" + emojiChars + "]+:");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            if (matcher.group().equalsIgnoreCase(":cat:")) {
                return true;
            }
        }

        return false;
    }
}
