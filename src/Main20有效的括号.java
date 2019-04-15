import java.util.Stack;

public class Main20有效的括号 {

    public boolean isValid(String s) {

        if (s == null || s.length() < 1) return true;

        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {


            if (c == '(') stack.push(')');
            else if (c == '[') stack.push(']');
            else if (c == '{') stack.push('}');

            else {
                if (stack.isEmpty()|| stack.pop()!=c)
                    return false;

            }
        }


        return stack.isEmpty();
    }
}
