package com.changgou.core.service;

/*
 * @Date 2021/2/24 16:17
 * @param null
 * @return
 * @Description //
 **/
public interface CoreService<T> extends
        DeleteService<T>,
        InsertService<T>,
        PagingService<T>,
        SelectService<T>,
        UpdateService<T> {
    //批量进行删除
    //批量进行添加


}
