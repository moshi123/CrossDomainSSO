package com.nextyu.sso.server.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户端应用.
 *
 * @author nextyu
 * @version 1.0
 */
public class ClientSystem implements Serializable {

    /**
     * 唯一标识.
     */
    private String id;
    /**
     * 系统名称.
     */
    private String name;

    /**
     * 应用基路径，代表应用访问起始点 .
     */
    private String baseURL;
    /**
     * 应用主页面URI，baseUrl + homeUri = 主页URL.
     */
    private String homeURI;
    /**
     * 系统间内部通信地址.
     */
    private String innerAddress;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getHomeURI() {
        return homeURI;
    }

    public void setHomeURI(String homeURI) {
        this.homeURI = homeURI;
    }

    public String getInnerAddress() {
        return innerAddress;
    }

    public void setInnerAddress(String innerAddress) {
        this.innerAddress = innerAddress;
    }

    public String getHomeURL() {
        return baseURL + homeURI;
    }

    /**
     * 与客户端系统通信，通知客户端token过期.
     *
     * @param vt
     * @param tokenTimeout
     * @return
     */
    public Date noticeTimeout(String vt, int tokenTimeout) {
        // TODO 与客户端通信处理有效期
        return null;
    }

    /**
     * 通知客户端用户退出.
     *
     * @param vt
     */
    public void noticeLogout(String vt) {
        // TODO 通知客户端用户退出
    }

    /**
     * 通知客户端服务端关闭，客户端收到信息后执行清除缓存操作.
     */
    public void noticeShutdown() {
        // TODO 通知客户端服务端关闭，客户端收到信息后执行清除缓存操作
    }

}
