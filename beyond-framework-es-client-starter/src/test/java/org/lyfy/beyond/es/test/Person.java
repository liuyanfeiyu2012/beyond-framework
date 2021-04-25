package org.lyfy.beyond.es.test;

import org.lyfy.beyond.es.client.annotation.EsField;
import lombok.Data;

import java.util.List;

/**
 * @author: 谢星星
 * @date: 2019/12/18 13:59
 * @description:
 */
@Data
public class Person {

    @EsField(path = "name")
    private String name;

    private List<Address> addressList;
}
