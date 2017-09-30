package com.hy.service;

import com.hy.base.BaseImpl;
import com.hy.base.BaseResult;
import com.hy.base.ReturnCode;
import com.hy.core.Constants;
import com.hy.core.ParamsMap;
import com.hy.core.Table;
import com.hy.dao.BaseDao;
import com.hy.util.ImgUtil;
import javafx.scene.control.Tab;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class CommonService {
    @Autowired
    private BaseImpl baseImpl;
    @Autowired
    private BaseDao baseDao;
    private String userTable = Table.FQ + Table.USER;
    private String saleTable = Table.FQ + Table.EMPLOYEE;
    public void batchAddSale() {

    }

    public BaseResult uploadImg (String appVer,String userId,  String phone, MultipartFile file,String fileType) throws IOException {
//        if(StringUtils.isEmpty(phone)) return new BaseResult(ReturnCode.HEADER_PARAMS_VERIFY_ERROR);
        String fileName = file.getOriginalFilename();
        boolean isUser=appVer.startsWith(Constants.USER);
        String path = (isUser?ImgUtil.USER_IMG_PATH:ImgUtil.SALE_IMG_PATH)+phone+"/"+fileType+"/"+ UUID.randomUUID().toString() + "." + fileName.split("\\.")[1];
        File newFile = new File(ImgUtil.BASE_PATH+path);
        if(!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
        file.transferTo(newFile);
        ParamsMap paramsMap=ParamsMap.newMap(Table.UserAttach.ATTACH_TYPE.name(),fileType).addParams(Table.UserAttach.URL.name(), path).addParams(Table.UserAttach.ATTACH_LEN.name(), newFile.length())
                .addParams(Table.UserAttach.UP_ID.name(), userId).addParams(Table.UserAttach.USER_ID.name(),userId).addParams(Table.UserAttach.IS_ENABLE.name(), 1);
        if("TX".equals(fileType)){
            baseDao.updateByProsInTab(isUser?userTable:saleTable, ParamsMap.newMap(Table.User.OSS_ID.name(), path).addParams(Table.ID, userId));
            paramsMap.addParams(Table.UserAttach.ATTACH_TYPE.name(), "9");
        }
        baseDao.insertUpdateByProsInTab(Table.FQ + Table.USER_ATTACH, paramsMap);
        return new BaseResult(ReturnCode.OK, path);
    }

    public BaseResult uploadApk ( String appType, MultipartFile file,String appVer,String upContent,String updateCount) throws IOException {
//        if(StringUtils.isEmpty(phone)) return new BaseResult(ReturnCode.HEADER_PARAMS_VERIFY_ERROR);
        String fileName = file.getOriginalFilename();
        String path = ImgUtil.APK_PATH+"/"+ fileName;
        File newFile = new File(ImgUtil.BASE_PATH+path);
        if(!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
        file.transferTo(newFile);
        int count=baseDao.insertByProsInTab(Table.FQ+Table.APP_VESION, ParamsMap.newMap(Table.AppVesion.APP_TYPE.name(),appType)
        .addParams(Table.AppVesion.VERSION.name(),appVer).addParams(Table.AppVesion.UPDATE_CONTENT.name(),upContent).addParams(Table.AppVesion.FILE_SIZE.name(),newFile.length()).addParams(Table.AppVesion.UPDATE_CONTENT.name(),updateCount));
        return count>0?new BaseResult(ReturnCode.OK, path):new BaseResult(ReturnCode.FAIL);
    }

    @CacheEvict(value ="tmp",allEntries = true,cacheManager = "cacheManager")
    public void clearTmpCache() {
        System.out.println("清除Tmp缓存....");
    }

}
