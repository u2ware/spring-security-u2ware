package org.springframework.security.web.authentication.rememberme;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.util.StringUtils;

public class TokenSignature extends AbstractTokenSignature{

	private static final int TWO_WEEKS_S = 1209600;

	private String key = getClass().getName();
	private String[] tokens;

	public TokenSignature(String username, String password) {

		int tokenLifetime = TWO_WEEKS_S;
		long expiryTime = System.currentTimeMillis();
		// SEC-949
		expiryTime += 1000L * (tokenLifetime < 0 ? TWO_WEEKS_S : tokenLifetime);

		String signatureValue = makeTokenSignature(expiryTime, username, password);

		this.tokens = new String[] {username, Long.toString(expiryTime), signatureValue};
	}
	public TokenSignature(String value) {
		this.tokens = super.decode(value);
	}

	public String encode() {
		return super.encode(this.tokens);
	}
	public void validate(String username, String password) {
		validate(this.tokens, username, password);
	}

	
	
	
	
	
	
	
	
	
	
	private String makeTokenSignature(long tokenExpiryTime, String username, String password) {
		String data = username + ":" + tokenExpiryTime + ":" + password + ":" + key;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm available!");
		}

		return new String(Hex.encode(digest.digest(data.getBytes())));
	}

	private void validate(String[] tokens, String username, String password) {

		long tokenExpiryTime;

		try {
			tokenExpiryTime = new Long(tokens[1]).longValue();
		}
		catch (NumberFormatException nfe) {
			throw new InvalidCookieException(
					"Cookie token[1] did not contain a valid number (contained '"
							+ tokens[1] + "')");
		}

		if (isTokenExpired(tokenExpiryTime)) {
			throw new InvalidCookieException("Cookie token[1] has expired (expired on '"
					+ new Date(tokenExpiryTime) + "'; current time is '" + new Date()
					+ "')");
		}

		String expectedTokenSignature = makeTokenSignature(tokenExpiryTime, username, password);

		if (!equals(expectedTokenSignature, tokens[2])) {
			throw new InvalidCookieException("Cookie token[2] contained signature '"
					+ tokens[2] + "' but expected '" + expectedTokenSignature + "'");
		}
	}
	private boolean isTokenExpired(long tokenExpiryTime) {
		return tokenExpiryTime < System.currentTimeMillis();
	}
	private boolean equals(String expected, String actual) {
		byte[] expectedBytes = bytesUtf8(expected);
		byte[] actualBytes = bytesUtf8(actual);
		if (expectedBytes.length != actualBytes.length) {
			return false;
		}

		int result = 0;
		for (int i = 0; i < expectedBytes.length; i++) {
			result |= expectedBytes[i] ^ actualBytes[i];
		}
		return result == 0;
	}

	private byte[] bytesUtf8(String s) {
		if (s == null) {
			return null;
		}
		return Utf8.encode(s);
	}

	
}
