package com.hy.service;

import com.hy.core.Page;
import com.hy.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BaseService {
	@Autowired
	private BaseDao baseDao;

	/**
	 * 添加对象
	 * 
	 * @param params
	 * @param tableName
	 */
	public String add(Map<String, Object> params, String tableName) {
		Assert.notNull(params);
		String id = (String) params.get("ID");
		if(!params.containsKey("ID")){
			id=UUID.randomUUID().toString().replace("-", "");
			params.put("ID", id);
		}
		baseDao.insertByProsInTab(tableName, params);
		return id;
	}
	
	public void addList(List<Map<String, Object>> list, String tableName) {
		if(list!=null&&list.size()>0){
			baseDao.insertBatchByProsInTab(tableName, list);
		}
	}
	
	public void addOrUpdateList(List<Map<String, Object>> list, String tableName) {
		if(list!=null&&list.size()>0){
			baseDao.insertUpdateBatchByProsInTab(tableName, list);
		}
	}
	

	/**
	 * 根据ID获取对象数据
	 * 
	 * @param tableName
	 *            表名
	 * @param id
	 *            主键
	 * @return 对象数据
	 */
	public Map<String, Object> getByID(String tableName, String id) {
		Assert.notNull(tableName);
		Assert.notNull(id);

		return baseDao.queryByIdInTab(tableName, id);
	}

	/**
	 * 删除对象
	 * 
	 * @param params
	 * @param tableName
	 * @return
	 */
	public int delete(Map<String, Object> params, String tableName) {
		Assert.notNull(params);
		LinkedHashMap<String, Object> map = new LinkedHashMap<>(params);
		if (map.containsKey("ID")) {
			map.remove("ID");
			map.put("ID", params.get("ID"));
		}
		return baseDao.deleteByProsInTab(tableName, map);
	}

	/**
	 * 更新对象
	 * 
	 * @param
	 * @return
	 */
	public int update(Map<String, Object> params, String tableName) {
		Assert.notNull(params);
		LinkedHashMap<String, Object> map = new LinkedHashMap<>(params);
		if (map.containsKey("ID")) {
			map.remove("ID");
			map.put("ID", params.get("ID"));
		}
		return baseDao.updateByProsInTab(tableName, map);
	}

	/**
	 * 查询集合
	 * 
	 * @param params
	 * @param orderMap
	 * @param tableName
	 * @return
	 */
	public List<Map<String, Object>> queryList(Map<String, Object> params, Map<String, Object> orderMap,
											   String tableName) {
		Assert.notNull(params);
		Map<String, Object> map = new LinkedHashMap<>(params);
		if (map.containsKey("ID")) {
			map.remove("ID");
			map.put("ID", params.get("ID"));
		}
		return baseDao.queryListInTab(tableName, map, orderMap);
	}

	/**
	 * 分页查询集合
	 * 
	 * @param page
	 * @param tableName
	 * @return
	 */
	public Page queryPage(Page page, String tableName) {
		Assert.notNull(page.getParams());
		Map<String, Object> map = new LinkedHashMap<>(page.getParams());
		if (map.containsKey("ID") && map.get("ID") != null) {
			map.remove("ID");
			map.put("ID", page.getParams().get("ID"));
			page.setParams(map);
		}
		baseDao.queryPageInTab(tableName, page);
		return page;
	}

}
