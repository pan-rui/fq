package com.hy.core;

import org.springframework.util.StringUtils;

/**
 * Created by panrui on 2015/9/12.
 */
public class DataSourceHolder {
    private static final ThreadLocal<DBType> localDB = new ThreadLocal<>();
    public static DBType status = DBType.normal;

    public static DBType getLocalDataSource() {
        DBType dbType = localDB.get();
        if (StringUtils.isEmpty(dbType))
            return DBType.master;
        return dbType;
    }

    public static void setLocalDataSource(DBType dbType) {
        localDB.set(dbType);
    }


    public enum DBType {
        master, slave1, normal;
//        master, slave1, normal,yg602,yg618;

       public DBType next() {
            DBType[] values = values();
            int target=this.ordinal();
            int index = ++target >= values.length-3 ? 0 : target;
            for (DBType dbType : values)
                if (dbType.ordinal() == index)
                    return dbType;
            return master;
        }
    }

}
