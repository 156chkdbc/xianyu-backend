package com.tcosfish.xianyu.cache;

import com.tcosfish.xianyu.model.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * @author tcosfish
 * @description 基于 redis缓存下的热度记录
 */
@Component
@RequiredArgsConstructor
public class HotProductCache {
  private final StringRedisTemplate rt;

  /** 给商品增加一次浏览量, 并返回最新值 */
  public Long incrView(Long productId) {
    String key = RedisKey.views(productId);
    Long views = rt.opsForValue().increment(key);
    // 热度分+1(score == views)
    rt.opsForZSet().incrementScore("item:views:", productId.toString(), 1);
    // 防止冷数据堆积, 设置过期时间, 24h后过期
    rt.expire(key, Duration.ofHours(24));
    return views;
  }

  /** 取 TopN 热度榜 */
  public List<Long> top(int n) {
    // z-set 有序列表
    Set<String> set = rt.opsForZSet().reverseRange("item:views:", 0, n - 1);
    if (set != null) {
      return set.stream().map(Long::valueOf).toList();
    } else {
      return List.of(); // 返回一个空的数组
    }
  }

  public Long getViewCount(Long productId) {
    String views = rt.opsForValue().get(RedisKey.views(productId));
    return views == null ? 0L : Long.parseLong(views);
  }

  /** 单个商品当前热度分 */
  public Double score(Long productId) {
    return rt.opsForZSet().score("views:products:", productId.toString());
  }
}
