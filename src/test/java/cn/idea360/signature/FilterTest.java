package cn.idea360.signature;

import cn.idea360.signature.algorithm.MD5SignatureAlgorithm;
import cn.idea360.signature.configration.SignatureConfigration;
import cn.idea360.signature.filter.SignatureFilter;
import cn.idea360.signature.properties.Secret;
import cn.idea360.signature.constant.SignatureConstant;
import cn.idea360.signature.properties.SignatureProperties;
import cn.idea360.signature.storage.InMemorySecretStorage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

/**
 * @author cuishiying
 */
@Slf4j
public class FilterTest {

	private final MockHttpServletRequest request = new MockHttpServletRequest();

	private final MockHttpServletResponse response = new MockHttpServletResponse();

	private final MockFilterChain filterChain = new MockFilterChain();

	private SignatureConfigration signatureConfigration;

	private Secret secret;

	@BeforeEach
	public void init() throws Exception {
		secret = Secret.builder().appName("当我遇上你").appId("xxx").appSecret("123").build();
		SignatureProperties signatureProperties = new SignatureProperties();
		signatureProperties.setSecrets(Collections.singletonList(secret));
		signatureProperties.setIncludedUris(Collections.singletonList("/sign"));
		signatureConfigration = new SignatureConfigration();
		signatureConfigration.setSecretStorage(new InMemorySecretStorage());
		signatureConfigration.setSignatureProperties(signatureProperties);
	}

	@Test
	void signature() throws Exception {
		String nonce = UUID.randomUUID().toString().toLowerCase();
		Long timestamp = System.currentTimeMillis();
		String queryString = "name=admin";
		String requestBody = "{\"msg\": \"Hello World\"}";
		String signatureData = String.format("%s%s%s%s%s", queryString, requestBody, nonce, timestamp,
				secret.getAppSecret());
		String signature = new MD5SignatureAlgorithm().signature(signatureData, secret.getAppSecret());
		log.info("signature data: {}", signatureData);
		log.info("request signature: {}", signature);

		request.setMethod("POST");
		request.setServletPath("/sign");
		request.setCharacterEncoding("UTF-8");
		request.setContentType("application/json");
		request.setQueryString(queryString);
		request.addParameter("age", "17");
		request.setContent(requestBody.getBytes(StandardCharsets.UTF_8));

		request.addHeader(SignatureConstant.CA_KEY, secret.getAppId());
		request.addHeader(SignatureConstant.CA_NONCE, nonce);
		request.addHeader(SignatureConstant.CA_TIMESTAMP, timestamp);
		request.addHeader(SignatureConstant.CA_SIGNATURE_METHOD, secret.getSignatureMethod());
		request.addHeader(SignatureConstant.CA_SIGNATURE, signature);

		SignatureFilter filter = new SignatureFilter(signatureConfigration);
		filter.doFilter(request, response, filterChain);
	}

}
