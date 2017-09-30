package com.hy.core;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: ${Description}
 * Author: 潘锐 (2017-04-01 18:58)
 * version: \$Rev: 1158 $
 * UpdateAuthor: \$Author: panrui $
 * UpdateDateTime: \$Date: 2017-04-18 15:53:47 +0800 (周二, 18 4月 2017) $
 */
public class MapResultHandler implements ResultHandler {

    private List<Map<String, Object>> mappedResults;

    public MapResultHandler() {
        mappedResults = new ArrayList<Map<String,Object>>();
    }

    @Override
    public void handleResult(ResultContext resultContext) {
        Map<String,Object> resultMap = (Map<String, Object>) resultContext.getResultObject();
        Map<String, Object> map = new LinkedHashMap();
       resultMap.forEach((k,v)->{
           map.put(ColumnProcess.encryptVal(k), v);
       });
       mappedResults.add(map);
    }

    public List<Map<String, Object>> getMappedResults() {
        return mappedResults;
    }
}
