package cn.idea360.signature.storage;

import cn.idea360.signature.properties.Secret;

import java.util.List;

/**
 * @author cuishiying
 */
public interface SecretStorage {

	/**
	 * 获取所有密钥
	 * @return 所有密钥
	 */
	List<Secret> getAllSecret();

	/**
	 * 获取单个密钥
	 * @param appid 密钥唯一ID
	 * @return 密钥
	 */
	Secret getSecret(String appid);

	/**
	 * 增加密钥
	 * @param secret 单个密钥
	 */
	void addSecret(Secret secret);

	/**
	 * 移除密钥
	 * @param appid 密钥唯一ID
	 */
	void removeSecret(String appid);

}
