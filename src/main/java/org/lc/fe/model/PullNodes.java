package org.lc.fe.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 陈雷
 */
public class PullNodes {
    private Map<String, String> containerOut = new HashMap<>();
    private Map<String, String> containerIn = new HashMap<>();
    private List<String> temp = new ArrayList<>();
    public String pullsFlag = null;
    private static final String BATCH_SEPARATOR = ",";
    private static final String BUSSINESS_SEPARATOR = "@";

    public void add(Object key, Object value){
        containerOut.put(pullsFlag.concat(BUSSINESS_SEPARATOR).concat(String.valueOf(key)), String.valueOf(value));
        containerIn.put(pullsFlag.concat(BUSSINESS_SEPARATOR).concat(String.valueOf(value)), String.valueOf(key));
        temp.add(String.valueOf(value));
    }

    public String getOutValue(String pullsFlag, Object key){
        StringBuilder stringBuilder = new StringBuilder();
        String[] split = String.valueOf(key).split(BATCH_SEPARATOR);
        for (int index = 0; index < split.length; index++) {
            if(index > 0){
                stringBuilder.append(BATCH_SEPARATOR);
            }
            String pullValue = containerOut.get(pullsFlag.concat(BUSSINESS_SEPARATOR).concat(split[index]));
            if(pullValue != null){
                stringBuilder.append(pullValue);
            }else {
                stringBuilder.append(split[index]);
            }
        }
        return stringBuilder.toString();
    }

    public String getInValue(String pullsFlag, Object value){
        StringBuilder stringBuilder = new StringBuilder();
        String[] split = String.valueOf(value).split(BATCH_SEPARATOR);
        for (int index = 0; index < split.length; index++){
            if(index > 0){
                stringBuilder.append(BATCH_SEPARATOR);
            }
            String pullKey = containerIn.get(pullsFlag.concat(BUSSINESS_SEPARATOR).concat(split[index]));
            if(pullKey != null){
                stringBuilder.append(pullKey);
            }else{
                stringBuilder.append(split[index]);
            }
        }
        return stringBuilder.toString();
    }

    public String[] pulls(){
        String[] pulls = temp.toArray(new String[temp.size()]);
        temp.clear();
        return pulls;
    }
}
