package com.changgou.user.controller;
import com.changgou.user.pojo.Areas;
import com.changgou.user.service.AreasService;
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
@RequestMapping("/areas")
@CrossOrigin
public class AreasController extends AbstractCoreController<Areas>{

    private AreasService  areasService;

    @Autowired
    public AreasController(AreasService  areasService) {
        super(areasService, Areas.class);
        this.areasService = areasService;
    }
}
