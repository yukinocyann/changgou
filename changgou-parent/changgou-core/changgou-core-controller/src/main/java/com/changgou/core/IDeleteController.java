package com.changgou.core;

import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


public interface IDeleteController<T> {
    /**
     * 根据ID 删除
     *
     * @param id
     * @return
     */
    Result deleteById(Object id);
}
