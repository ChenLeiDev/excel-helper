package org.lc.fe.util;

public class StringUtil {

    private StringUtil(){}

    public static boolean isNotBlank(final String sequence){
        if (sequence == null) {
            return false;
        }else{
            String str = sequence.replaceAll("\\s", "");
            if("".equals(str)){
                return false;
            }
        }
        return true;
    }

}
