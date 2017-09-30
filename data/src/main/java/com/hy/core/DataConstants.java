package com.hy.core;

/**
 * @Description: 数据基本定义
 * @Author: wady (2017-04-06 11:43)
 * @version: 1.0
 * @UpdateAuthor: wady
 * @UpdateDateTime: 2017-04-06
 */
public class DataConstants {
	
	//劳务同步标识位
	public final static String NOT_SYNCHRO="0";
	public final static String SYNCHRO_SUCCESS="1";
	public final static String SYNCHRO_FAIL="2";
	
	
	
	//工序第一级分类名  装修/土建
	public final static String PROCEDURE_TYPE_TJ = "主体结构";
	public final static String PROCEDURE_TYPE_ZX = "建筑装饰装修";
	public final static String PROCEDURE_TYPE_DJ = "地基与基础";
	
	
	//批次验收主状态
	public final static String ACCEPTANCE_BATCH_STATUS_MASTER = "1";
	public final static String ACCEPTANCE_BATCH_STATUS_MEMBER = "0";
	
	
	//户型图分区类型
	public final static  String HOUSEHOLD_CHART_AREA_TYPE_LB = "楼板";
	public final static  String HOUSEHOLD_CHART_AREA_TYPE_QT = "墙体";
	public final static  String HOUSEHOLD_CHART_AREA_TYPE_DP = "顶棚";

	// 屋面层
    public final static  String ROOF_FLOOR = "屋面层";

    // 层的内墙和外墙
    public final static  String FLOOR_INTERNAL_WALL_SUFFIX = "N";
    public final static  String FLOOR_EXTERNAL_WALL_SUFFIX = "W";
	
	//隐蔽照片类型
	public final static  String HIDDEN_PHOTO_TYPE_ZT = "0";
	public final static  String HIDDEN_PHOTO_TYPE_FB = "1";

    // 列表信息
    public final static  String LIST_KEY = "list";
    
    //权限工序名称
    public final static  String PROCEDURE_DATA_TYPE = "工序";

    //部位
    public final static  String REGION_TYPE = "部位";
    
    // 部位项目信息
    public final static  String REGION_PROJECT_TYPE = "项目";
    
    // 部位期信息
    public final static  String REGION_PERIOD_TYPE = "期";

    // 部位栋信息
    public final static  String REGION_BUILDING_TYPE = "栋";

    // 部位层信息
    public final static  String REGION_FLOOR_TYPE = "层";

    // 部位户信息
    public final static  String REGION_ROOM_TYPE = "户";
    
    //部位名称
    public final static  String REGION_FLOOR_KEY = "本层";
    public final static  String REGION_BUILDING_KEY = "本栋";
    public final static  String REGION_PERIOD_KEY = "本期";
    public final static  String REGION_PROJECT_KEY = "本项目";
    
    // 项目信息
    public final static  String REGION_PERIOD_TYPE_KEY = "period";

    // 部位栋信息
    public final static  String REGION_BUILDING_TYPE_KEY = "building";

    // 部位层信息
    public final static  String REGION_FLOOR_TYPE_KEY = "floor";

    // 部位户信息
    public final static  String REGION_ROOM_TYPE_KEY = "room";

    // 期信息
    public final static  String REGION_PERIOD_LIST_TYPE_KEY = "periods";

    // 部位栋信息
    public final static  String REGION_BUILDING_LIST_TYPE_KEY = "buildings";

    // 部位层信息
    public final static  String REGION_FLOOR_LIST_TYPE_KEY = "floors";

    // 部位户信息
    public final static  String REGION_ROOM_LIST_TYPE_KEY = "rooms";

    // 大写字母命名
    public final static String REGION_BUILDING_RULE_A_Z  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // 部位项目信息
    public final static  String REGION_PERIOD_TYPE_VAL = "1";

    // 部位栋信息
    public final static  String REGION_BUILDING_TYPE_VAL = "2";

    // 部位层信息
    public final static  String REGION_FLOOR_TYPE_VAL = "3";

    // 部位户信息
    public final static  String REGION_ROOM_TYPE_VAL = "4";
    
    //状态类型
    public final static  String PROCEDURE_STATUS = "1";
    
    //验收角色(班组1、监理验收员2、质检验收员3、甲方抽验员4、乙方抽验员4、)
    public final static  String INSPECTOR_ROLE_QT = "6";
    public final static  String INSPECTOR_ROLE_YF = "5";
    public final static  String INSPECTOR_ROLE_JF = "4";
    public final static  String INSPECTOR_ROLE_JL = "2";
    public final static  String INSPECTOR_ROLE_ZJ = "3";
    public final static  String INSPECTOR_ROLE_BZ = "1";
    
    public final static  String CHECK_SUCCESS = "1";
    public final static  String CHECK_FAIL = "0";
    
    //工序状态分类
    //验收记录note状态类型
    public final static  String NOTE_PROCEDURE_STATUS_TYPE="1";
    //质检批次检验状态类型
    public final static  String BATCH_PROCEDURE_STATUS_TYPE_ZJ="2";
    //监理批次检验状态类型
    public final static  String BATCH_PROCEDURE_STATUS_TYPE_JL="3";
    
    //工序状态id
    //施工中
    public final static  String PROCEDURE_STATUS_ID_SGZ = "1";
    //已报验
    public final static  String PROCEDURE_STATUS_ID_YBY = "2";
    //检验员已验收
    public final static  String PROCEDURE_STATUS_ID_ZJYYS = "3";
    //监理已验收
    public final static  String PROCEDURE_STATUS_ID_JLYYS = "4";
    
    //验收批状态
    //检验员未验收
    public final static  String BATCH_STATUS_ID_ZJWYS = "5";
    //检验员拟复验
    public final static  String BATCH_STATUS_ID_ZJNFY = "6";
    //检验员已验收
    public final static  String BATCH_STATUS_ID_ZJYYS = "7";
    //监理未验收
    public final static  String BATCH_STATUS_ID_JLWYS = "8";
    //监理拟复验
    public final static  String BATCH_STATUS_ID_JLNFY = "9";
    //监理已验收
    public final static  String BATCH_STATUS_ID_JLYYS = "10";
    //甲方抽验未验收
    public final static  String BATCH_STATUS_ID_JFWYS = "15";
    public final static  String BATCH_STATUS_ID_JFNFY = "16";
    public final static  String BATCH_STATUS_ID_JFYYS = "17";
    //乙方抽验未验收
    public final static  String BATCH_STATUS_ID_YFWYS = "18";
    public final static  String BATCH_STATUS_ID_YFNFY = "19";
    public final static  String BATCH_STATUS_ID_YFYYS = "20";
    
    public final static  String BATCH_STATUS_ID_BZWYS = "24";
    public final static  String BATCH_STATUS_ID_BZNFY = "25";
    public final static  String BATCH_STATUS_ID_BZYYS = "26";
    
    public final static  String BATCH_STATUS_ID_QTWYS = "21";
    public final static  String BATCH_STATUS_ID_QTNFY = "22";
    public final static  String BATCH_STATUS_ID_QTYYS = "23";
    
    //验收批流程状态
    public final static  String ACCEPTANCE_BATCH_STATUS_ID_WBY="11";
    public final static  String ACCEPTANCE_BATCH_STATUS_ID_YBY="12";
    public final static  String ACCEPTANCE_BATCH_STATUS_ID_ZJYYS="13";
    public final static  String ACCEPTANCE_BATCH_STATUS_ID_JLYYS="14";
    
    
}
