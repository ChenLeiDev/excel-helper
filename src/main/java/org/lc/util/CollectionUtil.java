package org.lc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtil {

    private CollectionUtil(){}

    public static boolean isNotEmpty(Collection collection){
        if(collection != null && !collection.isEmpty()){
            return true;
        }
        return false;
    }

    public static <T> HashSet<T> newHashSet(T... value) {
        HashSet<T> hashSet = new HashSet<>();
        if(ArrayUtil.isNotEmpty(value)){
            for (int i = 0; i < value.length; i++){
                if(value[i] != null){
                    hashSet.add(value[i]);
                }
            }
        }
        return hashSet;
    }

    public static <T> ArrayList<T> newArrayList(T[] value) {
        ArrayList<T> arrayList = new ArrayList<>();
        if(ArrayUtil.isNotEmpty(value)){
            for (int i = 0; i < value.length; i++){
                if(value[i] != null){
                    arrayList.add(value[i]);
                }
            }
        }
        return arrayList;
    }
}
