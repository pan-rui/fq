package com.hy.vo;

import com.hy.core.ParamsMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 请求VO对象
 * @Created: 潘锐 (2017-01-17 15:53)
 * $Rev: 2351 $
 * $Author: panrui $
 * $Date: 2017-05-16 18:07:26 +0800 (周二, 16 5月 2017) $
 */
public class ParamsVo implements Serializable{
    //查询参数
    private ParamsMap<String,Object> params = new ParamsMap<>();
    private List<Map<String, Object>> datas = null;
    //业务参数
    private ParamsMap<String,Object> reqData = null;
    
    private String id;

    public ParamsMap<String, Object> getParams() {
        return params;
    }

    public void setParams(ParamsMap<String, Object> params) {
        this.params = params;
    }

    public List<Map<String, Object>> getDatas() {
        return datas;
    }

    public void setDatas(List<Map<String, Object>> datas) {
        this.datas = datas;
    }

    public ParamsMap<String, Object> getReqData() {
        return reqData;
    }

    public void setReqData(ParamsMap<String, Object> reqData) {
        this.reqData = reqData;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    @Override
    public String toString() {
        return "{\"id\":\""+id+"\",\"params\":"+params.toString()+",\"datas\":"+datas.toString()+",\"reqData\":"+reqData.toString()+"}";
    }
}
