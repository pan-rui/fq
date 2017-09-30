package com.hy.task;

import com.hy.core.DataSourceHolder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by panrui on 2015/9/13.
 */
public class RecoverDBType implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DataSourceHolder.status= DataSourceHolder.DBType.normal;
    }
}
