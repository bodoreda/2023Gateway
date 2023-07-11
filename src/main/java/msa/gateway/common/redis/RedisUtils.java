package msa.gateway.common.redis;

import lombok.RequiredArgsConstructor;
import msa.gateway.model.UserInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * packageName : msa.gateway.common.redis
 * fileName : RedisUtils
 * author : BH
 * date : 2023-07-04
 * description :
 * ================================================
 * DATE                AUTHOR              NOTE
 * ================================================
 * 2023-07-04       RedisUtils       최초 생성
 */

@Component
@RequiredArgsConstructor
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setData(String key, String value, Long expiredTime){
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public void setUserInfo(String key, UserInfo userInfo, Long expiredTime) {
        redisTemplate.opsForValue().set(key, userInfo, expiredTime, TimeUnit.MILLISECONDS);
    }

    public String getData(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key){
        redisTemplate.delete(key);
    }
}
