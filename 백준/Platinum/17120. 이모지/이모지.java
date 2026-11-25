import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {

        Set<String> cats = new HashSet<>();
        cats.add(":cat:");
        cats.add(":cat2:");
        cats.add(":smiley_cat:");
        cats.add(":smile_cat:");
        cats.add(":joy_cat:");
        cats.add(":heart_eyes_cat:");
        cats.add(":smirk_cat:");
        cats.add(":kissing_cat:");
        cats.add(":scream_cat:");
        cats.add(":crying_cat_face:");
        cats.add(":pouting_cat:");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;

        while ((line = br.readLine()) != null) {
            if (hasExactWord(line, cats)) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }

    public static boolean hasExactWord(String text, Set<String> words) {
        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
