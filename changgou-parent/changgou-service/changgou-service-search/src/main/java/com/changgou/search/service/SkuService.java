package com.changgou.search.service;

import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/9/26 14:44
 * @description 标题
 * @package com.changgou.search.service
 */
public interface SkuService {
    void importSku();


    Map search(Map<String, String> searchMap);
}
