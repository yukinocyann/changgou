package com.changgou.oauth.service;

import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2020/10/8 16:17
 * @description 标题
 * @package com.changgou.oauth.service
 */
public interface UserLoginService {
    /**
     * 模拟浏览器发送请求 申请令牌
     * @param grant_type
     * @param username
     * @param password
     * @param clientId
     * @param secret
     * @return
     */
    Map<String, String> login(String grant_type, String username, String password, String clientId, String secret);
}
