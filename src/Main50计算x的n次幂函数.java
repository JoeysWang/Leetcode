/**
 * 分治 ，分解成 Y=x的n/2次方, result=Y*Y
 * 需要注意负数次方和 奇数次方
 */
public class Main50计算x的n次幂函数 {


    public double myPow(double x, int n) {
        if (n == 0)
            return 1;
        if (n == 1)
            return x;
        if (n == -1)
            return 1 / x;

        if (n % 2 != 0) {
            double result = myPow(x, (n - 1) / 2);
            return result * result * x;
        } else {
            double result = myPow(x, n / 2);
            return result * result;
        }
    }


}
