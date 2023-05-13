package cn.idea360.signature.storage;

import cn.idea360.signature.properties.Secret;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cuishiying
 */
public class InMemorySecretStorage implements SecretStorage {

	private static final Map<String, Secret> SECRET_MAP = new ConcurrentHashMap<>();

	@Override
	public List<Secret> getAllSecret() {
		return null;
	}

	@Override
	public Secret getSecret(String appid) {
		return SECRET_MAP.getOrDefault(appid, null);
	}

	@Override
	public void addSecret(Secret secret) {
		SECRET_MAP.put(secret.getAppId(), secret);
	}

	@Override
	public void removeSecret(String appid) {
		SECRET_MAP.remove(appid);
	}

}
