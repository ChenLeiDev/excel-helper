package org.lc.util;

import java.util.Collection;

public class CollectionUtil {

    private CollectionUtil(){}

    public static boolean isNotEmpty(Collection collection){
        if(collection != null && !collection.isEmpty()){
            return true;
        }
        return false;
    }

}
