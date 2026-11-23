import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(br.readLine()); // n개의 회의
        int[][] arr = new int[n][2]; // 각 회의 저장 배열

        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());

            arr[i][0] = Integer.parseInt(st.nextToken());
            arr[i][1] = Integer.parseInt(st.nextToken());
        }

        Arrays.sort(arr, Comparator
                .comparingInt((int[] a) -> a[1])
                .thenComparingInt(a -> a[0]));

        int end = 0;
        int count = 0;

        for (int[] meeting : arr) {
            if (meeting[0] >= end) {
                end = meeting[1];
                count++;
            }
        }

        System.out.println(count);
    }
}
