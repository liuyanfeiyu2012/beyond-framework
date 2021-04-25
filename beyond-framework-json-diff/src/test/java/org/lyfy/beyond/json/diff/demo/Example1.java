package org.lyfy.beyond.json.diff.demo;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @Auther: 谢星星
 * @Date: 2019/7/15 11:05
 * @Description:
 */
public class Example1 {

    public static void main(String[] args) {

        Gson gson = new Gson();

        // Serialization
        gson.toJson(1);            // ==> 1
        gson.toJson("abcd");       // ==> "abcd"
        gson.toJson(new Long(10)); // ==> 10
        int[] values = {1};
        gson.toJson(values);


        // Deserialization
        int one = gson.fromJson("1", int.class);
        Integer one1 = gson.fromJson("1", Integer.class);
        Long one2 = gson.fromJson("1", Long.class);
        Boolean false1 = gson.fromJson("false", Boolean.class);
        String str = gson.fromJson("\"abc\"", String.class);
        String[] anotherStr = gson.fromJson("[\"abc\"]", String[].class);

        // Serialization
        BagOfPrimitives obj = new BagOfPrimitives();
        String json = gson.toJson(obj);
        BagOfPrimitives obj2 = gson.fromJson(json, BagOfPrimitives.class);


        int[] ints = {1, 2, 3, 4, 5};
        String[] strings = {"abc", "def", "ghi"};
        gson.toJson(ints);     // ==> [1,2,3,4,5]
        gson.toJson(strings);  // ==> ["abc", "def", "ghi"]
        int[] ints2 = gson.fromJson("[1,2,3,4,5]", int[].class);


        Collection<Integer> ints1 = Lists.newArrayList(1,2,3,4,5);
        String json1 = gson.toJson(ints);  // ==> json is [1,2,3,4,5]
        Type collectionType = new TypeToken<Collection<Integer>>(){}.getType();
        Collection<Integer> ints3 = gson.fromJson(json1, collectionType);
    }


    static class BagOfPrimitives {
        private int value1 = 1;
        private double value4 = 1.222;
        private String value2 = "abc";
        private transient int value3 = 3;

        BagOfPrimitives() {
        }
    }
}
