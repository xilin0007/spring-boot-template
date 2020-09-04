package com.fxl.sbtemplate.util.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Slf4j
public class RedisManager {

    /**
     * @Fields MAX_ACTIVE :
     *         如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted
     *         (耗尽)
     */
    private static int MAX_ACTIVE = 10000;

    /**
     * @Fields MAX_IDLE : 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8
     */
    private static int MAX_IDLE = 1000;

    /**
     * @Fields MAX_WAIT :
     *         等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
     */
    private static int MAX_WAIT = 10000;

    /**
     * @Fields TIME_OUT : JEDIS 超时时间
     */
    private static int TIME_OUT = 5000;

    /**
     * @Fields JEDIS_INDEX : redis默认库
     */
    private static int JEDIS_INDEX = 0;

    /**
     * sentinel模式的连接池
     */
    private static JedisSentinelPool jedisSentinelPool = null;


    /**
     * 初始化sentinel连接池
     */
    public static void initSentinelPool(Map<String, Object> ymlConfig){
        // TODO: 2020/8/4  应更换一个新的redis库
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);

        String masterNameValue = ymlConfig.get("redis.masterNameValue").toString();
        String passwordValue = ymlConfig.get("redis.passwordValue").toString();
        String address = ymlConfig.get("redis.address").toString();
        int dbIndexValue = Integer.valueOf(ymlConfig.get("redis.dbIndexValue").toString());
        String[] addressList = address.split(",");
        Set<String> sentinels = new HashSet<String>();
        for (int i = 0;i<addressList.length;i++){
            sentinels.add(addressList[i]);
        }
        jedisSentinelPool = new JedisSentinelPool(masterNameValue,sentinels,config,TIME_OUT,passwordValue,dbIndexValue);
        log.info("初始化sentinel模式redis成功，当前master为：{}",jedisSentinelPool.getCurrentHostMaster());
    }

    /**
     * @Title getJedis
     * @Description 获取Jedis实例
     * @return
     * @return Jedis
     * @author WarnerZhang
     * @date 2015年8月3日
     */
    public static Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = jedisSentinelPool.getResource();
        } catch (Exception e) {
            log.error("获取sentinel模式jedis异常{}", e.getMessage(), e);
        }
        return jedis;
    }

    /**
     * @Title returnResource
     * @Description 释放jedis资源
     * @param jedis
     * @return void
     * @author WarnerZhang
     * @date 2015年8月3日
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            try {
                jedisSentinelPool.returnResource(jedis);
            } catch (Exception e) {
                log.error("回收redis资源出错", e);
            }
        }
    }

    public static void returnBrokenResource(final Jedis jedis) {
        if (jedis != null) {
            try {
                jedisSentinelPool.returnBrokenResource(jedis);
            } catch (Exception e) {
                log.error("回收redis资源出错", e);
            }
        }
    }

    /**
     * 获取数据
     * @param key
     * @return
     */
    public static String get(String key) {
        String value = null;

        JedisPool pool = null;
        Jedis jedis = null;
        try {
            jedis=getJedis();
            value = jedis.get(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }

        return value;
    }

    public static Object get(byte[] key) {
        Object value = null;

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            byte[] bytes = jedis.get(key);
            value = SerializeUtil.unserialize(bytes);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource(jedis);
        }

        return value;
    }

    public static String set(String key, String value) {
        String result = null;

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource(jedis);
        }

        return result;
    }

    public static String set(String key, byte[] value) {
        String result = null;

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            result = jedis.set(key.getBytes(), value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }

        return result;
    }

    public static String set(String key, String value, String nxxx,
                             String expx, long time) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.set(key, value, nxxx, expx, time);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String echo(String string) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.echo(string);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Boolean exists(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.exists(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    /**
     * 对Lua脚本进行求值
     * @param script Lua脚本
     * @param keys key1 key2 key3
     * @param argv argv1 argv2 argv3
     * @return
     */
    public static String eval(String script, List keys, List argv) {
        String value = null;

        JedisPool pool = null;
        Jedis jedis = null;
        try {
            jedis=getJedis();
            value = String.valueOf(jedis.eval(script, keys, argv));
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }

        return value;
    }

    public static String type(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.type(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long expire(String key, int seconds) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.expire(key, seconds);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long expireAt(String key, long unixTime) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.expireAt(key, unixTime);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long ttl(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.ttl(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long pttl(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.pttl(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Boolean setbit(String key, long offset, boolean value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.setbit(key, offset, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Boolean setbit(String key, long offset, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.setbit(key, offset, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Boolean getbit(String key, long offset) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.getbit(key, offset);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long setrange(String key, long offset, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.setrange(key, offset, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String getrange(String key, long startOffset, long endOffset) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.getrange(key, startOffset, endOffset);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String getSet(String key, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.getSet(key, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long setnx(String key, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.setnx(key, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    /**
     *<p>入参类型: [key, seconds 单位/s, value]</p>
     *<p>出参类型: java.lang.String</p>
     *<p>创建时间: 7:11 PM 2019/5/21</p>
     *<p>方法作者: huaxu</p>
     *<p>异   常:</p>
     */
    public static String setex(String key, int seconds, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.setex(key, seconds, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> blpop(String arg) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.blpop(arg);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> blpop(int timeout, String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.blpop(timeout, key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> brpop(String arg) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.brpop(arg);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> brpop(int timeout, String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long decrBy(String key, long integer) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.decrBy(key, integer);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long decr(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.decr(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long incrBy(String key, long integer) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.incrBy(key, integer);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Double incrByFloat(String key, double integer) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.incrByFloat(key, integer);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long incr(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.incr(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long append(String key, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.append(key, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String substr(String key, int start, int end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.substr(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long hset(String key, String field, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String hget(String key, String field) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hget(key, field);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long hsetnx(String key, String field, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hsetnx(key, field, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String hmset(String key, Map<String, String> hash) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hmset(key, hash);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> hmget(String key, String... fields) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hmget(key, fields);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long hincrBy(String key, String field, long value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Double hincrByFloat(String key, String field, double value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hincrByFloat(key, field, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Boolean hexists(String key, String field) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hexists(key, field);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long del(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.del(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long del(final String... keys) {

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.del(keys);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long hdel(String key, String... fields) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hdel(key, fields);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long hlen(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hlen(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> hkeys(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hkeys(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> hvals(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hvals(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Map<String, String> hgetAll(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hgetAll(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long rpush(String key, String... strings) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.rpush(key, strings);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long lpush(String key, String... strings) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.lpush(key, strings);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long lpushx(String key, String... string) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.lpushx(key, string);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long strlen(final String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.strlen(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long move(String key, int dbIndex) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.move(key, dbIndex);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long rpushx(String key, String... string) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.rpushx(key, string);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long persist(final String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.persist(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long llen(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.llen(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> lrange(String key, long start, long end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String ltrim(String key, long start, long end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.ltrim(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String lindex(String key, long index) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.lindex(key, index);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String lset(String key, long index, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.lset(key, index, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long lrem(String key, long count, String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.lrem(key, count, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static synchronized String lpop(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.lpop(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String rpop(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.rpop(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long sadd(String key, String... members) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.sadd(key, members);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> smembers(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.smembers(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long srem(String key, String... members) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.srem(key, members);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String spop(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.spop(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long scard(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.scard(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Boolean sismember(String key, String member) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.sismember(key, member);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String srandmember(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.srandmember(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> srandmember(String key, int count) {

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.srandmember(key, count);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zadd(String key, double score, String member) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zadd(String key, Map<String, Double> scoreMembers) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zadd(key, scoreMembers);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrange(String key, long start, long end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zrem(String key, String... members) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrem(key, members);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Double zincrby(String key, double score, String member) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zincrby(key, score, member);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zrank(String key, String member) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrank(key, member);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zrevrank(String key, String member) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrank(key, member);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrevrange(String key, long start, long end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrangeWithScores(String key, long start, long end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrangeWithScores(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrevrangeWithScores(String key, long start,
                                                 long end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zcard(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zcard(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Double zscore(String key, String member) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> sort(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.sort(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> sort(String key, SortingParams sortingParameters) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.sort(key, sortingParameters);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zcount(String key, double min, double max) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zcount(String key, String min, String max) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrangeByScore(String key, double min, double max) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrevrangeByScore(String key, double max,
                                               double min) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrangeByScore(String key, double min, double max,
                                            int offset, int count) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrevrangeByScore(String key, double max,
                                               double min, int offset, int count) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                                     double max) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
                                                        double min) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                                     double max, int offset, int count) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
                                                        double min, int offset, int count) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrangeByScore(String key, String min, String max) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrevrangeByScore(String key, String max,
                                               String min) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrangeByScore(String key, String min, String max,
                                            int offset, int count) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> zrevrangeByScore(String key, String max,
                                               String min, int offset, int count) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrangeByScoreWithScores(String key, String min,
                                                     String max) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrevrangeByScoreWithScores(String key, String max,
                                                        String min) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrangeByScoreWithScores(String key, String min,
                                                     String max, int offset, int count) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<Tuple> zrevrangeByScoreWithScores(String key, String max,
                                                        String min, int offset, int count) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zremrangeByRank(String key, long start, long end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zremrangeByScore(String key, double start, double end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zremrangeByScore(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long zremrangeByScore(String key, String start, String end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zremrangeByScore(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long linsert(String key, LIST_POSITION where, String pivot,
                               String value) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.linsert(key, where, pivot, value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long bitcount(final String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.bitcount(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long bitcount(final String key, long start, long end) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.bitcount(key, start, end);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static ScanResult<Entry<String, String>> hscan(String key,
                                                          final String cursor) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.hscan(key, cursor);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static ScanResult<String> sscan(String key, final String cursor) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.sscan(key, cursor);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static ScanResult<Tuple> zscan(String key, final String cursor) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.zscan(key, cursor);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long pfadd(String key, String... elements) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.pfadd(key, elements);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Long pfcount(String key) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.pfcount(key);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String flushDB() {

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.flushDB();
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> keys(final String pattern) {

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.keys(pattern);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static List<String> mget(final String... keys) {

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.mget(keys);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> sinter(final String... keys) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.sinter(keys);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> sunion(final String... keys) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.sinter(keys);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static Set<String> sdiff(final String... keys) {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.sdiff(keys);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String mset(final String... keysvalues) {

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.mset(keysvalues);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String setObject(String key,Object object)
    {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.set(key.getBytes(), SerializeUtil.serialize(object));
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }

    public static String setObjectEx(String key,Object object,int seconds)
    {
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            return jedis.setex(key.getBytes(),seconds,SerializeUtil.serialize(object));
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }
    public static Object getObject(String key){
        JedisPool pool = null;
        Jedis jedis = null;
        try {

            jedis = getJedis();
            byte[] value = jedis.get(key.getBytes());
            return SerializeUtil.unserialize(value);
        } catch (Exception e) {
            // 释放redis对象
            returnBrokenResource(jedis);
            log.error("操作redis出错", e);
        } finally {
            // 返还到连接池
            returnResource( jedis);
        }
        return null;
    }
}
