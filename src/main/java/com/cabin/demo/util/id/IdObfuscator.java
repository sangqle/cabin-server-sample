package com.cabin.demo.util.id;

import com.cabin.express.config.Environment;

public class IdObfuscator {
    // Derive the long key once from a configured passphrase
    private static final long SECRET_KEY =
            KeyUtil.deriveKey64(Environment.getString("ID_ENCRYPTOR_KEY"));

    /**
     * Encodes rawId → obfuscated Base62 string
     */
    public static String encodeId(long rawId) {
        long xored = rawId ^ SECRET_KEY;                                         // XOR obfuscation :contentReference[oaicite:5]{index=5}
        return Base62.encode(xored);
    }

    /**
     * Decodes Base62 string → raw ID
     */
    public static long decodeId(String noiseId) {
        long xored = Base62.decode(noiseId);
        return xored ^ SECRET_KEY;                                                // reverse XOR :contentReference[oaicite:6]{index=6}
    }

    public static void main(String[] args) {
        long rawId = 12345L;
        String noise = encodeId(rawId);
        System.out.println("Noise ID: " + noise);
        System.out.println("Decoded ID: " + decodeId(noise));
    }
}
