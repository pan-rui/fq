package gen;

import com.hy.core.ColumnProcess;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-03-31 10:02)
 * @version: \$Rev: 2898 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-06-07 19:37:03 +0800 (周三, 07 6月 2017) $
 */
public class GenerateAction {
    private String dbName;
    private String tableName;
    private String actionPackage;
    private String actionPath;
    private String tableEnumPackage;

    public GenerateAction() {
    }

    public GenerateAction(String tableName, String actionPackage, String actionPath) {

        this.tableName = tableName;
        this.actionPackage = actionPackage;
        this.actionPath = actionPath;
    }

    public GenerateAction(String dbName, String actionPackage, String actionPath, String tableEnumPackage) {
        this.dbName = dbName;
        this.actionPackage = actionPackage;
        this.actionPath = actionPath;
        this.tableEnumPackage = tableEnumPackage;
    }

    public static void main(String[] args) throws IOException {
        //TODO:待添加参数
        String driverClass = "com.mysql.jdbc.Driver";
        String schema = "fq";
//        String jdbcUrl = "jdbc:mysql://120.25.65.82:3306/"+schema+"?useUnicode=true&characterEncoding=UTF-8";
        String jdbcUrl = "jdbc:mysql://192.168.3.254:3306/"+schema+"?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
        String userId="root";
        String password = "hengyong321";
        GenerateAction generateAction = new GenerateAction(schema,"com.hy.action", "D:\\workspace\\fq\\data\\src\\test\\java\\com\\hy\\gen\\","com.hy.core");//TODO:可指定单个Table
      // generateAction.tableName="USER";      //单表情况
        generateAction.tableName = "%";
//        StringBuffer tables = new StringBuffer();
/*        tables.append("\t//表名列表 \n\t")
                .append("public enum ")
                .append("Tables").append("{\n");*/
        StringBuffer sbTab = new StringBuffer();
        sbTab.append("package " + generateAction.tableEnumPackage + ";\n");
        sbTab.append("public class Table {\n");
        sbTab.append("\tpublic static final String SEPARATE = \".\";\n");
        sbTab.append("\tpublic static final String SEPARATE_SPLIT = \",\";\n");
        sbTab.append("\tpublic static final String SEPARATE_CACHE = \"-\";\n");
        sbTab.append("\tpublic static final String FIELD_INTERVAL = \"_\";\n");
//        sbTab.append("\tpublic static final String USER_ID = \"USER_ID\";\n");
        sbTab.append("\tpublic static final String SPACE = \" \";\n");
        sbTab.append("\tpublic static final String FQ = \"fq.\";\n");
        sbTab.append("\tpublic static final String ID = \"ID\";\n");
        sbTab.append("\tpublic static final String IS_ENABLE = \"IS_ENABLE\";\n");
        sbTab.append("\tpublic static final String SEQ = \"SEQ\";\n");
        sbTab.append("\tpublic static final String USER_ID = \"USER_ID\";\n");
        sbTab.append("\tpublic static final String UP_ID = \"UP_ID\";\n");
        sbTab.append("\tpublic static final String UTIME = \"UTIME\";\n");
        sbTab.append("\tpublic static final String DESC = \"DESC\";\n");
        sbTab.append("\tpublic static final String ASC = \"ASC\";\n");
        sbTab.append("\tpublic static final String TYPE = \"TYPE\";\n");
        sbTab.append("\tpublic static final String REMARK = \"REMARK\";\n");
/*        sbTab.append("\tpublic static final String SEPARATE_TREE = \">\";\n");

        sbTab.append("\tpublic static final String ORDER_BY_DESC = \"DESC\";\n");
        sbTab.append("\tpublic static final String ORDER_BY_ASC = \"ASC\";\n");
        sbTab.append("\n\t//通用字段\n");
        sbTab.append("\tpublic static final String TENANT_ID = \"TENANT_ID\";\n");
        sbTab.append("\tpublic static final String CREATE_TIME = \"CREATE_TIME\";\n");
        sbTab.append("\tpublic static final String CREATE_USER_ID = \"CREATE_USER_ID\";\n");
        sbTab.append("\tpublic static final String IS_SEALED = \"IS_SEALED\";\n");
        sbTab.append("\tpublic static final String SEALED_USER_ID = \"SEALED_USER_ID\";\n");
        sbTab.append("\tpublic static final String SEALED_TIME = \"SEALED_TIME\";\n");
        sbTab.append("\tpublic static final String IS_VALID = \"IS_VALID\";\n");
        sbTab.append("\tpublic static final String IS_ENABLED = \"IS_ENABLED\";\n");
        sbTab.append("\tpublic static final String UPDATE_USER_ID = \"UPDATE_USER_ID\";\n");
        sbTab.append("\tpublic static final String UPDATE_TIME = \"UPDATE_TIME\";\n");*/

        File tbFile=new File(generateAction.actionPath+"Table.java");
        if(!tbFile.getParentFile().exists()) tbFile.getParentFile().mkdirs();
        FileWriter tbFw=new FileWriter(tbFile);
        StringBuffer sbAction = new StringBuffer();
        try {
            Class.forName(driverClass);
            Connection conn = DriverManager.getConnection(jdbcUrl, userId, password);
            DatabaseMetaData metaData=conn.getMetaData();
            ResultSet rs=null;
//            if(generateAction.tableName==null)
//                rs= metaData.getTables(conn.getCatalog(), "dems", generateAction.tableName, new String[]{"TABLE"});
//            else
                rs= metaData.getTables(conn.getCatalog(), "dems", generateAction.tableName, new String[]{"TABLE"});
            int tableCount=0;
            StringBuffer sbTabColumns = new StringBuffer();

            StringBuffer sbTables = new StringBuffer();

            while (rs.next()) {
                String table = rs.getString("TABLE_NAME");
                String tableComment = rs.getString("REMARKS");
                System.out.println("第" + ++tableCount + "张表,表名为" + table);
//                System.out.println(rs.getString("TABLE_TYPE"));

//                ResultSet tableRs = metaData.getTables(conn.getCatalog(), "root", table, new String[]{"TABLE"});
                ResultSet tableRs = metaData.getColumns(conn.getCatalog(), "fq", table, "");
//                ResultSetMetaData rsm = tableRs.getMetaData();
/*                for (int i = 1; i <= rsm.getColumnCount(); i++) {
                    String columnName = MybatisXml.getColumnName(rsm.getColumnName(i));
                    System.out.println("列名"+rsm.getColumnName(i));
                }*/
                String tableAlias= ColumnProcess.encryptVal(table);
/*                if(table.indexOf("_")<2)
                    tableName = table.substring(table.indexOf("_") + 1);*/
                sbTables.append("\t//" + tableComment + "\n\t" +
                        "public static final String ")
                        .append(table).append("=").append("\""+table + "\";\n");                   //表名常量
                sbTabColumns.append("\n\tpublic enum ")
                        .append(StringUtils.capitalise(tableAlias)).append("{\n");



                for (int i = 1; tableRs.next(); i++) {
                    String column=tableRs.getString("COLUMN_NAME");
                    String columnComment = tableRs.getString("REMARKS");
//                    System.out.println(tableRs.getString("TYPE_NAME")+"\t"+tableRs.getInt("COLUMN_SIZE")+"\t"+tableRs.getInt("DATA_TYPE"));
                    sbTabColumns.append("\t\t").append(column).append(",").append("\t//").append(columnComment).append("\n");
//                    sbTabColumns.append("\t\t").append(ColumnProcess.encryptVal(column)).append(",").append("\t//").append(columnComment).append("\n");
                }
                sbTabColumns.append("\t}\n");
/*                File javaFile = new File(generateAction.actionPath +  actionName+".java");
//                File javaFile = new File("./core/src/main/resources/" + mybatisXml.javaPath.replace(".", "/") + "/I" + poName+"Dao.java");
                if(!javaFile.getParentFile().exists()) javaFile.getParentFile().mkdirs();
                FileWriter fos=new FileWriter(javaFile);//XML文件生成
                fos.write(controller.toString());
                fos.flush();
                fos.close();*/
//                tables.append("\t\t").append(table).append(",\t//").append(tableComment).append("\n");
            }
//            tables.append("\t}\n");

            sbTab.append(sbTables.toString());

//            sbTab.append(tables.toString());

            sbTab.append(sbTabColumns.toString());

            sbTab.append("}");

//            System.out.println(sbTab.toString());
            tbFw.write(sbTab.toString());
            tbFw.flush();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("加载驱动失败。。。..");
        }finally {
            tbFw.close();
        }
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableEnumPackage() {
        return tableEnumPackage;
    }

    public void setTableEnumPackage(String tableEnumPackage) {
        this.tableEnumPackage = tableEnumPackage;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getActionPackage() {
        return actionPackage;
    }

    public void setActionPackage(String actionPackage) {
        this.actionPackage = actionPackage;
    }

    public String getActionPath() {
        return actionPath;
    }

    public void setActionPath(String actionPath) {
        this.actionPath = actionPath;
    }

    public String addMethod(String tableName, String tableAlias) {
        String methodName = "add" + StringUtils.capitalise(tableAlias);
        StringBuffer sb = new StringBuffer("    /**\n" +
                "     *" + tableName + "  增加\n" +
                "     * @param pv    请求参数封装对象\n" +
                "     * @param ddBB  操作库名\n" +
                "     * @return\n" +
                "     */").append("\n");
        sb.append("    @RequestMapping(value = \"" + tableAlias + "\", method = RequestMethod.POST)\n" +
                "    @ResponseBody");
        sb.append("    public BaseResult " + methodName + "(@EncryptProcess ParamsVo pv, @RequestHeader(Constants.TENANT_ID) String tenantId,@RequestAttribute String ddBB) {\n" +
                "        pv.getParams().addParams(\"TENANT_ID\", tenantId);\n" +
                "        baseService.add(pv.getParams(), ddBB + Table.SEPARATE + Table." + tableName + ");\n" +
                "        return new BaseResult(ReturnCode.OK);\n" +
                "    }").append("\n");
        return sb.toString();
    }

    public String deleteMethod(String tableName, String tableAlias) {
        String methodName = "del" + StringUtils.capitalise(tableAlias);
        StringBuffer sb = new StringBuffer("    /**\n" +
                "     *" + tableName + "  删除\n" +
                "     * @param pv    请求参数封装对象\n" +
                "     * @param ddBB  操作库名\n" +
                "     * @return\n" +
                "     */").append("\n");
        sb.append("    @RequestMapping(value = \"" + tableAlias + "\", method = RequestMethod.DELETE)\n" +
                "    @ResponseBody");
        sb.append("    public BaseResult " + methodName + "(@EncryptProcess ParamsVo pv, @RequestAttribute(Constants.USER_ID) String userId,@RequestAttribute String ddBB) {\n" +
                "        pv.getParams().addParams(\"DEL_USER_ID\", userId).addParams(\"DEL_TIME\", new Date());\n" +
                "        baseService.add(pv.getParams(), ddBB + Table.SEPARATE + Table." + tableName + ");\n" +
                "        return new BaseResult(ReturnCode.OK);\n" +
                "    }").append("\n");
        return sb.toString();
    }

    public String updateMethod(String tableName, String tableAlias) {
        String methodName = "update" + StringUtils.capitalise(tableAlias);
        StringBuffer sb = new StringBuffer("    /**\n" +
                "     *" + tableName + "  修改\n" +
                "     * @param pv    请求参数封装对象\n" +
                "     * @param ddBB  操作库名\n" +
                "     * @return\n" +
                "     */").append("\n");
        sb.append("    @RequestMapping(value = \"" + tableAlias + "\", method = RequestMethod.PUT)\n" +
                "    @ResponseBody");
        sb.append("    public BaseResult " + methodName + "(@EncryptProcess ParamsVo pv, @RequestAttribute(Constants.USER_ID) String userId,@RequestAttribute String ddBB) {\n" +
                "        pv.getParams().addParams(\"UPDATE_USER_ID\", userId).addParams(\"UPDATE_TIME\", new Date());\n" +
                "        int result=baseService.update(pv.getParams(), ddBB + Table.SEPARATE + Table." + tableName + ");\n" +
                "        return result > 0 ? new BaseResult(ReturnCode.OK) : new BaseResult(ReturnCode.FAIL);\n" +
                "    }").append("\n");
        return sb.toString();
    }

    public String queryMethod(String tableName, String tableAlias) {
        String methodName = "get" + StringUtils.capitalise(tableAlias);
        StringBuffer sb = new StringBuffer("    /**\n" +
                "     *" + tableName + "  获取\n" +
                "     * @param pv    请求参数封装对象\n" +
                "     * @param ddBB  操作库名\n" +
                "     * @return\n" +
                "     */").append("\n");
        sb.append("    @RequestMapping(value = \"" + tableAlias + "\", method = RequestMethod.GET)\n" +
                "    @ResponseBody");
        sb.append("    public BaseResult " + methodName + "(@EncryptProcess ParamsVo pv, @RequestAttribute(Constants.USER_ID) String userId,@RequestAttribute String ddBB) {\n" +
                "        return new BaseResult(0,baseService.queryList(pv.getParams(),null,ddBB + Table.SEPARATE + Table." + tableName + "));\n" +
                "    }").append("\n");
        return sb.toString();
    }
}
