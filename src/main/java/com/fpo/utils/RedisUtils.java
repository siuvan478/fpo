package com.fpo.utils;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 删除缓存<br>
     * 根据key精确匹配删除
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 取得缓存（int型）
     *
     * @param key
     * @return
     */
    public Integer getInt(String key) {
        String value = stringRedisTemplate.boundValueOps(key).get();
        if (StringUtils.isNotBlank(value)) {
            return Integer.valueOf(value);
        }
        return null;
    }

    /**
     * 将value对象写入缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        setex(key, value, null);
    }

    /**
     * 将value对象写入缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param time  失效时间(秒)
     */
    @SuppressWarnings("unchecked")
    public void setex(String key, Object value, Long time) {
        if (value == null) return;
        if (value.getClass().equals(String.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Integer.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Double.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Float.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Short.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Long.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value.getClass().equals(Boolean.class)) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else {
            if (time != null && time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
        }
    }

    /**
     * 获取缓存
     * 注：该方法暂不支持Character数据类型
     *
     * @param key   key
     * @param clazz 类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        return (T) redisTemplate.boundValueOps(key).get();
    }

    /**
     * 递增操作
     *
     * @param key
     * @param by
     * @return
     */
    @SuppressWarnings("unchecked")
    public double incr(String key, Long by) {
        return redisTemplate.opsForValue().increment(key, by);
    }

    /**
     * 获取缓存json对象
     *
     * @param key   key
     * @param clazz 类型
     * @return
     */
    public <T> T getJson(String key, Class<T> clazz) {
        return null;
    }
}