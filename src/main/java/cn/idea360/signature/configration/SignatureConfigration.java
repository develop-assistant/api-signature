package cn.idea360.signature.configration;

import cn.idea360.signature.algorithm.DefaultSignatureAlgorithm;
import cn.idea360.signature.algorithm.SignatureAlgorithm;
import cn.idea360.signature.properties.SignatureProperties;
import cn.idea360.signature.storage.InMemorySecretStorage;
import cn.idea360.signature.storage.SecretStorage;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cuishiying
 */
@Data
public class SignatureConfigration {

	private SignatureProperties signatureProperties = new SignatureProperties();

	private SecretStorage secretStorage = new InMemorySecretStorage();

	private final Map<String, SignatureAlgorithm> signatureAlgorithmMap = new HashMap<>();

	public SignatureConfigration() {
		for (DefaultSignatureAlgorithm algorithm : DefaultSignatureAlgorithm.values()) {
			SignatureAlgorithm signatureAlgorithm = algorithm.newInstance();
			signatureAlgorithmMap.put(algorithm.name(), signatureAlgorithm);
		}
	}

	public void addSignatureAlgorithm(String name, SignatureAlgorithm signatureAlgorithm) {
		signatureAlgorithmMap.put(name, signatureAlgorithm);
	}

}
