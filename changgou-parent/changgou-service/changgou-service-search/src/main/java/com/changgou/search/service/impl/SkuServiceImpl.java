package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/*
 * @Date 2021/3/1 19:53
 * @param null
 * @return
 * @Description //
 **/
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SearchResultMapperImpl searchResultMapperImpl;

    @Override
    public void importSku() {
        //1.先根据商品微服务feign 查询符合条件的sku的列表数据
        //1.1 service-goods-api创建一个feign接口
        //1.2 编写一个类 加入一个注解@feignclent(name="goods")
        //1.3 编写一个方法：查询符合条件的sku的列表数据
        //1.4 在service-goods微服务中实现controller-service-dao的调用。相当于实现feign接口

        //1.5 添加依赖 ，开启feignclients
        //1.6 注入feign 调用feign
        Result<List<Sku>> result = skuFeign.findByStatus("1");
        List<Sku> skuList = result.getData();
        if (skuList != null && skuList.size() > 0) {
            //转换将List<Sku> 转换成List<SkuInfo>
            List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuList), SkuInfo.class);
            //设置规格的数据到specMap属性中
            for (SkuInfo skuInfo : skuInfoList) {
                String spec = skuInfo.getSpec();//{"电视音响效果":"环绕","电视屏幕尺寸":"20英寸","尺码":"165"}
                skuInfo.setSpecMap(JSON.parseObject(spec, Map.class));
            }
            //2.把数据添加到ES中
            skuEsMapper.saveAll(skuInfoList);
        }

    }

    /**
     * 搜索
     *
     * @param searchMap 搜索的条件 {“keywords”:"手机","category":"二手手机"}
     * @return
     */
    @Override
    public Map search(Map<String, String> searchMap) {
        //1.接收页面传递过来的关键字
        String keywords = searchMap.get("keywords");
        if (StringUtils.isEmpty(keywords)) {
            keywords = "华为";
        }
        //2.创建查询对象的 构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //3.设置查询条件 match 先分词 再进行查询 指定查询的字段和要查询的值


        //3.1 设置 商品分类的分组查询条件 group by categoryName
        // terms就相当于group by   参数指定分组的别名，将来需要根据该别名获取分组的结果值   field:指定要分组的字段名 size：指定最大的分组的值

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(10000));

        //3.2 设置 商品品牌的分组查询条件 group by brandName
        // terms就相当于group by   参数指定分组的别名，将来需要根据该别名获取分组的结果值   field:指定要分组的字段名 size：指定最大的分组的值

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandgroup").field("brandName").size(10000));

        //3.2 设置 商品规格的分组查询条件 group by spec.keyword？？？？？？？
        // terms就相当于group by   参数指定分组的别名，将来需要根据该别名获取分组的结果值   field:指定要分组的字段名 size：指定最大的分组的值

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecgroup").field("spec.keyword").size(10000));


        //3.2.5 设置高亮 1.设置高亮的字段 2.设置前缀和后缀
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name"));
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));


        //多字段查询 指定要查询的文本 以及指定从哪一些字段中进行搜索
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords,"name","brandName","categoryName"));
        //nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));


        //3.3 过滤查询 多条件组合查询    must  must_not  should  filter

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


        //3.3.1 商品分类的过滤查询    term查询：词条查询 特点：不分词 进行整体的匹配
        String category = searchMap.get("category");
        if(!StringUtils.isEmpty(category)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", category));
        }

        //3.3.2 商品品牌的过滤查询    term查询：词条查询 特点：不分词 进行整体的匹配
        String brand = searchMap.get("brand");
        if(!StringUtils.isEmpty(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", brand));
        }

        //3.3.3 商品规格的过滤查询 {"keywords":"手机","category":"你点击到的分类的值","brand":"华为","spec_网络制式":"电信2G","spec_影像效果":"环绕"}
        for (Map.Entry<String, String> stringStringEntry : searchMap.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            if(key.startsWith("spec_")){
                //添加过滤查询的条件
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword", value));
            }
        }

        //3.3.4 商品价格的过滤查询 {"keywords":"手机","spec_影像效果":"环绕","price":"0-100"}
        String price = searchMap.get("price");
        if(!StringUtils.isEmpty(price)){
            String[] split = price.split("-");
            if(split[1].equalsIgnoreCase("*")){
                // 3000-*
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            }else {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]).lte(split[1]));
            }
        }


        //3.3.5 商品价格的过滤查询 {"keywords":"手机","spec_影像效果":"环绕","price":"0-100",sortField:"price",sortRule:"DESC"}
        //获取排序的字段的值和要排序的类型
        String sortField = searchMap.get("sortField");// price
        String sortRule = searchMap.get("sortRule");// DESC ASC

        if(!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
            //nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equalsIgnoreCase("DESC")?SortOrder.DESC:SortOrder.ASC));
            // fieldSort(sortField) 设置排序的字段
            // order() 设置排序的类型
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
        }

        // 3.3.6 分页查询 {"keywords":"手机","spec_影像效果":"环绕","price":"0-100",sortField:"price",sortRule:"DESC","pageNum":"1"}
        //参数1 指定当前的页码值 0 标识第一页
        //参数2 指定每页显示的行
        String pageNumString = searchMap.get("pageNum");
        Integer pageNum=1;
        Integer pageSize=40;
        if (!StringUtils.isEmpty(pageNumString)) {
            pageNum = Integer.parseInt(pageNumString);
        }
        Pageable pageable = PageRequest.of(pageNum-1,pageSize);

        // 构建分页查询
        nativeSearchQueryBuilder.withPageable(pageable);

        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);

        //4.构建查询对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        //5.执行分页查询
        //参数1 指定要查询的对象
        //参数2 指定要查询到的结果的映射的字节码对象 就是skuinfo
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(query, SkuInfo.class,searchResultMapperImpl);
        //6.获取结果 总页数 总记录数 当前页记录数

        //6.1 获取商品分类的分组查询的结果
      /*StringTerms stringTerms = (StringTerms) skuInfos.getAggregation("skuCategorygroup");
        List<String> categoryList = new ArrayList<String>();
        if(stringTerms !=null){
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();//商品分类的名称
                categoryList.add(keyAsString);
            }
        }*/
        List<String> categoryList = getList(skuInfos, "skuCategorygroup");

        //6.2 获取商品品牌的分组查询的结果
        /*StringTerms stringTermsBrand = (StringTerms) skuInfos.getAggregation("skuBrandgroup");
        List<String> brandList = new ArrayList<String>();
        if(stringTermsBrand !=null){
            for (StringTerms.Bucket bucket : stringTermsBrand.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();//商品分类的名称
                brandList.add(keyAsString);
            }
        }*/
        List<String> brandList = getList(skuInfos, "skuBrandgroup");

        //6.3 获取规格的列表数据Map<String,Set<String>> specMap;(一顿操作)
        StringTerms skuSpecgroup = (StringTerms) skuInfos.getAggregation("skuSpecgroup");

        Map<String, Set<String>> specMap = getStringSetMap(skuSpecgroup);


        //当前的页的记录
        List<SkuInfo> content = skuInfos.getContent();
        for (SkuInfo skuInfo : content) {
            System.out.println("目前有没有高亮：>>>>>>>>>"+skuInfo.getName());
        }
        //总页数
        int totalPages = skuInfos.getTotalPages();
        //总记录数
        long totalElements = skuInfos.getTotalElements();

        //7.封装返回
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("rows", content);
        resultMap.put("totalPages", totalPages);
        resultMap.put("total", totalElements);
        resultMap.put("categoryList", categoryList);
        resultMap.put("brandList", brandList);
        resultMap.put("specMap", specMap);
        resultMap.put("pageNum", pageNum);
        resultMap.put("pageSize", pageSize);
        return resultMap;
    }

    /**
     * 根据分组的别名获取别名对应的分组的结果
     *
     * @param skuInfos
     * @param name
     * @return
     */
    private List<String> getList(AggregatedPage<SkuInfo> skuInfos, String name) {
        StringTerms stringTerms = (StringTerms) skuInfos.getAggregation(name);
        List<String> list = new ArrayList<String>();
        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();//商品分类的名称
                list.add(keyAsString);
            }
        }
        return list;
    }

    /**
     * 解析规格的字符串数据 返回map
     *
     * @param stringTermsSpec
     * @return  {"电视影像效果":["小影院","立体声"]}
     */
    private Map<String, Set<String>> getStringSetMap(StringTerms stringTermsSpec) {

        Map<String, Set<String>> specMap = new HashMap<String, Set<String>>();

        if (stringTermsSpec != null) {
            Set<String> values = new HashSet<String>();

            for (StringTerms.Bucket bucket : stringTermsSpec.getBuckets()) {
                // {"电视音响效果":"小影院","电视屏幕尺寸":"20英寸","尺码":"165"}
                Map<String, String> map = JSON.parseObject(bucket.getKeyAsString(), Map.class);
                for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                    // 电视音响效果
                    String key = stringStringEntry.getKey();
                    // 小影院
                    String value = stringStringEntry.getValue();
                    values = specMap.get(key);
                    if(values==null){
                        values = new HashSet<String>();
                    }
                    values.add(value);//["小影院"]

                    specMap.put(key,values);
                }
            }
        }
        return specMap;
    }
}
