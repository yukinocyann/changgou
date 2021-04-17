package com.changgou.seckill.timer;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import entity.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/16 11:15
 * @description 标题
 * @package com.changgou.seckill.timer
 */
@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //定时 将查询到符合条件的秒杀商品的数据推送到redis中
    @Scheduled(cron = "0/10 * * * * ?")//cron表达式 指定的是何时执行定时任务
    public void loadGoodsPushRedis() {

        //0 获取当前的时间的5个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //12   20     14   22 点
        for (Date dateMenu : dateMenus) {
            //1.查询符合条件的mysql中的商品的数据
            //时间段的字符串
            String extName = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);
            //select * from tb_seckill_goods
            //where status=1 and stockCount>0 and startTime<=now() and endTime>now() and id not in (redis中已有的);
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //status=1
            criteria.andEqualTo("status", "1");
            //stockCount>0
            criteria.andGreaterThan("stockCount", 0);
            criteria.andGreaterThanOrEqualTo("startTime",dateMenu);
            criteria.andLessThan("endTime",DateUtil.addDateHour(dateMenu,2));
            // 所有的Key
            Set keys = redisTemplate.boundHashOps(extName).keys();
            if(keys!=null && keys.size()>0) {
                criteria.andNotIn("id", keys);
            }
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            //2.压入到redis中
            if(seckillGoods!=null) {
                for (SeckillGoods seckillGood : seckillGoods) {
                    //每一个key 都只需要保留2个小时
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX+extName).put(seckillGood.getId(), seckillGood);
                    //设置key过期时间 在指定的时间点就过期

                   // redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX+extName).expireAt(DateUtil.addDateHour(dateMenu,2));
                }
            }
        }


    }
}
