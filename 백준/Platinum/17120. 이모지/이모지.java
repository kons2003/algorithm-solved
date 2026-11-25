import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.contains(":cat:") || line.contains("\uD83D\uDC31")) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }
}
