package com.changgou.user.controller;
import com.changgou.user.pojo.Cities;
import com.changgou.user.service.CitiesService;
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
@RequestMapping("/cities")
@CrossOrigin
public class CitiesController extends AbstractCoreController<Cities>{

    private CitiesService  citiesService;

    @Autowired
    public CitiesController(CitiesService  citiesService) {
        super(citiesService, Cities.class);
        this.citiesService = citiesService;
    }
}
