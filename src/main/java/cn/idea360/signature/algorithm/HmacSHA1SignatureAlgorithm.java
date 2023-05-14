package cn.idea360.signature.algorithm;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author cuishiying
 */
public class HmacSHA1SignatureAlgorithm implements SignatureAlgorithm {

	@Override
	public String signature(String signatureData, String appSecret) {
		Mac mac = null;
		try {
			mac = Mac.getInstance("HmacSHA1");
			byte[] appSecretBytes = appSecret.getBytes(StandardCharsets.UTF_8);
			mac.init(new SecretKeySpec(appSecretBytes, 0, appSecretBytes.length, "HmacSHA1"));
		}
		catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IllegalArgumentException(e);
		}
		byte[] result = mac.doFinal(signatureData.getBytes(StandardCharsets.UTF_8));
		return Base64.encodeBase64String(result);
	}

}
