package cn.idea360.signature.filter;

import cn.idea360.signature.algorithm.SignatureAlgorithm;
import cn.idea360.signature.configration.SignatureConfigration;
import cn.idea360.signature.properties.Secret;
import cn.idea360.signature.constant.SignatureConstant;
import cn.idea360.signature.properties.SignatureProperties;
import cn.idea360.signature.properties.SignatureType;
import cn.idea360.signature.storage.SecretStorage;
import cn.idea360.signature.wrapper.ContentCachingRequestWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.idea360.signature.constant.HttpConstant.CONTENT_TYPE_FORM;
import static cn.idea360.signature.constant.HttpConstant.CONTENT_TYPE_JSON;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * @author cuishiying
 */
@Slf4j
public class SignatureFilter implements Filter {

	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Map<String, SignatureAlgorithm> signatureAlgorithmMap;

	private final SignatureProperties signatureProperties;

	private final SecretStorage secretStorage;

	public SignatureFilter(SignatureConfigration configration) {
		signatureAlgorithmMap = configration.getSignatureAlgorithmMap();
		signatureProperties = configration.getSignatureProperties();
		secretStorage = configration.getSecretStorage();
		signatureProperties.getSecrets().forEach(secretStorage::addSecret);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(
				(HttpServletRequest) servletRequest);
		String servletPath = requestWrapper.getServletPath();
		if ((signatureProperties.getSignatureType().equals(SignatureType.SINGLE)
				&& !this.contains(signatureProperties.getIncludedUris(), servletPath))
				|| (signatureProperties.getSignatureType().equals(SignatureType.ALL)
						&& this.contains(signatureProperties.getExcludedUris(), servletPath))) {
			filterChain.doFilter(requestWrapper, servletResponse);
			return;
		}
		this.doSignature(requestWrapper, servletResponse, filterChain);
	}

	private void doSignature(ContentCachingRequestWrapper requestWrapper, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String appid = obtainHeader(requestWrapper, SignatureConstant.CA_KEY);
		String nonce = obtainHeader(requestWrapper, SignatureConstant.CA_NONCE);
		String timestamp = obtainHeader(requestWrapper, SignatureConstant.CA_TIMESTAMP);
		String algorithm = obtainHeader(requestWrapper, SignatureConstant.CA_SIGNATURE_METHOD);
		String signature = obtainHeader(requestWrapper, SignatureConstant.CA_SIGNATURE);
		try {
			Assert.hasText(appid, "appid must not be null");
			Assert.hasText(algorithm, "algorithm must not be null");
			Assert.hasText(timestamp, "timestamp must not be null");
			Assert.hasText(nonce, "nonce must not be null");
			Assert.hasText(signature, "signature must not be null");
		}
		catch (Exception e) {
			this.sendUnauthorizedMessage(response, "签名参数不完整");
			return;
		}

		Secret secret = obtainSecret(appid);
		if (Objects.isNull(secret) || !StringUtils.hasText(secret.getAppSecret())) {
			this.sendUnauthorizedMessage(response, "签名密钥不存在");
			return;
		}

		LocalDateTime inTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)),
				ZoneOffset.ofHours(8));
		Duration duration = Duration.between(inTime, LocalDateTime.now());
		long seconds = duration.get(SECONDS);
		if (seconds > signatureProperties.getExpireInSeconds()) {
			this.sendUnauthorizedMessage(response, "签名已失效");
			return;
		}

		if (!secret.getSignatureMethod().equals(algorithm)) {
			this.sendUnauthorizedMessage(response, "签名类型不支持");
			return;
		}

		String query = requestWrapper.getQueryString();
		String body = this.obtainBody(requestWrapper);
		String signatureData = String.format("%s%s%s%s%s", query, body, nonce, timestamp, secret.getAppSecret());
		String sign = signatureAlgorithmMap.get(algorithm).signature(signatureData, secret.getAppSecret());
		if (!sign.equals(signature)) {
			this.sendUnauthorizedMessage(response, "验签失败");
			return;
		}
		filterChain.doFilter(requestWrapper, response);
	}

	private String obtainHeader(HttpServletRequest request, String header) {
		String result = request.getHeader(header);
		return result == null ? "" : result.trim();
	}

	private String obtainBody(HttpServletRequest request) throws IOException {
		byte[] requestBody = StreamUtils.copyToByteArray(request.getInputStream());
		return new String(requestBody, StandardCharsets.UTF_8);
	}

	private String obtainBodyIgnoreBlank(HttpServletRequest request) throws IOException {
		return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()))
				.replaceAll("\\s*|\\t|\\r|\\n", "");
	}

	private Secret obtainSecret(String appid) {
		return secretStorage.getSecret(appid);
	}

	private boolean contains(List<String> paths, String servletPath) {
		if (!CollectionUtils.isEmpty(paths)) {
			for (String path : paths) {
				String uriPattern = path.trim();
				if (antPathMatcher.match(uriPattern, servletPath)) {
					return true;
				}
			}
		}
		return false;
	}

	private void sendUnauthorizedMessage(HttpServletResponse response, String message) {
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		try {
			PrintWriter out = response.getWriter();
			Map<String, Object> map = new HashMap<>();
			map.put("code", HttpServletResponse.SC_UNAUTHORIZED);
			map.put("msg", message);
			out.write(objectMapper.writeValueAsString(map));
			out.flush();
			out.close();
		}
		catch (Exception e) {
			log.error("can't push signature err message", e);
		}
	}

	private String buildStringToSign(HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder();
		if ("GET".equals(request.getMethod()) || CONTENT_TYPE_FORM.equals(request.getHeader("Content-type"))) {
			sb.append(request.getQueryString());
		}
		if (CONTENT_TYPE_JSON.equals(request.getHeader("Content-type"))) {
			sb.append(obtainBody(request));
		}
		return sb.toString();
	}

}
