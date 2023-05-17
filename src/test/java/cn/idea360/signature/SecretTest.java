package cn.idea360.signature;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author cuishiying
 */
@Slf4j
public class SecretTest {

	@Test
	void print() {
		log.info("test junit");
	}

	@Test
	void appId() {
		log.info("{}", System.currentTimeMillis());
	}

	@Test
	void md5() {
		String signatureData = "当我遇上你";
		String sign = DigestUtils.md5DigestAsHex(signatureData.getBytes(StandardCharsets.UTF_8));
		log.info(sign);
	}

	@Test
	void base64() {
		String signatureData = "当我遇上你";
		String base64 = Base64.encodeBase64String(signatureData.getBytes(StandardCharsets.UTF_8));
		log.info(base64);
	}

	@Test
	void timestamp() {
		LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()),
				ZoneOffset.ofHours(8));
		log.info("timestamp: {}", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	@Test
	void nonce() {
		log.info(UUID.randomUUID().toString().toLowerCase());
	}

	@Test
	void hmacSha256() throws NoSuchAlgorithmException, InvalidKeyException {
		String appSecret = "123";
		Mac hmacSha256 = Mac.getInstance("HmacSHA256");
		byte[] appSecretBytes = appSecret.getBytes(StandardCharsets.UTF_8);
		hmacSha256.init(new SecretKeySpec(appSecretBytes, 0, appSecretBytes.length, "HmacSHA256"));
		byte[] md5Result = hmacSha256.doFinal("hello".getBytes(StandardCharsets.UTF_8));
		String signature = Base64.encodeBase64String(md5Result);
		log.info(signature);
	}

	@Test
	void hmacSha1() throws NoSuchAlgorithmException, InvalidKeyException {
		String appSecret = "123";
		Mac hmacSha1 = Mac.getInstance("HmacSHA1");
		byte[] appSecretBytes = appSecret.getBytes(StandardCharsets.UTF_8);
		hmacSha1.init(new SecretKeySpec(appSecretBytes, 0, appSecretBytes.length, "HmacSHA1"));
		byte[] md5Result = hmacSha1.doFinal("hello".getBytes(StandardCharsets.UTF_8));
		String signature = Base64.encodeBase64String(md5Result);
		log.info(signature);
	}

	@Test
	void base64AndMD5() throws NoSuchAlgorithmException {
		final MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update("hello".getBytes(StandardCharsets.UTF_8));
		byte[] md5Result = md.digest();
		String signature = Base64.encodeBase64String(md5Result);
		log.info(signature);
	}

}
