package com.changgou.user.service.impl;
import com.changgou.user.dao.AreasMapper;
import com.changgou.user.pojo.Areas;
import com.changgou.user.service.AreasService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import com.changgou.core.service.impl.CoreServiceImpl;
import java.util.List;
/****
 * @Author:admin
 * @Description:Areas业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class AreasServiceImpl extends CoreServiceImpl<Areas> implements AreasService {

    private AreasMapper areasMapper;

    @Autowired
    public AreasServiceImpl(AreasMapper areasMapper) {
        super(areasMapper, Areas.class);
        this.areasMapper = areasMapper;
    }
}
