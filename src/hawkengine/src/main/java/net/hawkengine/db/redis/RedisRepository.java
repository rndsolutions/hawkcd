package net.hawkengine.db.redis;

import com.google.gson.Gson;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.DbEntry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisRepository<T extends DbEntry> implements IDbRepository<T> {
	private Type type;
	private String entryNamespace;
	private String idNamespace;
	private JedisPool jedisPool;
	private Gson jsonConverter;

	public RedisRepository(Class<T> type) {
		this.type = type;
		this.entryNamespace = String.format("%s:%s", "Entries", type.getSimpleName());
		this.idNamespace = String.format("%s:%s", "Ids", type.getSimpleName());
		this.jedisPool = RedisManager.getPool();
		this.jsonConverter = new Gson();

	}

	public RedisRepository(Class<T> entry, JedisPool pool) {
		this(entry);
		this.jedisPool = pool;
//		this.jsonConverter = new GsonBuilder().create();
//		this.type = entry;
//		this.jedis = mockedJedis;
//		Jedis mockJedis = mock(Jedis.class);
//		Client mockClient = mock(Client.class);
//		this.entryNamespace = String.format("%s:%s", "Entries", entry.getSimpleName());
//		this.idNamespace = String.format("%s:%s", "Ids", entry.getSimpleName());
//		this.transaction = spy(new Transaction(mockClient));
//		when(mockJedis.multi()).thenReturn(this.transaction);
	}

	@Override
	public T getById(String id) throws Exception {
		T result;
		try (Jedis jedis = this.jedisPool.getResource()) {
			String entryKey = String.format("%s:%s", this.entryNamespace, id);
			String entryValue = jedis.get(entryKey);

			result = this.jsonConverter.fromJson(entryValue, this.type);
		}

		return result;
	}

	@Override
	public List<T> getAll() throws Exception {
		List<T> result;
		try (Jedis jedis = RedisManager.getJedis()) {
			Set<String> entitiesIds = jedis.smembers(this.idNamespace);

			result = new ArrayList<>();
			for (String id : entitiesIds) {
				T entry = this.getById(id);
				result.add(entry);
			}
		}

		return result;
	}

	@Override
	public T add(T entry) throws Exception {
		T result;
		try (Jedis jedis = this.jedisPool.getResource()) {
			String entryKey = String.format("%s:%s", this.entryNamespace, entry.getId());
			String entryValue = jedis.set(entryKey, this.jsonConverter.toJson(entry));
			Long entryId = jedis.sadd(this.idNamespace, entry.getId());
			result = this.getById(entry.getId());
		}

		return result;
	}

	@Override
	public T update(T entry) throws Exception {
		try (Jedis jedis = RedisManager.getJedis()) {
			String entryKey = String.format("%s:%s", this.entryNamespace, entry.getId());

			Transaction transaction = jedis.multi();
			transaction.set(entryKey, this.jsonConverter.toJson(entry));
			transaction.sadd(this.idNamespace, entry.getId());
			transaction.exec();
		}

		return entry;
	}

	@Override
	public boolean delete(String id) throws Exception {
		try (Jedis jedis = RedisManager.getJedis()) {
			String entryKey = String.format("%s:%s", this.entryNamespace, id);

			Transaction transaction = jedis.multi();
			transaction.del(entryKey);
			transaction.srem(this.idNamespace, "26");
			List<Object> result = transaction.exec();
		}

		return true;
	}
}
