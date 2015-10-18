package com.nextyu.sso.server.util;

import com.nextyu.sso.common.util.StringUtil;
import com.nextyu.sso.server.domain.ClientSystem;
import com.nextyu.sso.server.domain.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储VT_USER信息.
 *
 * @author nextyu
 * @version 1.0
 */
public class TokenManager {

    private static Logger logger = LoggerFactory.getLogger(TokenManager.class);

    private static final Config config = SpringContextUtil.getBean(Config.class);

    /**
     * 定时器.
     */
    private static final Timer timer = new Timer(true);

    static {
        // 定时任务，1分钟执行一次
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Map.Entry<String, Token> entry : DATA_MAP.entrySet()) {
                    String vt = entry.getKey();
                    Token token = entry.getValue();
                    Date expired = token.expired;
                    Date now = new Date();

                    // 当前时间大于过期时间
                    if (now.compareTo(expired) > 0) {
                        // 因为令牌支持自动延期服务，并且应用客户端缓存机制后，
                        // 令牌最后访问时间是存储在客户端的，所以服务端向所有客户端发起一次timeout通知，
                        // 客户端根据lastAccessTime + tokenTimeout计算是否过期，
                        // 若未过期，用各客户端最大有效期更新当前过期时间

                        List<ClientSystem> clientSystems = config.getClientSystems();

                        // 多个客户端中，取最大的过期时间
                        Date maxClientExpired = expired;
                        for (ClientSystem clientSystem : clientSystems) {
                            // 客户端根据lastAccessTime(保存在客户端) + tokenTimeout计算过期时间
                            Date clientExpired = clientSystem.noticeTimeout(vt, config.getTokenTimeout());
                            if (clientExpired != null && clientExpired.compareTo(now) > 0) {
                                maxClientExpired = maxClientExpired.compareTo(clientExpired) < 0 ? clientExpired : maxClientExpired;
                            }
                        }

                        // 客户端最大过期时间和当前时间对比
                        if (maxClientExpired.compareTo(now) > 0) { // 未过期
                            logger.debug("更新过期时间到" + maxClientExpired);
                            token.expired = maxClientExpired;
                        } else {
                            // 已过期
                            logger.debug("清除过期token：" + vt);
                            // 已过期，清除对应token
                            DATA_MAP.remove(vt);
                        }

                    }
                }
            }
        }, 60 * 1000, 60 * 1000);
    }

    /**
     * 避免静态类被实例化.
     */
    private TokenManager() {

    }

    /**
     * 复合结构体，含loginUser与过期时间expried两个成员.
     */
    private static class Token {
        /**
         * 登录 用户对象.
         */
        private LoginUser loginUser;
        /**
         * 过期时间.
         */
        private Date expired;
    }

    /**
     * 令牌存储结构.
     */
    private static final Map<String, Token> DATA_MAP = new ConcurrentHashMap<>();

    /**
     * 验证vt有效性.
     *
     * @param vt
     * @return
     */
    public static LoginUser validate(String vt) {
        Token token = DATA_MAP.get(vt);
        return token == null ? null : token.loginUser;
    }

    /**
     * 户授权成功后存入授权信息.
     *
     * @param vt
     * @param loginUser
     */
    public static void addToken(String vt, LoginUser loginUser) {
        Token token = new Token();
        token.loginUser = loginUser;

        // 非自动登录时的处理
        token.expired = new Date(new Date().getTime() + config.getTokenTimeout() * 60 * 1000);

        // TODO 自动登录时，有效期的处理

        DATA_MAP.put(vt, token);
    }

    /**
     * 作废vt.
     *
     * @param vt
     */
    public static void invalid(String vt) {
        if (!StringUtil.isEmpty(vt)) {
            DATA_MAP.remove(vt);
        }
    }

}
