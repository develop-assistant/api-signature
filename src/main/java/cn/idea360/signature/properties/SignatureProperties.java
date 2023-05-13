package cn.idea360.signature.properties;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuishiying
 */
@Data
public class SignatureProperties {

	/**
	 * 密钥对
	 */
	private List<Secret> secrets = new ArrayList<>();

	/**
	 * 加密方式, 默认是单接口加密(SINGLE) SINGLE: 白名单模式, 只有includedUri中的api验签 ALL: 黑名单模式,
	 * 只有excludedUri中的api不验签
	 */
	private SignatureType signatureType = SignatureType.SINGLE;

	/**
	 * 无需验签的uri
	 */
	private List<String> excludedUris = new ArrayList<>();

	/**
	 * 需要验签的uri
	 */
	private List<String> includedUris = new ArrayList<>();

	/**
	 * 时间戳有效性(单位秒) 默认5分钟
	 */
	private long expireInSeconds = 60L * 5;

}
