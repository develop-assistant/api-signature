package cn.idea360.signature;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
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
		String sign = DigestUtils.md5DigestAsHex(signatureData.getBytes(StandardCharsets.UTF_8)).toLowerCase();
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

}
