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

        List<String> lines = new ArrayList<>(); // 모든 입력 저장 ArrayList
        String line; // EOF 처리를 위한 String

        // 모든 입력 받기
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        // 전체 문자열 합친 후, 코드 블록 제거 및 배열로 변환
        String[] parts = removeCodeBlocks(String.join("\n", lines)).split("\n", -1);

        for (int i = 0; i < lines.size(); i++) {

            // 인라인 코드 제거 (`:cat:`)
            line = parts[i].replaceAll("(^|[^`])`[^`]+`([^`]|$)", "$1 $2");

            // ":cat:"이거나 🐱이면 YES
            if (hasExactWord(line)
                    || lines.get(i).contains("\uD83D\uDC31")) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }

    // 백틱 3개 이상으로 감싼 코드 블록 제거
    public static String removeCodeBlocks(String text) {
        Pattern pattern = Pattern.compile("```[\\s\\S]+?`{3,}");
        Matcher matcher = pattern.matcher(text);

        char[] chars = text.toCharArray();

        while (matcher.find()) {
            for (int i = matcher.start(); i < matcher.end(); i++) {
                // 줄바꿈을 제외한 문자를 공백으로 치환
                if (chars[i] != '\n') {
                    chars[i] = ' ';
                }
            }
        }
        return new String(chars);
    }

    // 고양이 이모지 찾기
    public static boolean hasExactWord(String text) {
        Pattern pattern = Pattern.compile(":[a-zA-Z0-9_+\\-]+:");
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
