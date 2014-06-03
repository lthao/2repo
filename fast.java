import java.util.*;
import java.lang.*;

class Node 
{
  int y;
  int x;
}

class Path 
{
  int start_x;
  int start_y;
  int end_x;
  int end_y;
  int diagonals;
}

public class fast {

  static int best_path;
  static int A_len;
  static int B_len;
  static int[][] global_arr = new int[2048][2048];
  static char[] wrapped_A;

  // GetPathWithoutBounds is used only for the first path in LCS.
  // It doens't have any upper/lower bounds so we don't need to 
  // perform any checks. The reguler GetPath performs bounds checking.
  // This func stores and classifies the nodes of the path as part of
  // the next iteration's upper/lower bound as it goes.
  
  static void GetPathWithoutBounds(int i, int j, char[] A, char[] B, HashMap<Integer, Integer> upper_path_bounds, 
    HashMap<Integer, Integer> lower_path_bounds)
  {
    Integer result = upper_path_bounds.get(i);
    if (result == null)
    {
      upper_path_bounds.put(i, j);
    }
    lower_path_bounds.put(i+A_len, j);

    if (i==0)
    {
      if (j==0)
      {
        return;
      }
      else
      {
        GetPathWithoutBounds(i, j-1, A, B, upper_path_bounds, lower_path_bounds);
      }
    } 
    if (j==0)
    {
      if (i==0)
      {
        return;
      }
      else
      {
        GetPathWithoutBounds(i-1, j, A, B, upper_path_bounds, lower_path_bounds);
      }
    }
    if ((j!=0) && (i!=0))
    {
      if (A[i-1]==B[j-1])
      {
        GetPathWithoutBounds(i-1, j-1, A, B, upper_path_bounds, lower_path_bounds);
      } 
      else if (global_arr[i][j-1] >= global_arr[i-1][j]) 
      {
        GetPathWithoutBounds(i, j-1, A, B, upper_path_bounds, lower_path_bounds);
      }
      else
      {
        GetPathWithoutBounds(i-1, j, A, B, upper_path_bounds, lower_path_bounds);
      }
    }
  }

  // GetPath also stores and classifies the nodes of the path as part of
  // the next iteration's upper/lower bound as it goes. It stores these in
  // middle_path_upper_bounds and middle_path_lower_bounds
  static void GetPath(int i, int j, char[] A, char[] B, HashMap<Integer, Integer> middle_path_upper_bounds, HashMap<Integer, Integer> middle_path_lower_bounds, int offset, 
    HashMap<Integer, Integer> upper_path_bounds, HashMap<Integer, Integer> lower_path_bounds) 
  {
    Integer result = middle_path_upper_bounds.get(i+offset);
    if (result == null)
    {
      middle_path_upper_bounds.put(i+offset, j);
    }
    middle_path_lower_bounds.put(i+offset, j);

    if (i==0)
    {
      if (j==0)
      {
        return;
      }
      else
      {
        GetPath(i, j-1, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
      }
    } 
    if (j==0)
    {
      if (i==0)
      {
        return;
      }
      else
      {
        GetPath(i-1, j, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
      }
    }
    if ((j!=0) && (i!=0))
    {
      
      Integer upper_j = upper_path_bounds.get(i - 1 + offset);
      Integer lower_j = lower_path_bounds.get(i + offset);
      if (upper_j == null)
      {
        upper_j = B_len;
      }
      if (lower_j == null)
      {
        lower_j = 0;
      }

      if ((j <= upper_j) && (j > lower_j))
      {
        if (A[i-1]==B[j-1])
        {
          GetPath(i-1, j-1, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
        } 
        else if (global_arr[i][j-1] >= global_arr[i-1][j]) 
        {
          GetPath(i, j-1, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
        }
        else
        {
          GetPath(i-1, j, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
        }
      }
      else if ((j > upper_j + 1) && (j > lower_j))
      {
        GetPath(i, j-1, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
      }
      else if (j==lower_j)
      {
        if (A[i-1]==B[j-1])
        {
          GetPath(i-1, j-1, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
        } 
        else 
        {
          GetPath(i-1, j, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
        }
      }
      else if ((j==upper_j+1) && (j>lower_j))
      {
        if (A[i-1]==B[j-1])
        {
          GetPath(i-1, j-1, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
        } 
        else 
        {
          GetPath(i, j-1, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
        }
      }
    }
  }

  static int SingleShortestPath(char[] A, char[] B, HashMap<Integer, Integer> upper_path_bounds, 
    HashMap<Integer, Integer> lower_path_bounds, int offset, HashMap<Integer, Integer> middle_path_upper_bounds,
    HashMap<Integer, Integer> middle_path_lower_bounds)
  {
    int m = A_len; 
    int n = B_len;

    // init matrix
    int zero_column_max = upper_path_bounds.get(offset);
    for (int j = 0; j <= zero_column_max; j++) global_arr[0][j] = 0;

    // not needed? column will always be 0
    // int zero_row_index = offset;
    // while(zero_row_index <= offset+A_len)
    // {
    //   Integer index = lower_path_bounds.get(zero_row_index);
    //   if(index==null)
    //   {
    //     global_arr[zero_row_index-offset][0] = 0;
    //     zero_row_index += 1;
    //   }
    //   else if (index==0)
    //   {
    //     global_arr[zero_row_index-offset][0] = 0;
    //     zero_row_index += 1;
    //   }
    //   else if (index!=0)
    //   {
    //     break;
    //   }
    // }

    // populate array using bounds
    for (int i = 1; i <= m; i++)
    {
      Integer upper_j = upper_path_bounds.get(i-1 + offset);
      Integer c = upper_path_bounds.get(i+offset);
      
      if (c == null)
      {
        c = n;
      }

      Integer lower_j = lower_path_bounds.get(i-1 + offset);
      Integer start_j = lower_path_bounds.get(i+offset);

      if ((start_j == null) || (start_j == 0))
      {
        start_j = 1;
      }
      
      if (upper_j == null)
      {
        upper_j = n;
      }
      if (lower_j == null)
      {
        lower_j = 0;
      }

      for (int j = start_j; j <= c; j++)
      {
        if (j < (upper_j+1) && j > lower_j) 
        {
          if (A[i-1]==B[j-1])
          {
            global_arr[i][j] = 1 + global_arr[i-1][j-1];
          }
          else
          {
            global_arr[i][j] = Math.max(global_arr[i][j-1], global_arr[i-1][j]);
          }
        } 
        else if (j > (upper_j+1) && j > lower_j)
        {
          global_arr[i][j] = global_arr[i][j-1];
        }
        else if (j == (upper_j+1) && j > lower_j)
        {
          if (A[i-1]==B[j-1])
          {
            global_arr[i][j] = 1 + global_arr[i-1][j-1];
          }
          else
          {
            global_arr[i][j] = global_arr[i][j-1];
          }
        }
        else if (j < (upper_j + 1) && j == lower_j)
        {
          global_arr[i][j] = global_arr[i-1][j];
        }
      }
    }

    GetPath(m, n, A, B, middle_path_upper_bounds, middle_path_lower_bounds, offset, upper_path_bounds, lower_path_bounds);
    return global_arr[m][n];
  }

  static void FindShortestPaths(char[] A, char[] B, HashMap<Integer, Integer> upper_path_bounds, 
    HashMap<Integer, Integer> lower_path_bounds, int upper_row_index, int lower_row_index)
  {
    if ((lower_row_index - upper_row_index) <= 1) return;
    int middle_row_index = (upper_row_index+lower_row_index)/2;

    // this is the cut string
    char[] mid_str = new char[A_len];
    System.arraycopy(wrapped_A, middle_row_index, mid_str, 0, A_len);

    // these are the bounds created by the shortest path using the cut string
    HashMap<Integer, Integer> middle_path_upper_bounds = new HashMap<Integer, Integer>(A_len+1, 1);
    HashMap<Integer, Integer> middle_path_lower_bounds = new HashMap<Integer, Integer>(A_len+1, 1);
    int num_diags = SingleShortestPath(mid_str, B, upper_path_bounds, lower_path_bounds, middle_row_index, middle_path_upper_bounds, middle_path_lower_bounds);
    
    // performs update
    if (num_diags > best_path) best_path = num_diags;

    FindShortestPaths(A, B, upper_path_bounds, middle_path_lower_bounds, upper_row_index, middle_row_index);
    FindShortestPaths(A, B, middle_path_upper_bounds, lower_path_bounds, middle_row_index, lower_row_index);
  }

  static int LCS(char[] A, char[] B, HashMap<Integer, Integer> upper_path_bounds, HashMap<Integer, Integer> lower_path_bounds) 
  {
    int m = A_len;
    int n = B_len;
    int i, j;
    for (i = 1; i <= m; i++)
    {
      for (j = 1; j <= n; j++) 
      {
        if (A[i-1] == B[j-1]) 
        {
          global_arr[i][j] = global_arr[i-1][j-1]+1;
        }
        else
        {
          global_arr[i][j] = Math.max(global_arr[i-1][j], global_arr[i][j-1]);
        }
      }
    }

    GetPathWithoutBounds(m, n, A, B, upper_path_bounds, lower_path_bounds);
    return global_arr[m][n];
  }

  static char[] cut(char[] A, int i) 
  {
    String str = new String(A);
    String start = str.substring(i);
    String end = str.substring(0, i);
    return (start + end).toCharArray();
  }

  static char[] modcut(int i) 
  {
    char[] temp = new char[A_len];
    System.arraycopy(wrapped_A, i, temp, 0, A_len);
    return temp;
  }

  public static void main(String[] args) 
  {
    Scanner s = new Scanner(System.in);
    int T = s.nextInt();
    for (int tc = 0; tc < T; tc++) 
    {
      String temp_str = s.next();
      wrapped_A = (temp_str+temp_str).toCharArray();
      char[] A = temp_str.toCharArray();
      char[] B = s.next().toCharArray();

      A_len = A.length;
      B_len = B.length;

      //global_arr = new int[A_len+1][B_len+1];

      HashMap<Integer, Integer> upper_path_bounds = new HashMap<Integer, Integer>(A_len+1, 1);
      HashMap<Integer, Integer> lower_path_bounds = new HashMap<Integer, Integer>(A_len+1, 1);
      best_path = LCS(A, B, upper_path_bounds, lower_path_bounds);

      FindShortestPaths(A, B, upper_path_bounds, lower_path_bounds, 0, A_len);
      System.out.println(best_path);
    }
  }
}