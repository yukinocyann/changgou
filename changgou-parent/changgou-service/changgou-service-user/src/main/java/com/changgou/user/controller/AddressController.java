package com.changgou.user.controller;

import com.changgou.user.config.TokenDecode;
import com.changgou.user.pojo.Address;
import com.changgou.user.service.AddressService;
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
@RequestMapping("/address")
@CrossOrigin
public class AddressController extends AbstractCoreController<Address> {

    private AddressService addressService;

    @Autowired
    private TokenDecode tokenDecode;

    @Autowired
    public AddressController(AddressService addressService) {
        super(addressService, Address.class);
        this.addressService = addressService;
    }


    @GetMapping("/user/list")
    public Result<List<Address>> list(){
        //1.获取当前的登录的用户
        String username = tokenDecode.getUsername();
        //2.根据当前的用户的名获取地址列表
        List<Address>list = addressService.list(username);
        //3.返回
        return new Result<List<Address>>(true,StatusCode.OK,"查询成功",list);

    }
}
