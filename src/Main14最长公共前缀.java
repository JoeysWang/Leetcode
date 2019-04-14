public class Main14最长公共前缀 {
    //    输入: ["flower","flow","flight"]
//    输出: "fl"
    public String longestCommonPrefix(String[] strs) {

        if (strs == null || strs.length < 1)
            return "";


        String res = strs[0];


        for (int i =1;i<strs.length;i++){

            while (strs[i].indexOf(res)!=0){
                res=res.substring(0,res.length()-1);
            }

        }


        return res;
    }

}
