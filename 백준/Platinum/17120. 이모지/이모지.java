import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String line;

        while ((line = br.readLine()) != null) {
            if (hasExactWord(line, ":cat:")) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }

    // 고양이 이모지 찾기
    public static boolean hasExactWord(String text, String word) {
        if (text == null || word == null || word.isEmpty()) {
            return false;
        }

        int textLen = text.length();
        int wordLen = word.length();
        int index = text.indexOf(word);

        // ":cat:"의 앞뒤에 문자 없는지 체크
        while (index != -1) {
            boolean isStartOk = (index == 0) || Character.isWhitespace(text.charAt(index - 1));
            int endIndex = index + wordLen;
            boolean isEndOk = (endIndex == textLen) || Character.isWhitespace(text.charAt(endIndex));

            if (isStartOk && isEndOk) {
                return true;
            }

            index = text.indexOf(word, index + 1);
        }

        return false;
    }
}
