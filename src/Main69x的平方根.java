import java.util.Stack;

public class Main69x的平方根 {


    public static void main(String[] args) {



        mySqrt(8);
    }

    /**
     * 函数是单调递增的，可以使用二分查找
     */
    public static int mySqrt(int x) {


        return findSolution(0, x, x);
    }

    private static int findSolution(int left, int right, int x) {
        int mid = (right + left) / 2;

        int resut = mid * mid;

        if (resut == x || x - resut < 1)
            return mid;
        if (resut > x)
            return findSolution(left, mid, x);
        if (resut < x)
            return findSolution(mid, right, x);

        return 1;
    }
}
