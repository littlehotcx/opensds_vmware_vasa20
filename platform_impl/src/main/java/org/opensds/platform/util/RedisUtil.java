/*
 *
 *  * // Copyright 2019 The OpenSDS Authors.
 *  * //
 *  * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  * // not use this file except in compliance with the License. You may obtain
 *  * // a copy of the License at
 *  * //
 *  * //     http://www.apache.org/licenses/LICENSE-2.0
 *  * //
 *  * // Unless required by applicable law or agreed to in writing, software
 *  * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * // License for the specific language governing permissions and limitations
 *  * // under the License.
 *  *
 *
 */

package org.opensds.platform.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import org.opensds.platform.common.config.ConfigManager;
//import com.huawei.openas.commons.encryption.Encryption;
//import com.huawei.openas.commons.encryption.EncryptionFactory;

public class RedisUtil {
	private static Logger LOGGER = LogManager.getLogger(RedisUtil.class);

//	public static JedisSentinelPool jedisSentinelPool;
	public static JedisPool jedisSentinelPool;
	
	private static JedisPoolConfig config = new JedisPoolConfig();
	
	static {
//		config.setMaxTotal(Integer.valueOf(ConfigManager.getInstance().getValue("redis.pool.maxActive")));
//		config.setMaxIdle(Integer.valueOf(ConfigManager.getInstance().getValue("redis.pool.maxIdle")));
//		config.setMaxWaitMillis(Integer.valueOf(ConfigManager.getInstance().getValue("redis.pool.maxWait")));
//		config.setTestOnBorrow(Boolean.valueOf(ConfigManager.getInstance().getValue("redis.pool.testOnBorrow")));
//		config.setTestOnReturn(Boolean.valueOf(ConfigManager.getInstance().getValue("redis.pool.testOnReturn")));
//		
//		Set<String> sentinels = new HashSet<>();
//		sentinels.add("127.0.0.1:26379");
//		jedisSentinelPool = new JedisSentinelPool(ConfigManager.getInstance().getValue("redis.sentinel.master"), sentinels , config, decryptPwd2KeyStore(ConfigManager.getInstance().getValue("redis.password")));
		
		config.setMaxTotal(Integer.valueOf(ConfigManager.getInstance().getValue("redis.pool.maxActive")));
		config.setMaxIdle(Integer.valueOf(ConfigManager.getInstance().getValue("redis.pool.maxIdle")));
		config.setMaxWaitMillis(Integer.valueOf(ConfigManager.getInstance().getValue("redis.pool.maxWait")));
		config.setTestOnBorrow(Boolean.valueOf(ConfigManager.getInstance().getValue("redis.pool.testOnBorrow")));
		config.setTestOnReturn(Boolean.valueOf(ConfigManager.getInstance().getValue("redis.pool.testOnReturn")));
		
//		Set<String> sentinels = new HashSet<>();
//		sentinels.add("127.0.0.1:26379");
//		jedisSentinelPool = new JedisSentinelPool(ConfigManager.getInstance().getValue("redis.sentinel.master"), sentinels , config, decryptPwd2KeyStore(ConfigManager.getInstance().getValue("redis.password")));
		
		jedisSentinelPool = new JedisPool(config, ConfigManager.getInstance().getValue("redis.ip"),
				Integer.valueOf(ConfigManager.getInstance().getValue("redis.port")), Protocol.DEFAULT_TIMEOUT, decryptPwd2KeyStore(ConfigManager.getInstance().getValue("redis.password")));
	}
	
	private static String decryptPwd2KeyStore(String orgPwd) {

//		String key = ConfigManager.getInstance().getValue("openas.encryption.key");
//		Encryption encrytion = EncryptionFactory.getEncyption();
//		String encryptionKey = encrytion.encode(key, "").getEncryptedKey();
//		return encrytion.decode(encryptionKey, orgPwd);
        // TODO:Encryption
		return orgPwd;
	}
	
	public static void setIncrementValue2TTL(String key,int expire)
	{
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			jedis.incr(key);
			jedis.expire(key, expire);

			LOGGER.debug("setIncrementValue2TTL succ ");
		} catch (Exception e) {
			LOGGER.error("setIncrementValue2TTL error.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static void setStringKeyValue2TTL(String key, String value, int expire) {
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			String result = jedis.setex(key, expire, value);

			LOGGER.debug("setStringKeyValue2TTL return : " + result);
		} catch (Exception e) {
			LOGGER.error("setStringKeyValue2TTL error.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static void setExpireSeconds(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			Long result = jedis.expire(key, seconds);

			LOGGER.debug("setExpireSeconds result : " + result);
		} catch (Exception e) {
			LOGGER.error("setExpireSeconds error.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static List<String> getStringListStartsWithKey(String key) {
		List<String> list = new ArrayList<String>();
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			Set<String> keys = jedis.keys(key + "*");
			for (String k : keys) {
				list.add(jedis.get(k));
			}

			LOGGER.debug("getStringListStartWithKey result : " + list);
		} catch (Exception e) {
			LOGGER.error("getStringListStartWithKey error.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return list;
	}
	

	public static void incr(String key){
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			jedis.incr(key);

			LOGGER.debug("incr key=" + key);
		} catch (Exception e) {
			LOGGER.error("incr key error : ", e);
		}
	}
	
	public static long getExpireSeconds(String key)
	{
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			Long result = jedis.ttl(key);

			LOGGER.debug("getExpireSeconds return : " + result);

			return result.longValue();
		} catch (Exception e) {
			LOGGER.error("getExpireSeconds error.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return -1;
	}

	public static String getStringKeyValue(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			String result = jedis.get(key);

			LOGGER.debug("getStringKeyValue return : " + result);

			return result;
		} catch (Exception e) {
			LOGGER.error("getStringKeyValue error.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return null;
	}

	public static boolean checkExist(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			Boolean result = jedis.exists(key);

			LOGGER.debug("checkExist return : " + result);
			return result.booleanValue();
		} catch (Exception e) {
			LOGGER.error("checkExist error : ", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return false;
	}

	public static void clearByKey(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			long result = jedis.del(key);

			LOGGER.debug("clearByKey return : " + result);
		} catch (Exception e) {
			LOGGER.error("clearByKey error.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static void clearKeyAndValues(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisSentinelPool.getResource();
			Set<String> keys = jedis.keys(key + "*");

			LOGGER.debug("clearKeyAndValues match to delete keys :" + keys);

			long result = jedis.del(keys.toArray(new String[keys.size()]));

			LOGGER.debug("clearKeyAndValues del return : " + result);
		} catch (Exception e) {
			LOGGER.error("clearKeyAndValues error.", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static class RedisSet {

		public static void addValue(String key, String value) {
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				long result = jedis.sadd(key, value);

				LOGGER.debug("RedisSet/addValue return : " + result);
			} catch (Exception e) {
				LOGGER.error("RedisSet/addValue error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
		
		public static void delValue(String key,String value)
		{
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				long result = jedis.srem(key, value);

				LOGGER.debug("RedisSet/delValue return : " + result);
			} catch (Exception e) {
				LOGGER.error("RedisSet/delValue error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		public static void setValue(String key, Set<String> value) {
			Jedis jedis = null;
			try {

				jedis = jedisSentinelPool.getResource();

				if (jedis.exists(key)) {
					jedis.del(key);
				}

				if (value == null || value.isEmpty()) {
					return;
				}
				
				long result = jedis.sadd(key, value.toArray(new String[value.size()]));

				LOGGER.debug("RedisSet/setValue return : " + result);
			} catch (Exception e) {
				LOGGER.error("RedisSet/setValue error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		public static Set<String> getValue(String key) {
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				Set<String> vSet = jedis.smembers(key);
				return vSet;
			} catch (Exception e) {
				LOGGER.error("RedisSet/getValue error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}

			return new HashSet<String>();
		}
	}

	public static class RedisMap {

		public static void putValue(String key, String field, String jsonValue) {
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				long result = jedis.hset(key, field, jsonValue);

				LOGGER.debug("RedisMap/putValue return : " + result);
			} catch (Exception e) {
				LOGGER.error("RedisMap/putValue error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		public static String getValue(String key, String field) {
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				String vString = jedis.hget(key, field);

				return vString;
			} catch (Exception e) {
				LOGGER.error("RedisMap/getValue error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}

			return null;
		}

		public static Map<String, String> getAllMap(String key) {
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				Map<String, String> result = jedis.hgetAll(key);

				return result;
			} catch (Exception e) {
				LOGGER.error("RedisMap/getAllMap error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}

			return new HashMap<String, String>();
		}

		public static void removeKey(String key, String field) {
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				long result = jedis.hdel(key, field);

				LOGGER.debug("RedisMap/removeKey return : " + result);
			} catch (Exception e) {
				LOGGER.error("RedisMap/removeKey error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		public static long getSize(String key) {
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				long result = jedis.hlen(key);

				return result;
			} catch (Exception e) {
				LOGGER.error("RedisMap/getSize error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}

			return 0;
		}

	}

	public static class RedisList {

		public static void setValue(String key, List<String> listValues) {
			Jedis jedis = null;
			try {
				if (listValues.isEmpty()) {
					return;
				}

				jedis = jedisSentinelPool.getResource();
				long result = jedis.lpush(key, listValues.toArray(new String[listValues.size()]));

				LOGGER.debug("RedisList/setValue return : " + result);
			} catch (Exception e) {
				LOGGER.error("RedisList/setValue error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		public static void addValue(String key, String value) {
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				long result = jedis.lpush(key, value);

				LOGGER.debug("RedisList/addValue return : " + result);
			} catch (Exception e) {
				LOGGER.error("RedisList/addValue error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		public static List<String> getValue(String key) {
			Jedis jedis = null;
			try {
				jedis = jedisSentinelPool.getResource();
				List<String> result = jedis.lrange(key, 0, -1);

				return result;
			} catch (Exception e) {
				LOGGER.error("RedisList/getValue error.", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}

			return new ArrayList<String>();
		}

	}

}
