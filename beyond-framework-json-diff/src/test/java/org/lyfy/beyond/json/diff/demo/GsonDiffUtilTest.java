package org.lyfy.beyond.json.diff.demo;

import org.lyfy.beyond.json.diff.GsonDiffUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Auther: 谢星星
 * @Date: 2019/7/15 14:45
 * @Description:
 */
public class GsonDiffUtilTest {
    @Data
    @AllArgsConstructor
    public static class Clazz {
        private String clazzName;
        private List<Stu> stuList;
    }
    @Data
    @AllArgsConstructor
    public static class Stu {
        private Long id;
        private String name;
        private Integer age;
        private Double score;
        private Address address;
    }
    @Data
    @AllArgsConstructor
    public static class Address {
        private String district;
        private String street;
    }
    public static void main(String[] args) {
        Stu stu1 = new Stu(1L, "xxx", 29, 75D, new Address("徐汇区", "龙州路梅陇四村"));
        Stu stu2 = new Stu(1L, "xxx", 29, 75D, new Address("徐汇区", "龙州路梅陇四村"));
        Stu stu3 = new Stu(2L, "xxy", 29, 75D, null);
        Stu stu4 = new Stu(3L, "yyy", 30, 80D, new Address("浦东新区", "王港CBD"));

//        System.out.println(GsonDiffUtil.diff2String(stu1, stu2));
//        System.out.println(GsonDiffUtil.diff2String(stu1, stu3));
//
//        System.out.println(GsonDiffUtil.diff2Map(stu1, stu2));
//        System.out.println(GsonDiffUtil.diff2Map(stu1, stu3));
//
        Clazz clazzA = new Clazz("三年二班", Lists.newArrayList(stu1, stu3));
        Clazz clazzB = new Clazz("三年二班", Lists.newArrayList(stu2, stu4));
//
        System.out.println(GsonDiffUtil.diff2String(clazzA, clazzB));
//
//        System.out.println(MergeTopLevelKeyUtil.merge(GsonDiffUtil.diff2Map(clazzA, clazzB)));
//        System.out.println(MergeTopLevelKeyUtil.merge(GsonDiffUtil.diff2Map(stu1, stu3)));

//        String stu1Str = new Gson().toJson(stu1);
//        String stu3Str = new Gson().toJson(stu3);
//        System.out.println(stu1Str);
//        System.out.println(stu3Str);

//        System.out.println(GsonDiffUtil.diff2String(stu1Str, stu3Str));

    }


}
