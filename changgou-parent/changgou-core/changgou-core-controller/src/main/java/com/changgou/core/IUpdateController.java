package com.changgou.core;

import com.github.pagehelper.PageInfo;
import entity.Result;


public interface IUpdateController<T> {

    /**
     * 根据对象进行更新 根据ID
     *
     * @param record
     * @return
     */
    Result updateByPrimaryKey(T record);
}
