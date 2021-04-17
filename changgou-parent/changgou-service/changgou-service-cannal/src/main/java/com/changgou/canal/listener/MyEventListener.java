package com.changgou.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.*;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/*
 * @Date 2021/2/26 20:03
 * @param null
 * @return
 * @Description //
 **/
@CanalEventListener//修饰类，当数据库的表被修改了就会执行该类中的某一些方法
public class MyEventListener {

    /*@InsertListenPoint//当某一个表发生了添加的操作的时候触发以下的方法
    public void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        rowData.getAfterColumnsList().forEach((c) -> System.err.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }*/

    //实现我们的业务逻辑：更新数据到redis中
   /* @UpdateListenPoint(destination = "example",schema = "changgou_content",table = "tb_content")
    public void onEvent1(CanalEntry.RowData rowData) {

        System.err.println("UpdateListenPoint");
        //更新前的数据
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            String name = column.getName();
            String value = column.getValue();
            System.out.println(name+":"+value);
        }
        System.out.println("=====================================================");
        //更新后数据
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            String name = column.getName();
            String value = column.getValue();
            System.out.println(name+":"+value);
        }

    }*/

  /*  @DeleteListenPoint
    public void onEvent3(CanalEntry.EventType eventType) {
        System.err.println("DeleteListenPoint");
    }*/

    //客制化，可以指定任意的类型（insert ,delete ,update ,create index ）
    //当changgou_content数据库中的tb_content被别人 insert update delete的时候都需要监听


    @Autowired
    private ContentFeign contentFeign;

    //字符串类型
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    //destination 指定要监听的目的地和配置文件中的值一致 和canal-server中的目录的名称一致
    // schema  指定监听的库
    // table 监听的表
    // eventType 监听的操作类型
    @ListenPoint(destination = "example",
            schema = "changgou_content",
            table = {"tb_content"},
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE})
    public void onEvent4(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.获取被修改的数据的行的category_id的字段对应的值
        String columnValue = getColumnValue(eventType, rowData);
        //2.调用feign 从广告微服务中获取最新的该分类下的所有的广告的列表
            //2.1 加入起步依赖
            //2.2 写一个接口@feignclient注解指定调用的服务的名称（name的值）
            //2.3 写一个方法 和调用的时候 调用的服务的controller一致
            //2.4 写一个方法实现接口（调用的服务方写一个controller来实现）
            //2.5 使用feign方 需要添加feign依赖
            //2.6 开启 feign 注入
        Result<List<Content>> result = contentFeign.findByCategory(Long.valueOf(columnValue));
        List<Content> contentList = result.getData();
        //3.链接到redis中 将数据存储到redis覆盖
        stringRedisTemplate.boundValueOps("content_"+columnValue).set(JSON.toJSONString(contentList));
        //set key  value

    }

    //获取category_id的列的值
    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId = "";
        if (eventType == CanalEntry.EventType.DELETE) {
            //获取删除之前的数据
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                //如果是列名为category_id
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();//分类的ID 的值    比如：1   2
                    break;
                }
            }
        } else {
            //获取更新或者添加之后的数据
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : afterColumnsList) {
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();//分类的ID 的值    比如：1   2
                    break;
                }
            }
        }
        return categoryId;
    }
}
