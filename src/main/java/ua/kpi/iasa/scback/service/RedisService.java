package ua.kpi.iasa.scback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Service
public final class RedisService {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public String set(String key, String value){
        redisTemplate
                .getConnectionFactory()
                .getConnection()
                .set(key.getBytes(), value.getBytes());
        return key;
    }

    public String set(String key, String value, long ttl){
        redisTemplate.getConnectionFactory().getConnection().setEx(key.getBytes(), ttl, value.getBytes());
        return null;
    }

    public String get(String key){
        return new String(
                redisTemplate.getConnectionFactory().getConnection().get(key.getBytes()),
                StandardCharsets.UTF_8
        );
    }
    public String getAndDelete(String key){
        String response = get(key);
        redisTemplate.getConnectionFactory().getConnection().del(key.getBytes());
        return response;
    }
}
