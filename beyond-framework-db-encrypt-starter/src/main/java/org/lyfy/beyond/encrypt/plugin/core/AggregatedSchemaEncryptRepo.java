package org.lyfy.beyond.encrypt.plugin.core;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : 谢星星
 * @date : 2019/12/2 14:54
 * @description:
 */
@Data
public class AggregatedSchemaEncryptRepo implements InitializingBean {

    private List<DBSchemaEncryptRepo> dbSchemaEncryptRepos = new ArrayList<>();

    private Map<String, FieldWrapperService> fieldWrapperServiceMap = new ConcurrentHashMap<>(64);

    public AggregatedSchemaEncryptRepo() {
    }

    public AggregatedSchemaEncryptRepo(List<DBSchemaEncryptRepo> dbSchemaEncryptRepos) {
        this.dbSchemaEncryptRepos = dbSchemaEncryptRepos;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        dbSchemaEncryptRepos.forEach(repo -> {
            Set<String> encryptionFieldStringSet = repo.getEncryptionFieldStringSet();
            if (CollectionUtils.isNotEmpty(encryptionFieldStringSet)) {
                encryptionFieldStringSet.forEach(value -> {
                    if (fieldWrapperServiceMap.containsKey(value)) {
                        throw new RuntimeException("Same Field Can Not Be Encrypted By Different 'fieldWrapperServiceMap', "
                                + "filed: " + value);
                    } else {
                        fieldWrapperServiceMap.put(value, repo.getFieldWrapperService());
                    }
                });
            }
        });
    }
}
