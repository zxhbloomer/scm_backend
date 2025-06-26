package com.xinyirun.scm.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * https://blog.csdn.net/ywyngq/article/details/122803019 《java计时工具StopWatch的使用》  $海阔天空$ 于 2022-02-06 23:23:38 发布 472
 * 作为一名程序猿，有时为了查看代码执行效率，以下代码肯定不少写：
 * long start = System.currentTimeMillis();
 * // do something…
 * long end = System.currentTimeMillis();
 * System.out.println(start-end);
 * 上面这段代码，只要是个java程序猿肯定都写过，问题是写个一两次还可以忍受，但是要针对复杂逻辑代码进行性能分析的时候，此时需要些大量的计时代码，你就无法忍受了。各位猿不要急，spring给我们提供一个工具类StopWatch，这货就是专门干计时的，但在使用的时候最好还是封装下，话不多说上代码：
 * ————————————————
 * 版权声明：本文为CSDN博主「$海阔天空$」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/ywyngq/article/details/122803019
 */
@Slf4j
public class StopWatchUtil {

    private static final ThreadLocal<String> localId = new ThreadLocal<>();

    /**
     * 一个Conrtoller类有一个LinkedHashMap<String, StopWatch>,LinkedHashMap<String, StopWatch>里面存放方法和方法的StopWatch
     */
    private static final ThreadLocal<Map<String, Map<String, StopWatch>>> localWatchs = new ThreadLocal<>();


    public static void stopAndStartNew(String controllerName, String methodName, String stopWatchTaskName) {
        stop(controllerName, methodName);
        start(controllerName, methodName, stopWatchTaskName);
    }

    public static void start(String controllerName, String methodName, String stopWatchTaskName) {
        stopWatchTaskName = StrUtil.builder().append(getId()).append(":").append(stopWatchTaskName).toString();
        a(controllerName, methodName).start(stopWatchTaskName);
    }

    public static void stop(String controllerName, String methodName) {
        StopWatch stopWatch;
        if ((stopWatch = a(controllerName, methodName)).isRunning()) {
            stopWatch.stop();
        }
    }

    private static Map<String, Map<String, StopWatch>> a() {
        Map<String, Map<String, StopWatch>> map;
        if (localWatchs.get() == null) {
            map = new ConcurrentHashMap<>();
            localWatchs.set(map);
        }
        return localWatchs.get();
    }

    private static Map<String, StopWatch> a(String controllerName) {
        LinkedHashMap<String, StopWatch> linkedHashMap;
        Map<String, Map<String, StopWatch>> map;
        if ((map = a()).containsKey(controllerName)) {
            return map.get(controllerName);
        }

        linkedHashMap = new LinkedHashMap<>();
        map.put(controllerName ,linkedHashMap);
        return linkedHashMap;
    }


    private static StopWatch a(String controllerName, String methodName) {
        StopWatch stopWatch;
        Map<String, StopWatch> map;
        if ((map = a(controllerName)).containsKey(methodName)) {
            return map.get(methodName);
        }

        stopWatch = new StopWatch(getId());
        map.put(methodName, stopWatch);
        return stopWatch;
    }

    public static void print(String controllerName) {
        Iterator<Map.Entry<String, StopWatch>> iterator = a(controllerName).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, StopWatch> next = iterator.next();
            print(controllerName, next.getKey(), false);
        }
        a().remove(controllerName);
    }

    public static void print(String controllerName, String methodName) {
        print(controllerName, methodName, true);
    }

    public static void print(String controllerName, String methodName, boolean bool) {
        StopWatch stopWatch;
        if ((stopWatch = a(controllerName, methodName)).isRunning()) {
            stopWatch.stop();
        }

        if (log.isTraceEnabled()) {
            log.trace("##### watch key: {}, step:{}, pretty:{}", controllerName, methodName, stopWatch.getTotalTimeSeconds());
        }

        log.info("##### watch key: {}, step:{}, pretty:{}", controllerName, methodName, stopWatch.getTotalTimeSeconds());


        if (bool) {
            a(controllerName).remove(methodName);
        }
    }


    public static void setId(String id) {
        localId.set(id);
    }

    public static String getId() {
        return localId.get();
    }

    public static void clearId() {
        localId.remove();
    }

    public static void clearWatch() {
        localWatchs.remove();
    }

    public static void testStopWatchUtil() throws InterruptedException {
        StopWatchUtil.stopAndStartNew("StopWatchUtil", "testStopWatchUtil", "测试工具类的用法");
        Thread.sleep(1000);
        StopWatchUtil.print("StopWatchUtil", "testStopWatchUtil");
    }

    public static void main(String[] args) throws InterruptedException {

        testStopWatchUtil();

        // 这里将StopWatch简单封装了下，作为一个工具使用，具体使用方式如下：
        StopWatchUtil.stopAndStartNew("XXXController类", "XXX方法名", "XXX标记处1");

        // 打印计时log
        StopWatchUtil.print("XXXController类", "XXX方法名");
    }
}