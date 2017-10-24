package com.hy.core;
public class Table {
	public static final String SEPARATE = ".";
	public static final String SEPARATE_SPLIT = ",";
	public static final String SEPARATE_CACHE = "-";
	public static final String FIELD_INTERVAL = "_";
	public static final String SPACE = " ";
	public static final String FQ = "fq.";
	public static final String ID = "ID";
	public static final String IS_ENABLE = "IS_ENABLE";
	public static final String USER_ID = "USER_ID";
	public static final String DESC = "DESC";
	public static final String ASC = "ASC";
	//
	public static final String ACCOUNT="ACCOUNT";
	//
	public static final String ADVERTIS="ADVERTIS";
	//
	public static final String APP_VESION="APP_VESION";
	//
	public static final String AUDIT_RECORD="AUDIT_RECORD";
	//
	public static final String BANKS="BANKS";
	//
	public static final String BRAND="BRAND";
	//
	public static final String CLASSIFY="CLASSIFY";
	//
	public static final String COMPANY="COMPANY";
	//
	public static final String COUPON="COUPON";
	//
	public static final String COUPON_DICT="COUPON_DICT";
	//
	public static final String C_POSITION_SERIES="C_POSITION_SERIES";
	//
	public static final String C_PRODUCT_ATTRIBUTE="C_PRODUCT_ATTRIBUTE";
	//
	public static final String C_STORE_PRODUCT="C_STORE_PRODUCT";
	//
	public static final String DELIVERY="DELIVERY";
	//
	public static final String EMPLOYEE="EMPLOYEE";
	//
	public static final String MERCHANT="MERCHANT";
	//
	public static final String ORDER="ORDER";
	//
	public static final String PLAN_REPAYMENT="PLAN_REPAYMENT";
	//
	public static final String PRODUCT="PRODUCT";
	//
	public static final String PRODUCT_ATTRIBUTE="PRODUCT_ATTRIBUTE";
	//
	public static final String PRODUCT_DISCUSS="PRODUCT_DISCUSS";
	//
	public static final String PRODUCT_STORE="PRODUCT_STORE";
	//
	public static final String PRODUCT_TAGS="PRODUCT_TAGS";
	//
	public static final String SCORE_GAIN="SCORE_GAIN";
	//
	public static final String SCORE_RECORD="SCORE_RECORD";
	//
	public static final String SMS_TEMPLATE="SMS_TEMPLATE";
	//
	public static final String STORE="STORE";
	//
	public static final String SYSTEM_DICT="SYSTEM_DICT";
	//
	public static final String TRADE_RECORD="TRADE_RECORD";
	//
	public static final String USER="USER";
	//
	public static final String USER_ATTACH="USER_ATTACH";
	//
	public static final String USER_BANK="USER_BANK";
	//
	public static final String USER_CALENDAR="USER_CALENDAR";
	//
	public static final String USER_COLLECT="USER_COLLECT";
	//
	public static final String USER_NOTE="USER_NOTE";

	public enum Account{
		ID,	//ID
		USER_ID,	//用户ID
		USER_NAME,	//用户名称
		USER_TYPE,	//客户类型
		BUSINESS_TYPE,	//业务类型（先空预留）
		ACCT_BALANCE,	//账户余额
		SCORE_BALANCE,	//积分余额
		ACCT_BALANCE_UTIME,	//账户余额更新时间
		SCORE_BALANCE_UTIME,	//积分余额更新时间
		CTIME,	//登记时间
		UP_ID,	//更新人
		UTIME,	//更新时间
		STATUS,	//状态:1:启用，0禁用，2冻结
	}

	public enum Advertis{
		ID,	//广告ID
		COMPANY_ID,	//企业ID或店铺ID
		POSITION,	//位置
		SEQ,	//顺序
		TITLE,	//标题
		LINK_URL,	//链接地址
		DESCRIPTION,	//描述
		KEYWORD,	//关键字
		TYPE,	//0图片，2,flash 3-商品
		CONTENT,	//根据类型不同而不同：类型为商品则为店铺商品ID,如果是图片或flash为图片或文件路径
		CHECK_STATUS,	//审核状态,0待提交,1待审核,2审核通过,3审核不通过  （前期可以考虑直接审核通过）
		CHECK_TIME,	//审核时间
		CHECK_OPERATER,	//审核操作员
		CHECK_MEMO,	//审核意见
		ON_LINE,	//上下线状态 0上线1下线
		CTIME,	//
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum AppVesion{
		ID,	//
		APP_TYPE,	//ios,android
		UPDATE_TYPE,	//强制，非强制
		VERSION,	//
		UPDATE_COUNT,	//版本编号
		DOWNLOAD_COUNT,	//
		DOWNLOAD_URL,	//
		FILE_SIZE,	//
		UPDATE_CONTENT,	//版本说明
		PUBLISH_PERSON,	//发布人
		PUBLISH_TIME,	//发布时间
		IS_ENABLE,	//1：正常；0：删除
	}

	public enum AuditRecord{
		ID,	//
		USER_ID,	//受审用户ID
		BIZ_TYPE,	//业务类型
		AUDIT_TYPE,	//审核类型
		AUDIT_STATUS,	//审核状态
		CTIME,	//创建时间
		AUDIT_ID,	//审核人ID
		AUDIT_NAME,	//审核人姓名
		AUDITEDON,	//审核时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum Banks{
		ID,	//唯一标识
		SWIFT,	//银行代码
		NAME,	//银行名称
		IS_ENABLE,	//是否启用:1:启用,0:禁用
		REMARK,	//备注
	}

	public enum Brand{
		ID,	//品牌编号:主键
		NAME,	//品牌名称:
		PIC,	//品牌Logo保存Logo图片的名称
		LIST_PIC,	//品牌图片:用于列表显示；保存图片的名称
		CONTENT,	//品牌介绍:图文结合方式，编辑器编辑
		CTIME,	//增加时间
		IS_ENABLE,	//1正常 0停用
		SEQ,	//排序
		PINYIN,	//多个拼音用json格式 [拼音1,拼音2]
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum Classify{
		ID,	//分类ID
		NAME,	//分类名称
		PARENT_ID,	//上级分类ID
		LEVEL,	//分类等级
		SEQ,	//排序
		IS_ENABLE,	//状态,1:有效,0无效
		PIC,	//分类图标
	}

	public enum Company{
		ID,	//企业ID
		COMPANY_NAME,	//企业名称
		COMPANY_CODE,	//企业代码
		COMPANY_TYPE,	//企业类型
		COMPANY_COMMENT,	//企业简介
		COMPANY_PERSON,	//企业法人
		COMPANY_PHONE,	//法人联系电话
		BIZ_PHONE,	//业务联系电话
		COMPANY_ADDR,	//企业地址
		REMARK,	//备注
		UTIME,	//修改时间
		UP_ID,	//修改人ID
		IS_ENABLE,	//1：正常；0：删除
	}

	public enum Coupon{
		ID,	//ID
		USER_ID,	//用户id
		COUPON_ID,	//礼券id
		CTIME,	//创建时间
		REMARK,	//备注
		STATUS,	//状态{"1":"未领取","2","未使用","3":"已使用","4":"未领取过期","5":"未使用过期","6":"处理中"}
	}

	public enum CouponDict{
		ID,	//ID
		COUPON_NAME,	//礼券名称
		COUPON_AMOUNT,	//礼券金额
		CTIME,	//创建时间
		REMARK,	//备注(code)
		IS_ENABLE,	//状态{"1":"启用","0":"禁用"}
		IS_WITHDRAW,	//允许提现{"1":"是","0":"否"}
		COUPON_TYPE,	//类型{"0":"红包","1":"推荐奖励","2":"其它"}
		EXPIRE_DATE,	//过期时间
	}

	public enum CPositionSeries{
		ID,	//主键
		PRODUCT_ID,	//产品ID
		STORE_ID,	//门店id
		POSITION_ID,	//职位ID
		PRODUCT_SERIES_NAME,	//产品名称
		PRODUCT_TYPE,	//产品类型
		PRODUCT_START_TIME,	//产品开始时间
		PRODUCT_END_TIME,	//产品结束时间
		DISCOUNT_START_TIME,	//打折开始时间
		DISCOUNT_END_TIME,	//打折结束时间
		DISCOUNT_ENABLE_STATUS,	//折扣启用状态
		COMMISSION_RATE,	//佣金百分比
		CTIME,	//创建时间
		UP_ID,	//修改人
		UTIME,	//修改时间
		IS_ENABLE,	//1启用 0禁用
	}

	public enum CProductAttribute{
		ID,	//ID
		ATT_ID,	//属性ID
		PRODUCT_ID,	//商品ID
		ATT_NAME,	//属性名称
		ATT_VALS,	//属性可选值[值1，值2...]
		SEQ,	//排序
		IS_ENABLE,	//状态1启用,0禁用
		CTIME,	//增加时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum CStoreProduct{
		ID,	//ID
		STORE_ID,	//店铺ID
		PRODUCT_ID,	//商品ID
		CLASSIFY_ID,	//商品分类ID
		SHELVE_STATUS,	//上下架状态（1:下架、0:上架）
		SHELVE_TIME,	//上下架时间
		SALE_TYPE,	//销售类型,1自销,2.代销
		PAY_NUM,	//销量（实际支付数量）
		STOCK_NUM,	//库存量
		PAGE_VIEW,	//浏览次数,人气
		COLLECT_NUM,	//收藏次数,关注
		CUSTOM_PRICE,	//店铺自定义销售价
		ATT_JSON,	//[{属性组:{属性1:值1，属性2:值2...},库存:xx,累积售出:xx,进货价:XX,售价:xx,优惠价:xxx}...]
		PRO_TAGS,	//商品标签[标签id1,标签id2]
		CTIME,	//增加时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum Delivery{
		ID,	//ID
		USER_ID,	//用户ID
		NAME,	//姓名
		PHONE,	//电话
		PROVINCE,	//省
		CITY,	//市
		AREA,	//区
		EXACT,	//详细地址
		ZIP,	//邮编
		MARK,	//标签
		IS_DEFAULT,	//默认地址:{"1":"是","0":"否"}
		IS_ENABLE,	//是否启用：1启用，0禁用
		CTIME,	//创建时间
	}

	public enum Employee{
		ID,	//主键
		NAME,	//姓名
		PHONE,	//手机
		WORK_ID,	//工号
		POST,	//岗位
		LEVEL,	//级别
		CARD_NO,	//身份证号
		CLIENT_SN,	//客户端序列号
		PWD,	//密码
		REG_ADDR,	//户籍地址
		DIPLOMA,	//学历
		ADDR,	//现住址
		OSS_ID,	//
		DEPT,	//部门
		UP_NAME,	//上级姓名
		UP_PHONE,	//上级电话
		CTIME,	//创建时间
		UP_ID,	//修改人ID
		UTIME,	//修改时间
		STATE,	//0:停用,1:正常,2：离职
	}

	public enum Merchant{
		ID,	//主键
		MERCHANT_NAME,	//商户名称
		BUSINESS_LICENSE_NUM,	//营业执照号
		ADDRESS,	//商户地址
		ZIPCODE,	//邮编
		CARD_NUMBER,	//身份证号
		NAME,	//姓名
		PHONE,	//手机号
		MAIL,	//邮箱
		ACCOUNT_NAME,	//户名
		BANK_CODE,	//开户行
		ACCOUNT_SUB_BANK,	//开户支行
		BANK_ACCOUNT,	//账号
		CTIME,	//创建时间
		UP_ID,	//修改人
		UTIME,	//修改时间
		MERCHANT_STATUS,	//商户状态：0:启用，1:冻结，2:停用
		MERCHANT_TYPE_CODE,	//商家类型
		CROSS_REGION_CODE,	//是否跨区域
	}

	public enum Order{
		ID,	//订单ID
		USER_ID,	//用户ID
		STORE_ID,	//店铺ID
		PRODUCT_ID,	//商品ID
		PRODUCT_NAME,	//商品名称
		ORDER_NO,	//流水号
		PAY_TYPE,	//付款方式{"0":"支付宝","1":"微信"}
		CONTACT,	//收货地址ID
		DELIVERY,	//配送方式
		ATTR,	//商品规格属性
		MONEY,	//消费金额
		PERIOD,	//分期详情
		DISCOUNT,	//会员折扣
		PREFERENTIAL,	//优惠金额
		FREIGHT,	//运费
		BILL,	//发票号
		SCORE_MONEY,	//积分抵扣金额
		PAY_MONEY,	//实付金额
		ORDER_MONEY,	//订单金额
		MONTHLY,	//月供
		PAY_NO,	//支付流水号
		STATE,	//订单状态{"0":"下单待付款","1":"待发货","2":"待收货","3":"申请退款中","4":"退款审核失败","5":"收货确认中","6":"待放款","7":"还款中","8":"已取消","9":"已退货","10":"已还款"}
		DESCRIPTION,	//描述
		DELIVERY_DATE,	//配送时间:{"1234567":"周一至周日","12345":"(工作日)周一至周五","67":"节假日(周六周日)"}
		PAY_INFO,	//支付详情
		PAY_TIME,	//付款时间
		SHIPMENTS_TIME,	//发货时间
		END_DELIVERY_TIME,	//收货时间
		LOGISTICS,	//物流公司
		LOGISTICS_CODE,	//物流编号
		REMARK,	//备注
		LOGISTICS_STATE,	//物流状态
		REQ_IP,	//请求IP
		SHIPMENTS_INFO,	//发货详情
		CTIME,	//创建时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum PlanRepayment{
		ID,	//ID
		USER_ID,	//用户ID
		ORDER_ID,	//订单ID
		REPAY_NUM,	//还款期数
		REPAY_TYPE,	//还款类型
		PLANREPAY_DATE,	//计划还款日
		PLANREPAY_MONEY,	//计划还款金额
		OVERDUE,	//逾期利息
		PAY_DATE,	//实际还款日
		USE_SCORE,	//积分抵扣金额
		REAL_REPAY_MONEY,	//实际还款金额
		TRADE_NO,	//流水号
		STATUS,	//结清状态：1:已还,0:未还,2:提前还,3:逾期
		CTIME,	//创建时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum Product{
		ID,	//产品ID
		PRO_TYPE,	//商品类型，11.总直销推送商品，12.总代理推送商品，21.运营商店铺商品，31.社区店铺商品
		PRO_CODE,	//供应商商品编号
		NAME,	//商品名称
		NUM,	//商品数量
		BUY_LIMIT,	//限购数量,0不限购
		PRO_AREA,	//产地（描述）
		CLASSIFY_ID,	//商品分类ID
		CLASSIFY_LIST_ID,	//多级商品分类 json格式[,分类id,分类id],查询该分类的商品时 不用递归用"like ,分类id,"
		BRAND_ID,	//品牌ID(固有属性)
		TAGS,	//标签id 使用,分隔
		ATT_JSON,	//[{属性组:{属性1:值1，属性2:值2...},库存:xx,累积售出:xx,进货价:XX,售价:xx,优惠价:xxx}...]
		PERIODS,	//[{期数:xx,首付比:xx,分期利率:xx,手续费:xxx,产品险:xx}...]
		PERIOD_EXT,	//{首付比:[……],产品险:[…….]}
		INTRO,	//商品简介
		DETAILS,	//商品详情
		FAQ,	//常见问题
		PIC,	//默认图片
		PICS,	//商品浏览图片集
		CTIME,	//增加时间
		SUP_PRICE,	//供货价（根据商品类型不同为供应商供货价或店铺代销时的供货价）  其他的总公司供货价，市供货价在推送表中在推送时设置供货价。
		YH_PRICE,	//优惠销售价
		PRICE,	//默认销售价
		MARKET_PRICE,	//市场参考价
		RECHECK_ID,	//审核人ID
		RECHECK_STATUS,	//审状态,0待提交,1待审核,2审核通过,3审核不通过
		RECHECK_TIME,	//审时间
		RECHECK_MEMO,	//审意见
		IS_ENABLE,	//商品状态,1有效,0无效
		STORE_ID,	//所属机构（店铺）ID
		LOGISTICS_NO,	//物流编号
		REMARK,	//产品备注
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum ProductAttribute{
		ID,	//属性ID
		CLASSIFY_ID,	//分类ID
		ATT_NAME,	//属性名称
		PIC,	//属性图片,LOGO等
		SEQ,	//排序
		IS_ENABLE,	//状态1启用,0禁用
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum ProductDiscuss{
		ID,	//主键
		PRODUCT_ID,	//商品ID
		USER_ID,	//用户ID
		FLOORS,	//楼层
		PARENT_ID,	//上级ID
		CTIME,	//增加时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum ProductStore{
		ID,	//主键
		PRODUCT_ID,	//商品ID
		CLASSIFY_ID,	//商品分类ID
		ATTR,	//商品属性{属性ID:属性值,...}
		UNIT,	//商品单位  1：个 2：斤
		NUM,	//库存数量
		INST_ID,	//供应商ID
		CTIME,	//增加时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum ProductTags{
		ID,	//主键
		TAG_NAME,	//标签名称
		FROM,	//来源，如果为空表示公司添加的，各站点或店铺可以使用但不允许修改
		ACT_TYPE,	//活动类型
		ACT_RANGE,	//优惠幅度
	}

	public enum ScoreGain{
		ID,	//ID
		ADVERTIS_ID,	//广告ID
		USER_ID,	//用户ID
		PRODUCT_ID,	//商品ID
		GET_SCORE,	//获得积分
		CTIME,	//获得时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum ScoreRecord{
		ID,	//ID
		ORDER_ID,	//订单ID或还款ID
		USER_ID,	//用户ID
		PRODUCT_ID,	//商品ID
		USE_SCORE,	//使用积分
		CTIME,	//增加时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum SmsTemplate{
		ID,	//ID
		TYPE,	//模版分类
		CODE,	//编号
		NAME,	//模板名称
		VARIABLES,	//变量[变量名:变量值...]
		CONTEXT,	//模板内容
		REMARK,	//备注
		IS_ENABLE,	//是否启用。1：表示启用，0：表示未启用
		CTIME,	//创建时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum Store{
		ID,	//主键
		STORE_CODE,	//门店代码
		STORE_NAME,	//门店名称
		MERCHANT_ID,	//商户id，--外键(门店属于哪个商户)
		BRANCH_NAME,	//分部名称
		ORGCODE,	//分部归属
		STORE_ADDR,	//门店地址
		ZIPCODE,	//邮编
		CONTACT_NAME,	//主要联系人姓名
		CONTACT_PHONE,	//主要联系人电话
		CONTACT_MAIL,	//主要联系人邮箱
		START_TIME,	//营业开始时间
		END_TIME,	//营业结束时间
		BRAND_ID,	//主推品牌
		CTIME,	//创建时间
		UP_ID,	//修改人
		UTIME,	//修改时间
		STORE_STATUS,	//门店状态 0:启用、1:关闭、2:冻结
		OPER_MODE_CODE,	//运作模式
		MERCHANT_TYPE_CODE,	//商家类型
		ACCOUNT_NAME,	//开户名
		BANK_NAME,	//开户行
		ACCOUNT_SUB_BANK,	//开户支行
		BANK_ACCOUNT,	//银行账号
		FU_MOBILE,	//电子签章账号
		FU_PASSWORD,	//电子签章密码
		FEE_PROPORTION,	//服务费比例
		CALCULATE_METHOD,	//服务费计算方式
		CONTACT_QQ,	//联系人QQ号
		CONTACT_WEIXIN,	//联系人微信号
		STORE_REGISTER_NO,	//门店注册号
		STORE_DESC,	//备注
		STORE_CHARACTER,	//门店性质：1传统、2大商户、3咕咚、4壁虎、5运营商
	}

	public enum SystemDict{
		ID,	//ID
		NAME,	//名称
		CODE,	//key
		VALUE,	//value
		DESCRIPTION,	//描述
		IS_ENABLE,	//状态{"1":"启用","0":"禁用"}
		CTIME,	//创建时间
		SEQ,	//显示顺序
	}

	public enum TradeRecord{
		ID,	//ID
		TRAND_TYPE,	//交易类型
		ACCT_TIME,	//交易时间
		USER_ID,	//用户ID
		PRODUCT_ID,	//商品ID
		SOURCE,	//用户卡号
		TRADE_AMOUNT,	//交易金额
		TRADE_TYPE,	//交易方式 0银联,1支付宝,2微信
		TRADE_NO,	//交易流水号
		PAY_INFO,	//交易详情
		CTIME,	//增加时间
		UP_ID,	//修改人
		UTIME,	//修改时间
	}

	public enum User{
		ID,	//用户ID
		NAME,	//客户名称
		USER_NAME,	//用户名
		NICKNAME,	//昵称
		SEX,	//客户性别:1男,0女
		TYPE,	//客户类型
		CARD_NO,	//身份证号码
		BIZER_ID,	//业务员ID
		CTIME,	//创建时间
		UP_ID,	//更新人
		UTIME,	//更新时间
		SOURCE,	//来源渠道
		CLIENT_SN,	//客户端序列号
		IS_ENABLE,	//状态 1:启用,0禁用
		CERT_STATUS,	//实名认证状态:0待认证,1已认证,2认证失败,3待审核，4已审核5审核失败
		SOCIAL_NUMBER,	//社保号码
		DEGREE,	//教育程度
		MARRY,	//婚姻状况
		EMAIL,	//电子邮件
		FIRST_WORKTIME,	//首次参加工作时间
		CHILDREN_NUMBER,	//子女数目
		ADDRE_REGIST,	//户籍地址
		PHONE,	//手机
		PWD,	//登录密码
		ADDRESS,	//居住地址
		MONTHLY_INCOME,	//月收入
		OTHER_INCOME,	//其它收入
		FAMILY_INCOME,	//家庭月收入
		MONTHLY_SPENDING,	//月支出
		UNITNAME,	//单位名称
		INDUSTRY,	//行业类别
		UNITTYPE,	//单位性质
		DEPARTMENT,	//任职部门
		DUTY,	//职位级别
		ENTRY_DATE,	//入职日期
		UNIT_ADDRESS,	//单位所在地址
		CREATEDBY,	//创建人
		CREATEDON,	//创建时间
		OSS_ID,	//照片地址
		IDENTITY_CARD_VALIDITY_PERIOD,	//身份证有效期
		ISSUING_ORGAN,	//发证机关
		QQ,	//QQ
		WEIXIN,	//微信
		MATE_ADDRESS,	//配偶联系地址
		MATE_ID_CODE,	//配偶身份证号
		HOUSEHOLD_POSTCODE,	//户籍邮编
		LIVE_POSTCODE,	//居住邮编
		UNIT_PHONE,	//单位电话
		UNIT_EXT_PHONE,	//单位电话分机
		TPOS_INFO,	//来源信息:{tposName:wx,tposId:openId,tAccount:234353,tNickName:haha,......}
		CAREER,	//职业
		PARTER_NAME,	//配偶姓名
		UNIT_WORKTIME,	//现单位工作时长/距毕业时长（月份）
		POST_CODE,	//邮寄邮编
		UNIT_POSTCODE,	//单位邮编
		MATE_MOBEL,	//配偶电话
		MATE_UNIT,	//配偶单位
		UNITN_WEIXIN,	//单位微信公众号
		PARTE_WEIXIN,	//配偶微信号
		PARTE_QQ,	//配偶QQ号
		COMPANY_ID,	//所属集团ID
	}

	public enum UserAttach{
		ID,	//主键
		USER_ID,	//用户ID
		ATTACH_TYPE,	//附件类型:0身份证正面，1身份证反，2,银行卡 9照片
		URL,	//附件URL
		ATTACH_LEN,	//附件大小
		CTIME,	//创建时间
		UP_ID,	//修改人
		UTIME,	//修改时间
		IS_ENABLE,	//1:正常；0：删除
	}

	public enum UserBank{
		ID,	//主键
		BANK_CARD_NO,	//卡号
		USER_ID,	//用户ID
		USER_NAME,	//客户名称
		BANK_USER_NAME,	//户名
		BANK_NAME,	//开户行
		BANK_SUB_BRANCH,	//开户支行
		BANK_CITY,	//开户城市
		BANK_MOBILE,	//银行预留手机号
		CTIME,	//创建时间
		UP_ID,	//更新人
		UTIME,	//更新时间
		STATUS,	//状态：0禁用,1启用，2默认卡,3冻结
	}

	public enum UserCalendar{
		ID,	//ID
		USER_ID,	//用户ID
		EVENT_NAME,	//事件名称
		EVENT_TIME,	//事件时间
		IS_REMIND,	//是否提醒：0是，1否
		EVENT_ADDR,	//事件地址
		BERORE_REMIND,	//提前提醒时间(毫秒)
		REMARK,	//备注
		CTIME,	//创建时间
		UP_ID,	//修改人
		UTIME,	//修改时间
		IS_ENABLE,	//删除：1有效，0无效
	}

	public enum UserCollect{
		ID,	//主键
		USER_ID,	//用户ID
		PRODUCT_ID,	//商品ID|店铺ID
		PRODUCT_NAME,	//商品名称|店铺名称
		PRODUCT_TYPE,	//商品类型
		TYPE,	//收藏类型 0店铺收藏 1商品收藏 3品牌收藏
		CTIME,	//收藏时间
		UP_ID,	//更新人
		UTIME,	//更新时间
		IS_ENABLE,	//1：正常；0：删除
	}

	public enum UserNote{
		ID,	//ID
		USER_ID,	//用户ID
		NOTE_TITLE,	//笔记标题
		NOTE_CONTENT,	//笔记内容
		IS_REMIND,	//是否提醒：0是，1否
		BERORE_REMIND,	//提前提醒时间(毫秒)
		REMARK,	//备注
		CTIME,	//创建时间
		UP_ID,	//修改人
		UTIME,	//修改时间
		IS_ENABLE,	//启用：1有效，0无效
	}
}