package com.hy.util;

import com.hy.core.Table;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TreeUtil {

	public static final String ID = "id";
	public static final String PARENT_ID = "parentId";
	public static final String TREE_CODE = "treeCode";

		public static List<Map<String, Object>> getTree(List<Map<String, Object>> treeList, String parentIdKey,
			String entityIdKey, String idKey, Map<String, Object> entityMap, String entityKey, String listKey) {
		List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
		Map<String, Object> obj = new LinkedHashMap<String, Object>();
		Map<String, Object> parentMap = new LinkedHashMap<String, Object>();
		Map<String, Object> treeMap = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> children = null;
		Map<String, Object> corporate = null;

		for (Map<String, Object> org : treeList) {
			// obj.put(infoKey, org);
			Object parentId = org.get(parentIdKey);
			Object id = org.get(idKey);
			// 为实体时加入时

			if (parentId != null) {
				if (treeMap.containsKey(parentId)) {
					parentMap = (Map<String, Object>) treeMap.get(String.valueOf(parentId));

					if (parentMap.containsKey(listKey)) {
						children = (List<Map<String, Object>>) parentMap.get(listKey);
					}

					if (children == null) {
						children = new LinkedList<Map<String, Object>>();

						parentMap.put(listKey, children);
					}

					if (org.get(entityIdKey) != null) {
						corporate = (Map<String, Object>) entityMap.get(org.get(entityIdKey));
						org.put(entityKey, corporate);
					}

					children.add(org);
				} else {
					parentMap = new LinkedHashMap<String, Object>();

					children = new LinkedList<Map<String, Object>>();

					parentMap.put(listKey, children);

					treeMap.put(String.valueOf(parentId), parentMap);
				}
			} else {
				if (treeMap.containsKey(String.valueOf(id))) {
					parentMap = (Map<String, Object>) treeMap.get(String.valueOf(id));

					if (parentMap == null) {
						parentMap = new HashMap<String, Object>();
					}
				} else {
					parentMap = new HashMap<String, Object>();
				}

				if (org.get(entityIdKey) != null) {
					corporate = (Map<String, Object>) entityMap.get(org.get(entityIdKey));
					org.put(entityKey, corporate);
				}

				if (parentMap.containsKey(String.valueOf(id))) {
					children = (List<Map<String, Object>>) parentMap.get(listKey);
					if (children != null) {
						org.put(listKey, children);
					}
				}

				treeMap.put(String.valueOf(id), org);

				resultList.add(org);
			}
		}
		return resultList;
	}

	public static List<Map<String, Object>> getTree(List<Map<String, Object>> treeList, String parentIdKey,
			String entityIdKey, String idKey, Map<String, Object> entityMap, String infoKey, String entityKey,
			String listKey) {
		List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
		Map<String, Object> obj = new LinkedHashMap<String, Object>();
		Map<String, Object> parentMap = new LinkedHashMap<String, Object>();
		Map<String, Object> treeMap = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> children = null;
		Map<String, Object> child = null;
		Map<String, Object> corporate = null;

		for (Map<String, Object> org : treeList) {
			// obj.put(infoKey, org);
			Object parentId = org.get(parentIdKey);
			Object id = org.get(idKey);
			// 为实体时加入时

			if (parentId != null) {
				if (treeMap.containsKey(parentId)) {
					parentMap = (Map<String, Object>) treeMap.get(String.valueOf(parentId));

					if (parentMap.containsKey(listKey)) {
						children = (List<Map<String, Object>>) parentMap.get(listKey);
					}

					if (children == null) {
						children = new LinkedList<Map<String, Object>>();

						parentMap.put(listKey, children);
					}

					child = new LinkedHashMap<String, Object>();

					if (org.get(entityIdKey) != null) {
						corporate = (Map<String, Object>) entityMap.get(org.get(entityIdKey));
						child.put(entityKey, corporate);
					}
					child.put(infoKey, org);

					children.add(child);
				} else {
					parentMap = new LinkedHashMap<String, Object>();

					children = new LinkedList<Map<String, Object>>();

					parentMap.put(listKey, children);

					treeMap.put(String.valueOf(parentId), parentMap);
				}
			} else {
				if (treeMap.containsKey(String.valueOf(id))) {
					parentMap = (Map<String, Object>) treeMap.get(String.valueOf(id));

					if (parentMap == null) {
						parentMap = new HashMap<String, Object>();
					}
				} else {
					parentMap = new HashMap<String, Object>();
				}

				if (org.get(entityIdKey) != null) {
					corporate = (Map<String, Object>) entityMap.get(org.get(entityIdKey));
					parentMap.put(entityKey, corporate);
				}
				parentMap.put(infoKey, org);

				treeMap.put(String.valueOf(id), parentMap);

				resultList.add(parentMap);
			}
		}
		return resultList;
	}

	public static Map<String, Object> getParentTree(List<Map<String, Object>> list, String parentId) {
		Map<String, Object> parentMap = null;

		if (parentId != null) {
			for (Map<String, Object> map : list) {
				if (parentId.equals(map.get(ID))) {
					parentMap = map;
					break;
				}
			}
		}

		return parentMap;
	}

	public static String getTreeCode(List<Map<String, Object>> list, String parentCode, String parentId) {
		int i = 1;
		String format = "%02d";
		String code = null;

		for (Map<String, Object> map : list) {
			if (parentCode != null && parentCode.length() > 0) {
				if (map.get(PARENT_ID) != null && parentId.compareTo((String) map.get(PARENT_ID)) == 0) {
					i++;
				}
			} else {
				if (parentId == null && map.get(PARENT_ID) == null) {
					i++;
				}
			}
		}

		if (parentCode != null && parentCode.split("\\" + Table.SEPARATE).length >= 2) {
			format = "%03d";
		}

		if (parentCode == null) {
			code = String.format(format, i);
		} else {
			code = parentCode + Table.SEPARATE + String.format(format, i);
		}

		return code;
	}



}
