package org.lyfy.beyond.json.diff.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: 谢星星
 * @Date: 2019/7/15 11:38
 * @Description:
 */
public class JsonService {

    public Person getPerson() {
        Person person = new Person(123, "bobo", "HangZhou");
        return person;
    }

    public List<Person> getPersonList() {
        List<Person> list = new ArrayList<>();
        Person person1 = new Person(123, "sisi", "BeiJing");
        Person person2 = new Person(2002, "bobo", "HangZhou");
        list.add(person1);
        list.add(person2);

        return list;
    }

    public List<String> getList() {
        List<String> list = new ArrayList<>();
        list.add("BeiJing");
        list.add("HangZhou");
        return list;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 123);
        map.put("name", "sisi");
        map.put("place", "BeiJing");
        return map;
    }

    public List<Map<String, Object>> getlistMap() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 123);
        map1.put("name", "sisi");
        map1.put("place", "BeiJing");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 456);
        map2.put("name", "bobo");
        map2.put("place", "HangZhou");
        list.add(map1);
        list.add(map2);
        return list;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {
        Integer id;
        String name;
        String place;

        @Override
        public String toString() {
            return "Person [id=" + id + ", name=" + name + ", place=" + place + "]";
        }

    }
}
