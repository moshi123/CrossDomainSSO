package com.nextyu.sso.server.domain;

import com.nextyu.sso.common.util.StringUtil;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
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
        String url = innerAddress + "/notice/timeout?vt=" + vt + "&tokenTimeout=" + tokenTimeout;
        try {
            String result = httpAccess(url);
            return StringUtil.isEmpty(result) ? null : new Date(Long.parseLong(result));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * 通知客户端用户退出.
     *
     * @param vt
     */
    public boolean noticeLogout(String vt) {
        String url = innerAddress + "/notice/logout?vt=" + vt;
        try {
            String result = httpAccess(url);
            return Boolean.parseBoolean(result);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通知客户端服务端关闭，客户端收到信息后执行清除缓存操作.
     */
    public boolean noticeShutdown() {
        String url = innerAddress + "/notice/shutdown";
        try {
            String result = httpAccess(url);
            return Boolean.parseBoolean(result);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String httpAccess(String theUrl) throws Exception {
        URL url = new URL(theUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(500);
        InputStream is = conn.getInputStream();
        conn.connect();

        byte[] buff = new byte[is.available()];
        is.read(buff);
        String ret = new String(buff, "utf-8");

        conn.disconnect();
        is.close();

        return ret;
    }

}
