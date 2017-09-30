package com.hy.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.util.Assert;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-04-10 13:48)
 * @version: \$Rev: 1158 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-04-18 15:53:47 +0800 (周二, 18 4月 2017) $
 */
public class ShiroSessionDao extends AbstractSessionDAO {
    private static Logger logger = LogManager.getLogger(ShiroSessionDao.class);
    private JedisPool jedisPool;
    private String prefix;
    private RedisCacheManager redisCacheManager;
    private String cacheName;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null)
            throw new NullPointerException("session is empty");
        try {
            redisCacheManager.getCache(cacheName).put(prefix + session.getId().toString(), session);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("save session error");
        }
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            logger.error("session can not be null,delete failed");
            return;
        }
        Serializable id = session.getId();
        if (id == null) {
            throw new NullPointerException("session id is empty");
        }
        try {
            redisCacheManager.getCache(cacheName).evict(prefix + id.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("delete session error");
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        try {
            Jedis jedis = jedisPool.getResource();
            final Collection<Session> sessions = new ArrayList<>();
            jedis.keys("*" + prefix + "*").forEach(key -> {
                Cache.ValueWrapper valueWrapper = redisCacheManager.getCache(cacheName).get(key);
                if (valueWrapper != null)
                    sessions.add((Session) valueWrapper.get());
            });
            jedis.close();
            return sessions;
        } catch (Exception e) {
            logger.error("获取全部session异常");
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        update(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Assert.notNull(sessionId);
        Cache.ValueWrapper valueWrapper = redisCacheManager.getCache(cacheName).get(prefix + sessionId.toString());
        return valueWrapper == null ? null : (Session) valueWrapper.get();
    }
}
