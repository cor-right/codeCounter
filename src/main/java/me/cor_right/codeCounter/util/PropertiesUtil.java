package me.cor_right.codeCounter.util;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置信息的工具类
 */
public class PropertiesUtil {

    // default
    private static final String defaultPropertiesPath = "target/classes/path.properties";

    private static Properties properties;

    // logger
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    static {
        properties = new Properties();
        loadFile(defaultPropertiesPath);
    }

    public static void loadFile(String filepath) {
        try {
            properties.load(new FileInputStream(new File(filepath)));
        } catch (IOException e) {
            System.out.println("load file failed ");
        }
    }

    public static String getValue(String key) {
        return (String) properties.get(key);
    }

    public static void setValue(String key, String value) {
        properties.setProperty(key, value);
    }

}
