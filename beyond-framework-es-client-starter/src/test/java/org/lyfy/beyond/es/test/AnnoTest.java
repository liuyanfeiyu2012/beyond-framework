package org.lyfy.beyond.es.test;

import org.lyfy.beyond.es.client.util.Anno;

/**
 * @author: 谢星星
 * @date: 2019/12/18 14:29
 * @description:
 */
public class AnnoTest {

    public static void main(String[] args) {

        System.out.println(Anno.of(Person::getName));
        System.out.println(Anno.of(Person::getAddressList));

        System.out.println(Anno.of(Address::getDesc));
        System.out.println(Anno.of(Address::getAge));

    }
}
