package org.lyfy.beyond.es.test;

import org.lyfy.beyond.es.client.annotation.EsField;
import lombok.Data;

/**
 * @author: 谢星星
 * @date: 2019/12/18 14:29
 * @description:
 */
@Data
@EsField(parentPath = "addressList")
public class Address {

    private String desc;

    @EsField(parentPath = "addressList", path = "age")
    private String age;
}
