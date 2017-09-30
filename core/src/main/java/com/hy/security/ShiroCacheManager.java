package com.hy.security;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;
import java.util.Set;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-04-10 09:18)
 * @version: \$Rev: 1424 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-04-22 17:56:14 +0800 (周六, 22 4月 2017) $
 */
public class ShiroCacheManager implements CacheManager {
    private RedisCacheManager redisCacheManager;
    private String authorCacheName;

    public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public void setAuthorCacheName(String authorCacheName) {
        this.authorCacheName = authorCacheName;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        org.springframework.cache.Cache cache=redisCacheManager.getCache(name);
        if (cache == null) {
            cache = redisCacheManager.getCache(authorCacheName);
        }
        return new ShiroCache(cache);
    }

    static class ShiroCache<K,V> implements Cache<K,V> {
        private org.springframework.cache.Cache redisCache;

        public ShiroCache(org.springframework.cache.Cache redisCache) {
            this.redisCache = redisCache;
        }

        @Override
        public Object get(Object key) throws CacheException {
            if (redisCache == null) return null;
            org.springframework.cache.Cache.ValueWrapper obj=redisCache.get(key);
            return obj == null ? null : obj.get();
        }

        @Override
        public Object put(Object key, Object value) throws CacheException {
            Object previous = get(key);
            redisCache.put(key,value);
            return previous;
        }

        @Override
        public Object remove(Object key) throws CacheException {
            org.springframework.cache.Cache.ValueWrapper value = redisCache.get(key);
            redisCache.evict(key);
            return value != null ? value.get() : null;
        }

        @Override
        public void clear() throws CacheException {
            redisCache.clear();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Set keys() {
            return null;
        }

        @Override
        public Collection values() {
            return null;
        }
    }
}
