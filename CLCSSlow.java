import java.util.*;


public class CLCSSlow {
  static int[][] arr = new int[2048][2048];

  static int LCS(char[] A, char[] B) {
    int m = A.length, n = B.length;
    int i, j;
    for (i = 0; i <= m; i++) arr[i][0] = 0;
    for (j = 0; j <= n; j++) arr[0][j] = 0;
    
    for (i = 1; i <= m; i++) {
      for (j = 1; j <= n; j++) {
        arr[i][j] = Math.max(arr[i-1][j], arr[i][j-1]);
        if (A[i-1] == B[j-1]) arr[i][j] = Math.max(arr[i][j], arr[i-1][j-1]+1);
      }
    }
    return arr[m][n];
  }

  static char[] cut(char[] A, int i) {
    String str = new String(A);
    String start = str.substring(i);
    String end = str.substring(0, i);
    return (start + end).toCharArray();
  }

  static int CLCS(char[] A, char[] B) {
    int m = A.length;
    int max = 0;
    for (int i=0; i<m; i++) {
      int temp = LCS(cut(A,i), B);
      if (temp > max) {
        max = temp;
      }
    }
    return max;
  }

  public static void main(String[] args) {
    Scanner s = new Scanner(System.in);
    int T = s.nextInt();
    for (int tc = 0; tc < T; tc++) {
      char[] A = s.next().toCharArray();
      char[] B = s.next().toCharArray();
      System.out.println(CLCS(A, B));
    }
  }
}