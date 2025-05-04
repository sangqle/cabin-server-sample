package com.cabin.demo.util.photo;

import com.cabin.express.config.Environment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class FileNameEncoder {

    // HMAC-SHA256 algorithm and secret (load from env/config)
    private static final String HMAC_ALGO = "HmacSHA256";
    private static final byte[] SECRET =
            Environment.getString("FILE_NAME_SECRET").getBytes(StandardCharsets.UTF_8);

    // Allowed alphabet for Base62 (62 chars; URL-safe)
    private static final char[] ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BASE = ALPHABET.length;

    // Desired length of the encoded basename
    private static final int TARGET_LENGTH = 20;

    public static String encode(String fileName) {
        try {
            // 1) split base + extension
            int dot = fileName.lastIndexOf('.');
            String base = dot > 0 ? fileName.substring(0, dot) : fileName;
            String ext  = dot > 0 ? fileName.substring(dot)    : "";

            // 2) HMAC-SHA256 of basename
            byte[] hmac = hmacSha256(base.getBytes(StandardCharsets.UTF_8));

            // 3) Base62-encode full HMAC bytes
            String full62 = encodeBase62(hmac);

            // 4) Trim or pad to TARGET_LENGTH
            String short62 = full62.length() > TARGET_LENGTH
                    ? full62.substring(0, TARGET_LENGTH)
                    : padRight(full62, TARGET_LENGTH, '0');
            // 5) reattach extension
            return short62 + ext;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] hmacSha256(byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(SECRET, HMAC_ALGO));
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("HMAC failure", e);
        }
    }

    private static String encodeBase62(byte[] data) {
        // Interpret data as an unsigned big integer
        java.math.BigInteger bi = new java.math.BigInteger(1, data);
        StringBuilder sb = new StringBuilder();
        while (bi.signum() > 0) {
            java.math.BigInteger[] divmod = bi.divideAndRemainder(
                    java.math.BigInteger.valueOf(BASE)
            );
            sb.append(ALPHABET[divmod[1].intValue()]);
            bi = divmod[0];
        }
        return sb.length() > 0
                ? sb.reverse().toString()
                : "0";
    }

    private static String padRight(String s, int length, char pad) {
        if (s.length() >= length) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < length) sb.append(pad);
        return sb.toString();
    }

    // Quick test
    public static void main(String[] args) {
        String[] names = { "photo.jpg", "report.pdf", "打招呼.png" };
        for (String name : names) {
            System.out.println(name + " -> " + encode(name));
        }
    }
}
