package com.nextyu.sso.server.util;

import com.nextyu.sso.common.util.StringUtil;
import com.nextyu.sso.server.domain.ClientSystem;
import com.nextyu.sso.server.domain.LoginUser;
import com.nextyu.sso.server.service.AuthenticationHandler;
import com.nextyu.sso.server.service.PreLoginHandler;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 应用配置信息.
 *
 * @author nextyu
 * @version 1.0
 */
public class Config implements ResourceLoaderAware {

    private static Logger logger = LoggerFactory.getLogger(Config.class);

    private ResourceLoader resourceLoader;

    /**
     * 登录页面视图名称.
     */
    private String loginViewName = "login";

    /**
     * 令牌有效期，单位为分钟，默认30分钟.
     */
    private int tokenTimeout = 30;

    /**
     * 验证身份处理器.
     */
    private AuthenticationHandler authenticationHandler;

    /**
     * 登录前预处理器.
     */
    private PreLoginHandler preLoginHandler;

    /**
     * 客户端系统列表.
     */
    private List<ClientSystem> clientSystems = new ArrayList<>();

    public String getLoginViewName() {
        return loginViewName;
    }

    public void setLoginViewName(String loginViewName) {
        this.loginViewName = loginViewName;
    }

    public int getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(int tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    public AuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void setAuthenticationHandler(AuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    public PreLoginHandler getPreLoginHandler() {
        return preLoginHandler;
    }

    public void setPreLoginHandler(PreLoginHandler preLoginHandler) {
        this.preLoginHandler = preLoginHandler;
    }

    public List<ClientSystem> getClientSystems() {
        return clientSystems;
    }

    public void setClientSystems(List<ClientSystem> clientSystems) {
        this.clientSystems = clientSystems;
    }

    /**
     * 获取指定用户的可以业务系统列表.
     *
     * @param loginUser
     * @return
     */
    public List<ClientSystem> getClientSystems(LoginUser loginUser) {
        return null;
    }

    /**
     * 重新加载配置，以支持热部署.
     */
    public void refreshConfig() throws Exception {
        // 加载config.properties
        Properties configProperties = new Properties();
        try {
            Resource resource = resourceLoader.getResource("classpath:config.properties");
            configProperties.load(resource.getInputStream());
        } catch (IOException e) {
            logger.warn("在classpath下未找到配置文件config.properties");
        }

        // vt有效期参数
        String configTokenTimeout = (String) configProperties.get("tokenTimeout");
        if (!StringUtil.isEmpty(configTokenTimeout)) {
            try {
                tokenTimeout = Integer.parseInt(configTokenTimeout);
                logger.debug("config.properties设置tokenTimeout={}", tokenTimeout);
            } catch (NumberFormatException e) {
                logger.warn("tokenTimeout参数配置不正确");
            }
        }

        try {
            // 加载客户端系统配置列表
            loadClientSystems();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("加载client system配置失败");
        }

    }

    /**
     * 加载客户端系统配置列表.
     *
     * @throws Exception
     */
    private void loadClientSystems() throws Exception {
        clientSystems.clear();
        Resource resource = resourceLoader.getResource("classpath:client-systems.xml");
        SAXReader reader = new SAXReader();
        Document document = reader.read(resource.getInputStream());
        Element rootElement = document.getRootElement();
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            ClientSystem clientSystem = new ClientSystem();

            clientSystem.setId(element.attributeValue("id"));
            clientSystem.setName(element.attributeValue("name"));
            clientSystem.setBaseURL(element.elementText("baseURL"));
            clientSystem.setHomeURI(element.elementText("homeURI"));
            clientSystem.setInnerAddress(element.elementText("innerAddress"));

            clientSystems.add(clientSystem);
        }
    }


    /**
     * 应用停止时执行，做清理性工作，如通知客户端logout.
     */
    public void destroy() {
        for (ClientSystem clientSystem : clientSystems) {
            clientSystem.noticeShutdown();
        }
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
