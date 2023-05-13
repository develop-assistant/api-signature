package cn.idea360.signature.algorithm;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author cuishiying
 */
public class MD5SignatureAlgorithm implements SignatureAlgorithm {

	@Override
	public String signature(String signatureData) {
		String md5 = DigestUtils.md5DigestAsHex(signatureData.getBytes(StandardCharsets.UTF_8)).toLowerCase();
		return Base64.encodeBase64String(md5.getBytes(StandardCharsets.UTF_8));
	}

}
