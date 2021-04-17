package com.changgou.user.controller;
import com.changgou.user.pojo.Provinces;
import com.changgou.user.service.ProvincesService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.changgou.core.AbstractCoreController;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/provinces")
@CrossOrigin
public class ProvincesController extends AbstractCoreController<Provinces>{

    private ProvincesService  provincesService;

    @Autowired
    public ProvincesController(ProvincesService  provincesService) {
        super(provincesService, Provinces.class);
        this.provincesService = provincesService;
    }
}
