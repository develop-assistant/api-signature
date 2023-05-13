package cn.idea360.signature.properties;

/**
 * @author cuishiying
 */
public enum SignatureMethod {

	MD5, HmacSHA1, HmacSHA256;

	public static boolean exists(String algorithm) {
		try {
			SignatureMethod.valueOf(algorithm);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

}
