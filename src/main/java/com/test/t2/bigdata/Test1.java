package com.test.t2.bigdata;
/**
 * 
 * http://blog.csdn.net/wyp584168/article/details/40826375
 * 
 * 
 * 如果有一张大表，表中的数据有几百万、几千万甚至上亿，要实现实时查询，查询的结果要在十秒钟之内出来，怎么办？如何做优化?
 * 本人现在做的项目中，有个表的数据超过1千万行，超过3G的数据。现在需要对表中的数据进行查询统计，之前由于没做优化，导致此表的查询效率非常低下，让使用者非常苦恼，于是本人参与了此表的优化。
 * 举个类似的例子，比如表中的结构如下，现在要统计某一天出生的人口数，或者统计某一城市的人口数，或者某一城市某一天出生的人口数。
#查询某一城市的人口数
SELECT COUNT(*) FROM population WHERE city='广州';
#查询某一天出生的人口数
SELECT COUNT(*) FROM population WHERE birthday = '2014-11-02';
#查询某一城市某一天出生的人口数
SELECT COUNT(*) FROM population WHERE city='广州' AND birthday = '2014-11-02';

 *
 */
public class Test1 {

}
