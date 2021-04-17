package com.changgou.user.service;
import com.changgou.user.pojo.Address;
import com.github.pagehelper.PageInfo;
import java.util.List;
import com.changgou.core.service.CoreService;
/****
 * @Author:admin
 * @Description:Address业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface AddressService extends CoreService<Address> {

    List<Address> list(String username);
}
