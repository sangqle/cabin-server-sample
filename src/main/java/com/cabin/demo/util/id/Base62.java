package com.cabin.demo.util.id;

public class Base62 {
    private static final String ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    // Encode a non-negative long to a Base62 string
    public static String encode(long value) {
        if (value == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int digit = (int)(value % BASE);
            sb.append(ALPHABET.charAt(digit));
            value /= BASE;
        }
        return sb.reverse().toString(); // reverse since we built LSB→MSB :contentReference[oaicite:4]{index=4}
    }

    // Decode a Base62 string back to its long value
    public static long decode(String str) {
        long result = 0;
        for (char c : str.toCharArray()) {
            int idx = ALPHABET.indexOf(c);
            if (idx < 0) throw new IllegalArgumentException("Invalid Base62 char: " + c);
            result = result * BASE + idx;
        }
        return result;  // gives the original obfuscated long :contentReference[oaicite:5]{index=5}
    }
}