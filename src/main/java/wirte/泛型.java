package wirte;

import java.util.ArrayList;
import java.util.List;

public class 泛型 {

    public static void main(String[] args) {

        Myclass<List<String>> a = new Myclass();
        a.print(new ArrayList<>());
    }

    public static class Myclass<T> {

        public void print(T s) {
            if (s instanceof List ) {
                System.out.println("s is List");
                System.out.println( s.getClass().getGenericSuperclass().getTypeName());

            }
            System.out.println(s.getClass().getName());
        }
    }

    public static class Banner {

    }
}
