package org.lc.model;

import java.util.ArrayList;
import java.util.List;

public class DynamicColumn {
    private List<String> dynamicHeaders = new ArrayList<>();
    private List<String> dynamicColumns = new ArrayList<>();

    public DynamicColumn addColumn(String header, String dynamicColumn){
        dynamicHeaders.add(header);
        dynamicColumns.add(dynamicColumn);
        return this;
    }

    public String getDynamicColumn(String header){
        for(int index = 0; index < dynamicHeaders.size(); index++){
            if(dynamicHeaders.get(index).equals(header)){
                return dynamicColumns.get(index);
            }
        }
        return null;
    }

    public List<String> getHeaders(){
        return dynamicHeaders;
    }

    public List<String> getHeadersByDynamicColumn(String dynamicColumn){
        List<String> headers = new ArrayList<>();
        for(int index = 0; index < dynamicColumns.size(); index++){
            if(dynamicColumns.get(index).equals(dynamicColumn)){
                headers.add(dynamicHeaders.get(index));
            }
        }
        return headers;
    }
}
