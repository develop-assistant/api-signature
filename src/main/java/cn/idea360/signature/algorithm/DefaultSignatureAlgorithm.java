package cn.idea360.signature.algorithm;

import org.springframework.objenesis.instantiator.util.ClassUtils;

/**
 * @author cuishiying
 */
public enum DefaultSignatureAlgorithm {

	MD5(MD5SignatureAlgorithm.class), HmacSHA1(HmacSHA1SignatureAlgorithm.class), HmacSHA256(
			HmacSHA256SignatureAlgorithm.class);

	private final Class<? extends SignatureAlgorithm> algorithmClass;

	DefaultSignatureAlgorithm(Class<? extends SignatureAlgorithm> algorithmClass) {
		this.algorithmClass = algorithmClass;
	}

	public SignatureAlgorithm newInstance() {
		return ClassUtils.newInstance(this.algorithmClass);
	}

}
