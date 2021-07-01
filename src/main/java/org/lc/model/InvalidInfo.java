package org.lc.model;


import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 陈雷
 */
public class InvalidInfo {
    private LinkedHashMap<String, String> info = new LinkedHashMap<>();
    public boolean valid = true;

    public void addInvalid(String key, String value){
        valid = false;
        info.put(key, value);
    }

    public String getInvalid(String key){
        return info.get(key);
    }

    public Set<String> keys(){
        return info.keySet();
    }

    public Collection<String> values(){
        return info.values();
    }

    public Set<Map.Entry<String, String>> entrySet(){
        return info.entrySet();
    }

    public Map<String, String> getInfo() {
        return info;
    }
}
