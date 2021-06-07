package org.lc.fe.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtil {

    private CollectionUtil(){}

    public static boolean isNotEmpty(Collection collection){
        if(collection != null && !collection.isEmpty()){
            return true;
        }
        return false;
    }

}
