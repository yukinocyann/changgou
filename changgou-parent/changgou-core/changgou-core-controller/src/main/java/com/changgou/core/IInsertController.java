package com.changgou.core;

import entity.Result;


public interface IInsertController<T> {
    /**
     * 添加记录
     * @param record
     * @return
     */
    Result insert(T record);

}