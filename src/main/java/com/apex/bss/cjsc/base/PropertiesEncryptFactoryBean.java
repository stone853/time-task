package com.apex.bss.cjsc.base;

import org.springframework.beans.factory.FactoryBean;

import java.util.Properties;

/**
 * Created by CJY on 2017/5/12.
 */
public class PropertiesEncryptFactoryBean implements FactoryBean {

    private Properties properties;

    @Override
    public Object getObject() throws Exception {
        return getProperties();
    }

    @Override
    public Class getObjectType() {
        return Properties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Properties getProperties() {
        return properties;
    }

    //数据库账号密码明文
    public void setProperties(Properties inProperties) {
        this.properties = inProperties;
        String encodeUsername = properties.getProperty("user");
        String encodePassword = properties.getProperty("password");
        String flag = properties.getProperty("flag");
        if ("0".equals(flag)) {
            if (encodeUsername != null) {
                properties.put("user", encodePassword);
            }
            if (encodePassword != null) {
                properties.put("password", encodePassword);
            }
        }
        if ("1".equals(flag)) {
            if (encodeUsername != null) {
                String decodeUsername = SecurityHelper.decode(encodeUsername, "");
                properties.put("user", decodeUsername);

            }
            if (encodePassword != null) {
                String decodePassword = SecurityHelper.decode(encodePassword, "");
                properties.put("password", decodePassword);

            }
        }

    }
}
