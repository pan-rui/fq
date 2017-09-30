package com.hy.annotation.impl;

import com.hy.core.Constants;
import com.hy.annotation.KeyInTable;
import com.hy.dao.BaseDao;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.Serializable;

/**
 * Created by lenovo on 2014/12/8.
 */
public class KeyInTableCheck implements ConstraintValidator<KeyInTable, Serializable> {
    private KeyInTable keyInTable;
    private BaseDao baseDao;
    private String column;

    @Override
    public void initialize(KeyInTable keyInTable) {
        this.keyInTable = keyInTable;
//        System.out.println("application========================>"+applicationContext);
        this.baseDao = (BaseDao) Constants.applicationContext.getBean(keyInTable.value());
        this.column = keyInTable.column();
    }

    @Override
    public boolean isValid(Serializable serializable, ConstraintValidatorContext constraintValidatorContext) {
        if (serializable == null) return false;
/*        if (StringUtils.isEmpty(column))
            return baseDao.queryById((long) serializable) != null;
        else
            return baseDao.queryByPros(new ParamsMap().addParams(column, serializable)).size() > 0;*/
        return true;
    }
}
