package com.changgou.core.service;


public interface InsertService<T> {
    /**
     * 添加记录
     * @param record
     * @return
     */
    int insert(T record);

}