package org.lyfy.beyond.banner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * @author: 谢星星
 * @Date: 2019/7/16 19:51
 * @Description:
 */
@Order(value = 1)
public class BeyondFrameworkApplicationRunListener implements SpringApplicationRunListener {

    public BeyondFrameworkApplicationRunListener(SpringApplication application, String[] args) {
    }

    @Override
    public void starting() {

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        Properties props = new Properties();
        System.setProperty("banner.location", "classpath:META-INF/banner.txt");
        environment.getPropertySources().addFirst(new PropertiesPropertySource("bannerConfig", props));
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }
}
