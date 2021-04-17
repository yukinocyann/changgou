package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作es的dao
 * @author ljh
 * @version 1.0
 * @date 2020/9/26 15:01
 * @description 标题
 * @package com.changgou.search.dao
 */
@Repository
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo,Long> {
    /*@Query(value="{\n" +
            "  \"query\": {\n" +
            "    \"match\": {\n" +
            "      \"name\": \"?1\"\n" +
            "    }\n" +
            "  }\n" +
            "}")
    List<SkuInfo> findALLL(String keywords);*/
}
