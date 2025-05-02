package com.cabin.demo.util.id;

import com.cabin.express.config.Environment;

public class IdObfuscator {
    // Derive the long key once from a configured passphrase
    private static final long ENCRYPTOR_PHOTO_ID_KEY =
            KeyUtil.deriveKey64(Environment.getString("ENCRYPTOR_PHOTO_ID_KEY"));

    private static final long ENCRYPTOR_USER_ID_KEY =
            KeyUtil.deriveKey64(Environment.getString("ENCRYPTOR_USER_ID_KEY"));

    /**
     * Encodes rawId → obfuscated Base62 string
     */
    public static String encodePhotoId(long rawId) {
        long xored = rawId ^ ENCRYPTOR_PHOTO_ID_KEY;                                         // XOR obfuscation :contentReference[oaicite:5]{index=5}
        return Base62.encode(xored);
    }

    public static String encodeUserId(long rawId) {
        long xored = rawId ^ ENCRYPTOR_USER_ID_KEY;                                         // XOR obfuscation :contentReference[oaicite:5]{index=5}
        return Base62.encode(xored);
    }

    /**
     * Decodes Base62 string → raw ID
     */
    public static long decodePhotoId(String noiseId) {
        long xored = Base62.decode(noiseId);
        return xored ^ ENCRYPTOR_PHOTO_ID_KEY;                                                // reverse XOR :contentReference[oaicite:6]{index=6}
    }

    public static long decodeUserId(String noiseId) {
        long xored = Base62.decode(noiseId);
        return xored ^ ENCRYPTOR_USER_ID_KEY;                                                // reverse XOR :contentReference[oaicite:6]{index=6}
    }

    public static void main(String[] args) {
        long rawId = 12345L;

        String noise = encodePhotoId(rawId);
        System.out.println("Noise ID: " + noise);
        System.out.println("Decoded ID: " + decodePhotoId(noise));

        System.err.println(0x20F0F0F0F0F0F0FL);
    }
}
