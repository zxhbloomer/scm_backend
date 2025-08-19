package com.xinyirun.scm.common.utils.redis;

import com.xinyirun.scm.common.properies.SystemConfigProperies;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * reids工具类
 * @author
 * @date 2019年8月9日
 */
@Component
@Slf4j
public class RedisUtil {

    @Autowired
	private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SystemConfigProperies systemConfigProperies;

	/**
	 * 设置key-value失效时间，字符串类型key
	 * @param key
	 * @param seconds
	 * @return
	 */
	public boolean expire(String key, long seconds) {
		try {
        	if (seconds > 0) {
        		redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        	}
        	return true;
	    } catch (Exception e) {
            log.error("expire error:", e);
	    	return false;
	    }
	}

	/**
	    * 根据key 获取过期时间
	    * @param key 键 不能为null
	    * @return 时间(秒) 返回0代表为永久有效
	    */
   public long getExpire(String key) {
       return redisTemplate.getExpire(key, TimeUnit.SECONDS);
   }
	   
	/**
	 * 检查key是否存在缓存
	 * @param key
	 * @return
	 */
	public boolean checkKeyExisted(String key) {
		try {
	    	return redisTemplate.hasKey(key);
	    } catch (Exception e) {
            log.error("checkKeyExisted error:", e);
	     	return false;
	    }
	}
	
	/**
	 * 加1操作
	 * 
	 * @param key
	 * @return 返回操作后的值
	 */
	public long increase(String key) {
		return increase(key, 1);
	}
	
	/**
    * 删除缓存
    * @param key 可以传一个值 或多个
    */
   @SuppressWarnings("unchecked")
   public void delete(String... key) {
       if (key != null && key.length > 0) {
           if (key.length == 1) {
               redisTemplate.delete(key[0]);
           } else {
               Collection collection = Arrays.asList(key);
               redisTemplate.delete(collection);
           }
       }
   }
   
   /**
    * 根据模式匹配删除缓存键
    * @param pattern 模式，支持通配符 * 和 ?
    * @return 删除的键数量
    */
   public long deleteByPattern(String pattern) {
       try {
           if (pattern == null || pattern.trim().isEmpty()) {
               log.warn("deleteByPattern: pattern不能为空");
               return 0;
           }
           
           Set<String> keys = redisTemplate.keys(pattern);
           if (keys != null && !keys.isEmpty()) {
               Long deletedCount = redisTemplate.delete(keys);
               log.info("根据模式 '{}' 删除了 {} 个缓存键", pattern, deletedCount);
               return deletedCount != null ? deletedCount : 0;
           } else {
               log.info("根据模式 '{}' 未找到匹配的缓存键", pattern);
               return 0;
           }
       } catch (Exception e) {
           log.error("deleteByPattern error, pattern: {}", pattern, e);
           return 0;
       }
   }
   
   /**
    * 普通缓存获取
    * @param key 键
    * @return 值
    */
   public Object get(String key) {
       return key == null ? null : redisTemplate.opsForValue().get(key);
   }
   
   public String getString(String key) {
	   Object result = get(key);
	   if(result != null) {
		   return (String) result;
	   }
       return null;
   }
   
   /**
    * 普通缓存放入
    * @param key 键
    * @param value 值
    * @return true成功 false失败
    */
   public boolean set(String key, Object value) {
       try {
           redisTemplate.opsForValue().set(key, value);
           return true;
       } catch (Exception e) {
           log.error("set error:", e);
           return false;
       }
   }
   
   /**
    * 普通缓存放入并设置时间
    * @param key 键
    * @param value 值
    * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
    * @return true成功 false 失败
    */
   public boolean set(String key, Object value, long time) {
       try {
           if (time > 0) {
               redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
           } else {
               set(key, value);
           }
           return true;
       } catch (Exception e) {
           log.error("set error:", e);
           return false;
       }
   }

   /**
    * 递增
    * @param key 键
    * @param delta 要增加几(大于0)
    * @return
    */
   public long increase(String key, long delta) {
       if (delta < 0) {
           throw new RuntimeException("递增因子必须大于0");
       }
       return redisTemplate.opsForValue().increment(key, delta);
   }
	
   /**
    * 递减
    * @param key 键
    * @param delta 要减少几(小于0)
    * @return
    */
   public long decrease(String key, long delta) {
       if (delta < 0) {
           throw new RuntimeException("递减因子必须大于0");
       }
       return redisTemplate.opsForValue().increment(key, -delta);
   }
	
	/* ------------------------Map操作----------------------------bgn */
   
   /**
    * 获取某个map中的某一项的值
    * @param key 键 不能为null
    * @param item 项 不能为null
    * @return 值
    */
   public Object getFromMap(String key, String item) {
       return redisTemplate.opsForHash().get(key, item);
   }

   /**
    * 获取hashKey对应的所有键值
    * @param key 键
    * @return 对应的多个键值
    */
   public Map<Object, Object> hmget(String key) {
       return redisTemplate.opsForHash().entries(key);
   }

   /**
    * HashSet
    * @param key 键
    * @param map 对应多个键值
    * @return true 成功 false 失败
    */
   public boolean hmset(String key, Map<String, Object> map) {
       try {
           redisTemplate.opsForHash().putAll(key, map);
           return true;
       } catch (Exception e) {
           log.error("hmset error:", e);
           return false;
       }
   }

   /**
    * HashSet 并设置时间
    * @param key 键
    * @param map 对应多个键值
    * @param time 时间(秒)
    * @return true成功 false失败
    */
   public boolean hmset(String key, Map<String, Object> map, long time) {
       try {
           redisTemplate.opsForHash().putAll(key, map);
           if (time > 0) {
               expire(key, time);
           }
           return true;
       } catch (Exception e) {
           log.error("hmset error:", e);
           return false;
       }
   }

   /**
    * 向一张hash表中放入数据,如果不存在将创建
    * @param key 键
    * @param item 项
    * @param value 值
    * @return true 成功 false失败
    */
   public boolean putToMap(String key, String item, Object value) {
       try {
           redisTemplate.opsForHash().put(key, item, value);
           int redisCacheExpiredMin = systemConfigProperies.getRedisCacheExpiredMin();
           long redisCacheExpiredSecond = redisCacheExpiredMin * 60;
           expire(key, redisCacheExpiredSecond);
           return true;
       } catch (Exception e) {
           log.error("putToMap error:", e);
           return false;
       }
   }
   
   /**
    * 向一张hash表中放入数据,如果不存在将创建，并设置失效时间
    * @param key 键
    * @param item 项
    * @param value 值
    * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
    * @return true 成功 false失败
    */
   @Deprecated
   public boolean putToMap(String key, String item, Object value, long time) {
       try {
           redisTemplate.opsForHash().put(key, item, value);
           if (time > 0) {
               expire(key, time);
           }
           return true;
       } catch (Exception e) {
           log.error("putToMap error:", e);
           return false;
       }
   }
   
   /**
    * 删除hash表中的值
    * @param key 键 不能为null
    * @param item 项 可以使多个 不能为null
    */
   public void removeFromMap(String key, Object... item) {
       redisTemplate.opsForHash().delete(key, item);
   }
   
   /**
    * 判断hash表中是否有该项的值
    * @param key 键 不能为null
    * @param item 项 不能为null
    * @return true 存在 false不存在
    */
   public boolean hHasKey(String key, String item) {
       return redisTemplate.opsForHash().hasKey(key, item);
   }
   
   /**
    * hash递增 如果不存在,就会创建一个 并把新增后的值返回
    * @param key 键
    * @param item 项
    * @param by 要增加几(大于0)
    * @return
    */
   public double hincr(String key, String item, double by) {
       return redisTemplate.opsForHash().increment(key, item, by);
   }
   
   /**
    * hash递减
    * @param key 键
    * @param item 项
    * @param by 要减少记(小于0)
    * @return
    */
   public double hdecr(String key, String item, double by) {
       return redisTemplate.opsForHash().increment(key, item, -by);
   }
   
   /* ------------------------Map操作----------------------------endbgn */
   
   /* ------------------------Set操作----------------------------bgn */
   
   /**
    * 根据key获取Set中的所有值
    * @param key 键
    * @return
    */
   public Set<Object> sGet(String key) {
       try {
           return redisTemplate.opsForSet().members(key);
       } catch (Exception e) {
           log.error("sGet error:", e);
           return null;
       }
   }
   /**
    * 根据value从一个set中查询,是否存在
    * @param key 键
    * @param value 值
    * @return true 存在 false不存在
    */
   public boolean sHasKey(String key, Object value) {
       try {
           return redisTemplate.opsForSet().isMember(key, value);
       } catch (Exception e) {
           log.error("sHasKey error:", e);
           return false;
       }
   }
   /**
    * 将数据放入set缓存
    * @param key 键
    * @param values 值 可以是多个
    * @return 成功个数
    */
   public long sSet(String key, Object... values) {
       try {
           return redisTemplate.opsForSet().add(key, values);
       } catch (Exception e) {
           log.error("sSet error:", e);
           return 0;
       }
   }
   /**
    * 将set数据放入缓存
    * @param key 键
    * @param time 时间(秒)
    * @param values 值 可以是多个
    * @return 成功个数
    */
   public long sSetAndTime(String key, long time, Object... values) {
       try {
           Long count = redisTemplate.opsForSet().add(key, values);
           if (time > 0) {
               expire(key, time);
           }
           return count;
       } catch (Exception e) {
           log.error("sSetAndTime error:", e);
           return 0;
       }
   }
   /**
    * 获取set缓存的长度
    * @param key 键
    * @return
    */
   public long sGetSetSize(String key) {
       try {
           return redisTemplate.opsForSet().size(key);
       } catch (Exception e) {
           log.error("sGetSetSize error:", e);
           return 0;
       }
   }
   /**
    * 移除值为value的
    * @param key 键
    * @param values 值 可以是多个
    * @return 移除的个数
    */
   public long setRemove(String key, Object... values) {
       try {
           Long count = redisTemplate.opsForSet().remove(key, values);
           return count;
       } catch (Exception e) {
           log.error("setRemove error:", e);
           return 0;
       }
   }
   
   /* ------------------------Set操作----------------------------end */
   
   /* ------------------------ListSet操作----------------------------bgn */
   
   /**
    * 获取list缓存的内容
    * @param key 键
    * @param start 开始
    * @param end 结束 0 到 -1代表所有值
    * @return
    */
   public List<Object> lGet(String key, long start, long end) {
       try {
           return redisTemplate.opsForList().range(key, start, end);
       } catch (Exception e) {
           log.error("lGet error:", e);
           return null;
       }
   }
   /**
    * 获取list缓存的长度
    * @param key 键
    * @return
    */
   public long lGetListSize(String key) {
       try {
           return redisTemplate.opsForList().size(key);
       } catch (Exception e) {
           log.error("lGetListSize error:", e);
           return 0;
       }
   }
   /**
    * 通过索引 获取list中的值
    * @param key 键
    * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
    * @return
    */
   public Object lGetIndex(String key, long index) {
       try {
           return redisTemplate.opsForList().index(key, index);
       } catch (Exception e) {
           log.error("lGetIndex error:", e);
           return null;
       }
   }
   /**
    * 将list放入缓存
    * @param key 键
    * @param value 值
    * @return
    */
   public boolean lSet(String key, Object value) {
       try {
           redisTemplate.opsForList().rightPush(key, value);
           return true;
       } catch (Exception e) {
           log.error("lSet error:", e);
           return false;
       }
   }
   /**
    * 将list放入缓存
    * @param key 键
    * @param value 值
    * @param time 时间(秒)
    * @return
    */
   public boolean lSet(String key, Object value, long time) {
       try {
           redisTemplate.opsForList().rightPush(key, value);
           if (time > 0) {
               expire(key, time);
           }
           return true;
       } catch (Exception e) {
           log.error("lSet error:", e);
           return false;
       }
   }
   /**
    * 将list放入缓存
    * @param key 键
    * @param value 值
    * @return
    */
   public boolean lSet(String key, List<Object> value) {
       try {
           redisTemplate.opsForList().rightPushAll(key, value);
           return true;
       } catch (Exception e) {
           log.error("lSet error:", e);
           return false;
       }
   }
   /**
    * 将list放入缓存
    *
    * @param key 键
    * @param value 值
    * @param time 时间(秒)
    * @return
    */
   public boolean lSet(String key, List<Object> value, long time) {
       try {
           redisTemplate.opsForList().rightPushAll(key, value);
           if (time > 0) {
               expire(key, time);
           }
           return true;
       } catch (Exception e) {
           log.error("lSet error:", e);
           return false;
       }
   }
   /**
    * 根据索引修改list中的某条数据
    * @param key 键
    * @param index 索引
    * @param value 值
    * @return
    */
   public boolean lUpdateIndex(String key, long index, Object value) {
       try {
           redisTemplate.opsForList().set(key, index, value);
           return true;
       } catch (Exception e) {
           log.error("lUpdateIndex error:", e);
           return false;
       }
   }
   /**
    * 移除N个值为value
    * @param key 键
    * @param count 移除多少个
    * @param value 值
    * @return 移除的个数
    */
   public long lRemove(String key, long count, Object value) {
       try {
           Long remove = redisTemplate.opsForList().remove(key, count, value);
           return remove;
       } catch (Exception e) {
           log.error("lRemove error:", e);
           return 0;
       }
   }
	
   /* ------------------------ListSet操作----------------------------end */
   
   
	/**
	 * 获取分布式自旋锁
	 * @author xuhongyu
	 * @param lockKey 锁
	 * @param requestId 建锁请求标识(解铃还须系铃人)
	 * @return 是否获取成功
	 * boolean

	public boolean getDistributedSpinLock(String lockKey, String requestId) {
		boolean status = false;
		int spinCnt = (EXPIRE_TIME+SPIN_SLEEP_TIME)/SPIN_SLEEP_TIME;//自旋次数
		for(int i=0;i<spinCnt;i++){
			if (getDistributedMutexLock(lockKey, requestId)) {
				status = true;
				break;
			}else{
				try {
					Thread.sleep(SPIN_SLEEP_TIME);
				} catch (Exception e) {
					continue;
				}
			}
		}
        return status;
	}
     */
	/**
	 * 获取分布式互斥锁<br>
	 * 这是一种原始的方法，参考：https://blog.csdn.net/u013219624/article/details/83314484<br>
	 * @author xuhongyu
	 * @param lockKey 锁
	 * @param requestId 建锁请求标识(解铃还须系铃人)
	 * @return 是否获取成功
	 * boolean

	public boolean getDistributedMutexLock(String lockKey, String requestId) {
		String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, EXPIRE_TIME);
		if (result != null && LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}
     */
	/**
	 * 释放分布式锁:
	 * 获取锁对应的value值，检查是否与requestId相等，如果相等则删除锁（解锁）
	 * @author xuhongyu
	 * @param lockKey 锁
	 * @param requestId 请求标识(解铃还须系铃人)
	 * @return 是否释放成功
	 * boolean

	public boolean releaseDistributedLock(String lockKey, String requestId) {
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
		if (result != null && RELEASE_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}
     */
}