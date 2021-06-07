package org.lc.fe.util;

import java.util.Collection;

public class CollectionUtil {

    public static boolean isNotEmpty(Collection collection){
        if(collection != null && collection.size() > 0){
            return true;
        }
        return false;
    }

}
