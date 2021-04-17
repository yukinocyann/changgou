package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/9/28 15:08
 * @description 标题
 * @package com.changgou.search.service.impl
 */
@Component
public class SearchResultMapperImpl implements SearchResultMapper {
    /**
     * 手动的建立映射结果 将ES中的JSON的数据 转换POJO 并设置分页的结果 总记录数。。。。
     *
     * @param response
     * @param clazz
     * @param pageable
     * @param <T>
     * @return
     */
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
        //主要的目标：实现从es获取高亮的数据 再进行映射 返回POJO

        //1.创建当前页的记录列表数据  获取高亮的值 设置到该List中
        List<T> content = new ArrayList<T>();
        //2.创建分页对象   --->有
        SearchHits hits = response.getHits();
        if (hits == null || hits.getTotalHits() <= 0) {
            return new AggregatedPageImpl<T>(content);
        }
        for (SearchHit hit : hits) {
            //1.获取数据（没有高亮的）
            String sourceAsString = hit.getSourceAsString();
            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
            //2. 获取数据 高亮的数据 替换了没有高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null &&
                    highlightFields.get("name") != null &&
                    highlightFields.get("name").getFragments() != null &&
                    highlightFields.get("name").getFragments().length > 0) {
                HighlightField highlightField = highlightFields.get("name");
                Text[] fragments = highlightField.getFragments();
                StringBuffer sb = new StringBuffer();
                for (Text fragment : fragments) {
                    String string = fragment.string();//高亮的数据
                    sb.append(string);//todo按照需求决定
                }
                String s = sb.toString();//高亮值

                if (!StringUtils.isEmpty(s)) {
                    skuInfo.setName(s);//替换
                }
            }

            //3.转换成POJO 存储到content中
            content.add((T) skuInfo);
        }
        //3.获取总记录数
        long totalHits = hits.getTotalHits();
        //4.获取聚合函数的结果对象
        Aggregations aggregations = response.getAggregations();
        //5.获取游标的ID
        String scrollId = response.getScrollId();

        return new AggregatedPageImpl<T>(content, pageable, totalHits, aggregations, scrollId);
    }
}
