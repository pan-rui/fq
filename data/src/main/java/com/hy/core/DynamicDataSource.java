package com.hy.core;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by panrui on 2015/9/12.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        if (DataSourceHolder.status.equals(DataSourceHolder.DBType.normal))
            return DataSourceHolder.getLocalDataSource().name();
        else return DataSourceHolder.status.name();
    }
}
