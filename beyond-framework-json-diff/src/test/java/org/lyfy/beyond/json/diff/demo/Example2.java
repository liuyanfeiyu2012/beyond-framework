package org.lyfy.beyond.json.diff.demo;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * @Auther: 谢星星
 * @Date: 2019/7/15 11:32
 * @Description:
 */
public class Example2 {

    public static void main(String[] args) {
        Gson gson = new Gson();
        JsonService service = new JsonService();

        String str1 = gson.toJson(service.getPerson());
        System.out.println("1: " + str1);
//        1: {"id":123,"name":"bobo","place":"HangZhou"}

        String str4 = gson.toJson(service.getMap());
        System.out.println("2: " + str4);
//        2: {"id":123,"name":"sisi","place":"BeiJing"}

        String str2 = gson.toJson(service.getPersonList());
        System.out.println("3: " + str2);
//        3: [{"id":123,"name":"sisi","place":"BeiJing"},{"id":2002,"name":"bobo","place":"HangZhou"}]

        String str3 = gson.toJson(service.getList());
        System.out.println("4: " + str3);
//        4: ["BeiJing","HangZhou"]

        String str5 = gson.toJson(service.getlistMap());
        System.out.println("5: " + str5);
//        5: [{"id":123,"name":"sisi","place":"BeiJing"},{"id":456,"name":"bobo","place":"HangZhou"}]


        JsonService.Person personA = gson.fromJson(str4, JsonService.Person.class);
        System.out.println(personA);
//        Person [id=123, name=sisi, place=BeiJing]

        Map<String, Object> mapA = gson.fromJson(str4, Map.class);
        System.out.println(mapA);
//        {id=123.0, name=sisi, place=BeiJing}

        //在已经知道要转成List的情况下可以直接带List.class参数
        List<Map> list = gson.fromJson(str5 /*str2*/, List.class);
        for (Map map : list) {
            String s = gson.toJson(map);
            JsonService.Person p = gson.fromJson(s, JsonService.Person.class);
            System.out.println(p);
        }
//        Person [id=123, name=sisi, place=BeiJing]
//        Person [id=456, name=bobo, place=HangZhou]

        //转泛型的时候会把int转成double
        List<Map<String, Object>> list_map = gson.fromJson(str5, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        for (Map m : list_map) {
            System.out.println(m);
        }
//        {id=123.0, name=sisi, place=BeiJing}
//        {id=456.0, name=bobo, place=HangZhou}

        //转Person可以把double的小数舍去
        List<JsonService.Person> personList = gson.fromJson(str2 /*str5*/, new TypeToken<List<JsonService.Person>>() {
        }.getType());
        for (JsonService.Person p : personList) {
            System.out.println(p);
        }
//        Person [id=123, name=sisi, place=BeiJing]
//        Person [id=2002, name=bobo, place=HangZhou]
    }


}
