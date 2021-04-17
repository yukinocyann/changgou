package com.changgou.core.service;


public interface UpdateService<T> {

    /**
     * 根据对象进行更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(T record);
}
