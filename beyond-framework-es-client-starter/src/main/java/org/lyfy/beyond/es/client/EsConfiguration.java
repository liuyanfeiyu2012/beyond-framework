package org.lyfy.beyond.es.client;

import org.lyfy.beyond.es.client.core.EsClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: 谢星星
 * @date: 2019/12/18 10:27
 * @description:
 */
@Configuration
@ConditionalOnBean(RestHighLevelClient.class)
@ConditionalOnProperty(name = "funmall.base.es.client", havingValue = "true", matchIfMissing = true)
public class EsConfiguration {

    @Configuration
    protected static class ESClientConfiguration {

        @Bean
        @ConditionalOnMissingBean(EsClient.class)
        public EsClient esClient(RestHighLevelClient restHighLevelClient) {
            return new EsClient(restHighLevelClient);
        }
    }
}
