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
            String address = matcher.group(1);
            String linkLabel = matcher.group(2);

            if (linkLabel == null) {
                linkLabel = address.contains("\uD83D\uDC31")
                        ? "\uD83D\uDC31" : "";
            } else {
                linkLabel = linkLabel.trim();

                // 주소와 표시 문구가 동일하면 이모지 변환 제외
                if (linkLabel.equals(address)
                        || linkLabel.equals(address.replaceFirst("^\\w+://", ""))) {
                    linkLabel = linkLabel.contains("\uD83D\uDC31")
                            ? "\uD83D\uDC31" : "";
                }
            }

            matcher.appendReplacement(visibleText, Matcher.quoteReplacement(" " + linkLabel + " "));
        }

        matcher.appendTail(visibleText);

        return visibleText.toString();
    }

    // 고양이 이모지 찾기(:dog:cat:은 안됨, :dog::cat:은 가능, :아:cat: 안됨)
    public static boolean hasExactWord(String text) {
        Pattern pattern = Pattern.compile(
                ":[[\\p{L}\\p{N}_+\\-]&&[\\x{0}-\\x{FFFF}]]+:"
        );
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            // 대소문자 구분 없이 검사 (:cat:, :CAT:)
            if (matcher.group().equalsIgnoreCase(":cat:")) {
                return true;
            }
        }

        return false;
    }
}
