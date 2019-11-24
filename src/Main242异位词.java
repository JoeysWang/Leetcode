import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Main242异位词 {
    public static void main(String[] args) {

      boolean result=  isAnagram("caar","rac");
      System.out.println(result);
    }
    public static boolean isAnagram(String s, String t) {

        Map<Character, Integer> couts = new HashMap<>();
        Map<Character, Integer> couts2 = new HashMap<>();


        toMap(s, couts);
        toMap(t, couts2);

        boolean result = true;
        if (couts.keySet().size()!=couts2.keySet().size())
            return false;
        Set<Character> characters = couts.keySet();

        for (Character character : characters) {
            if (!couts.get(character).equals(couts2.get(character))) {
                return false;
            }
        }

        return result;
    }

    private static void toMap(String s, Map<Character, Integer> couts) {
        for (char c : s.toCharArray()) {
            if (couts.containsKey(c)) {
                couts.put(c, couts.get(c) + 1);
            } else
                couts.put(c, 1);

        }
    }


}
