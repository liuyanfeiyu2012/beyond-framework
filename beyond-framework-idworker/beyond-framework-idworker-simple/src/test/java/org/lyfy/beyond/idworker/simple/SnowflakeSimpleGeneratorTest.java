package org.lyfy.beyond.idworker.simple;

import org.lyfy.beyond.idworker.service.IdWorker;
import org.lyfy.beyond.idworker.simple.SnowflakeIdSimpleGenerator;
import org.lyfy.beyond.idworker.simple.UidParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @Auther: 谢星星
 * @Date: 2019/7/1 16:45
 */
public class SnowflakeSimpleGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeIdSimpleGenerator.class);

    public static void testProductIdByMoreThread(int dataCenterId, int workerId, int n) throws InterruptedException {
        List<Thread> tlist = new ArrayList<>();
        Set<Long> setAll = new HashSet<>();
        CountDownLatch cdLatch = new CountDownLatch(10);
        long start = System.currentTimeMillis();
        int threadNo = dataCenterId;
        Map<String, SnowflakeIdSimpleGenerator> idFactories = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            idFactories.put("snowflake" + i, new SnowflakeIdSimpleGenerator(workerId, threadNo++));
        }
        for (int i = 0; i < 10; i++) {
            Thread temp = new Thread(new Runnable() {
                @Override
                public void run() {
                    Set<Long> setId = new HashSet<>();
                    IdWorker idWorker = idFactories.get(Thread.currentThread().getName());
                    for (int j = 0; j < n; j++) {
                        setId.add(idWorker.nextId());
                    }
                    synchronized (setAll) {
                        setAll.addAll(setId);
                        LOGGER.info("{}生产了{}个id,并成功加入到setAll中.", Thread.currentThread().getName(), n);
                    }
                    cdLatch.countDown();
                }
            }, "snowflake" + i);
            tlist.add(temp);
        }
        for (int j = 0; j < 10; j++) {
            tlist.get(j).start();
        }
        cdLatch.await();

        long end1 = System.currentTimeMillis() - start;

        LOGGER.info("共耗时:{}毫秒,预期应该生产{}个id, 实际合并总计生成ID个数:{}", end1, 10 * n, setAll.size());
    }

    public static void testProductId(int dataCenterId, int workerId, int n) {
        IdWorker idWorker = new SnowflakeIdSimpleGenerator(workerId, dataCenterId);
        IdWorker idWorker2 = new SnowflakeIdSimpleGenerator(workerId + 1, dataCenterId);
        Set<Long> setOne = new HashSet<>();
        Set<Long> setTow = new HashSet<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            setOne.add(idWorker.nextId());//加入set
        }
        long end1 = System.currentTimeMillis() - start;
        LOGGER.info("第一批ID预计生成{}个,实际生成{}个<<<<*>>>>共耗时:{}", n, setOne.size(), end1);

        for (int i = 0; i < n; i++) {
            setTow.add(idWorker2.nextId());//加入set
        }
        long end2 = System.currentTimeMillis() - start;
        LOGGER.info("第二批ID预计生成{}个,实际生成{}个<<<<*>>>>共耗时:{}", n, setTow.size(), end2);

        setOne.addAll(setTow);
        LOGGER.info("合并总计生成ID个数:{}", setOne.size());

    }

    public static void testPerSecondProductIdNums() {
        IdWorker idWorker = new SnowflakeIdSimpleGenerator();
        long start = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; System.currentTimeMillis() - start < 1000; i++, count = i) {
            idWorker.nextId();
        }
        long end = System.currentTimeMillis() - start;
        System.out.println(end);
        System.out.println(count);
    }

    public static void main(String[] args) {
        IdWorker idWorker = new SnowflakeIdSimpleGenerator();
        Long id = idWorker.nextId();
        System.out.println(id);
        System.out.println(UidParseUtil.parseUID(id));
        System.out.println(idWorker.nextId());

        /** case1: 测试每秒生产id个数
         *   结论: 每秒生产id个数300w+
         */
        testPerSecondProductIdNums();

        /** case2: 单线程-测试多个生产者同时生产N个id,验证id是否有重复?
         *   结论: 验证通过,没有重复.
         */
        testProductId(1, 2, 20000);
        /** case3: 多线程-测试多个生产者同时生产N个id, 全部id在全局范围内是否会重复?
         *   结论: 验证通过,没有重复.
         */
        try {
            testProductIdByMoreThread(1, 2, 100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
