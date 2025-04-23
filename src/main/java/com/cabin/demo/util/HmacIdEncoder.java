package com.cabin.demo.util;

import com.cabin.express.config.Environment;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class HmacIdEncoder {
    private static final String HMAC_ALGO = "HmacSHA256";
    // Secret key from your CabinJV config (must be kept safe!)
    private static final byte[] SECRET =
            Environment.getString("ID_ENCRYPTOR_KEY").getBytes(StandardCharsets.UTF_8);

    /** Number of HMAC bytes to include (8 bytes → 16 hex chars) */
    private static final int TRUNCATE_BYTES = 8;

    /**
     * Encodes the given integer ID into a token "id:hexSig".
     */
    public String encode(int id) throws GeneralSecurityException {
        String payload = Integer.toString(id);
        Mac mac = Mac.getInstance(HMAC_ALGO);                    // HMAC-SHA256 :contentReference[oaicite:0]{index=0}
        mac.init(new SecretKeySpec(SECRET, HMAC_ALGO));
        byte[] fullSig = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        byte[] sig = Arrays.copyOf(fullSig, TRUNCATE_BYTES);
        return payload + ":" + bytesToHex(sig);
    }

    /**
     * Decodes the token, validates its HMAC, and returns the original ID.
     */
    public int decode(String token) throws GeneralSecurityException {
        String[] parts = token.split(":", 2);
        if (parts.length != 2) throw new IllegalArgumentException("Invalid token format");

        String payload = parts[0];
        String sigHex   = parts[1];
        Mac mac = Mac.getInstance(HMAC_ALGO);
        mac.init(new SecretKeySpec(SECRET, HMAC_ALGO));
        byte[] expectedFull = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        byte[] expectedSig  = Arrays.copyOf(expectedFull, TRUNCATE_BYTES);
        String expectedHex  = bytesToHex(expectedSig);

        // constant-time compare to prevent timing attacks
        if (!MessageDigest.isEqual(expectedHex.getBytes(StandardCharsets.UTF_8),
                sigHex.getBytes(StandardCharsets.UTF_8))) {
            throw new SecurityException("Invalid HMAC signature");
        }
        return Integer.parseInt(payload);
    }

    /** Utility: convert a byte array to a hex string :contentReference[oaicite:1]{index=1} */
    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2]     = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // Quick test
    public static void main(String[] args) throws Exception {
        HmacIdEncoder encoder = new HmacIdEncoder();
        int id = 12345;
        String token = encoder.encode(id);
        System.out.println("Token: " + token);
        System.out.println("Decoded ID: " + encoder.decode(token));
    }
}
