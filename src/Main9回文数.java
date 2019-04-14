public class Main9回文数 {

    class Solution {


        public boolean isPalindrome(int x) {

            if (x<0)
                return false;


            int begin = x;
            int revers = 0;

            while (begin > 0) {


                int yu = begin % 10;
                revers = revers * 10 + yu;


                begin = begin / 10;

            }


            return revers == x;
        }
    }
}
