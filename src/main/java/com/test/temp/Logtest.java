package com.test.temp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hong.Wu
 * @date: 23:10 2019/11/30
 */
public class Logtest {

    public static void main(String[] args) {

        // Java 自带的日志工具类 java.util.logging.Logger
        // commons-logging log4j 是一个具体的日志框架的实现，而 commons-logging 就是日志的门面接口
        // log4j 在 2015/08/05 这一天被 Apache 宣布停止维护了，用户需要切换到 Log4j2上面

        // Slf4j Simple Logging Facade for Java，即简单日志门面接口 和 Apache 的 commons-logging 是一样的概念，它们都不是具体的日志框架
        // Logback 是 Slf4j 的原生实现框架，同样也是出自 Log4j 一个人之手，但拥有比 log4j 更多的优点、特性和更做强的性能，现在基本都用来代替 log4j 成为主流。

    }
}

class LogTest {
//    https://my.oschina.net/u/3387320/blog/2874726
    private final static Logger logger = LoggerFactory.getLogger(LogTest.class);

    public static void main(String[] args) {
        logger.info("main====logback 成功了=====");
        // todo 业务逻辑
    }
}