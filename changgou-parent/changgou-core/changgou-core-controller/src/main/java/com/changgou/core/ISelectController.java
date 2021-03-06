package com.changgou.core;

import entity.Result;

import java.util.List;


public interface ISelectController<T> {
    //根据ID 获取信息
    public Result<T> findById(Object id);


    //根据ID 获取信息列表
    public Result<List<T>> findAll();
}
