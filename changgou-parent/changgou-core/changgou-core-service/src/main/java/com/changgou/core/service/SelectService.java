package com.changgou.core.service;

import java.util.List;


public interface SelectService<T> {

    /**
     * 查询所有
     *
     * @return
     */
    public List<T> selectAll();

    /**
     * 查询一个对象
     *
     * @param id
     * @return
     */
    public T selectByPrimaryKey(Object id);

    /**
     * 根据条件查询 等号条件
     *
     * @param record
     * @return
     */
    public List<T> select(T record);


}
