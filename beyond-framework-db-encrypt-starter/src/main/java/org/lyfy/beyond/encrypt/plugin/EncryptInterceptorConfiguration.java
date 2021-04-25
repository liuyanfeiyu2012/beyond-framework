package org.lyfy.beyond.encrypt.plugin;

import org.lyfy.beyond.encrypt.plugin.interceptor.EncryptParameterInterceptor;
import org.lyfy.beyond.encrypt.plugin.interceptor.EncryptResultSetInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@ConditionalOnProperty(name = "funmall.base.db.encrypt.enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class EncryptInterceptorConfiguration {


    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void addPageInterceptor() {
        EncryptParameterInterceptor interceptor1 = new EncryptParameterInterceptor(applicationContext);
        EncryptResultSetInterceptor interceptor2 = new EncryptResultSetInterceptor(applicationContext);
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor1);
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor2);
        }
    }
}
