package cn.idea360.signature.properties;

import lombok.Builder;
import lombok.Data;

/**
 * @author cuishiying
 */
@Builder
@Data
public class Secret {

	private String appId;

	private String appSecret;

	@Builder.Default
	private String signatureMethod = SignatureMethod.MD5.name();

}
