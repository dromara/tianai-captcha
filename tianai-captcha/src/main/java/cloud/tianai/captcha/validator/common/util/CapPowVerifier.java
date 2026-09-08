package cloud.tianai.captcha.validator.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CapPowVerifier {

    // ================= SHA256 ====================

    private static byte[] sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // ============== PRNG (hex) ==================

    /**
     * cap 风格 PRNG:
     * sha256(seed).hex().slice(0, len)
     */
    public static String prngHex(String seed, int length) {
        byte[] hash = sha256(seed.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }

        return sb.substring(0, length);
    }

    // ============== HEX PARSER ==================

    public static byte[] parseHexTarget(String hex) {
        if ((hex.length() & 1) == 1) {
            hex = hex + "0";
        }

        int len = hex.length() / 2;
        byte[] out = new byte[len];

        for (int i = 0; i < len; i++) {
            out[i] = (byte) Integer.parseInt(
                hex.substring(i * 2, i * 2 + 2),
                16
            );
        }

        return out;
    }

    // ========= BIT PREFIX MATCH =================

    /**
     * 完全复刻 wasm 的 hash_matches_target
     */
    public static boolean hashMatchesTarget(
        byte[] hash,
        byte[] targetBytes,
        int targetBits
    ) {
        int fullBytes = targetBits / 8;
        int remainingBits = targetBits % 8;

        for (int i = 0; i < fullBytes; i++) {
            if (hash[i] != targetBytes[i]) {
                return false;
            }
        }

        if (remainingBits > 0 && fullBytes < targetBytes.length) {
            int mask = 0xFF << (8 - remainingBits);

            int h = hash[fullBytes] & 0xFF;
            int t = targetBytes[fullBytes] & 0xFF;

            if ((h & mask) != (t & mask)) {
                return false;
            }
        }

        return true;
    }

    // ============== VERIFY ======================

    /**
     * redeem 校验
     */
    public static boolean verify(
        String token,
        int index,
        long nonce,
        int saltLen,
        int difficulty
    ) {
        // salt / target 派生
        String saltHex =
            prngHex(token + (index + 1), saltLen);

        String targetHex =
            prngHex(token + (index + 1) + "d", difficulty);

        byte[] salt = saltHex.getBytes(StandardCharsets.UTF_8);
        byte[] targetBytes = parseHexTarget(targetHex);

        int targetBits = targetHex.length() * 4;

        // salt || ascii(nonce)
        byte[] nonceBytes =
            Long.toString(nonce).getBytes(StandardCharsets.UTF_8);

        byte[] input = new byte[salt.length + nonceBytes.length];
        System.arraycopy(salt, 0, input, 0, salt.length);
        System.arraycopy(nonceBytes, 0, input, salt.length, nonceBytes.length);

        byte[] hash = sha256(input);

        return hashMatchesTarget(hash, targetBytes, targetBits);
    }

    // ============== DEMO ========================

    public static void main(String[] args) {
        String token = "POW_123123131313132131231";
        int index = 1;
        long nonce = 55406;

        int saltLen = 16;
        int difficulty = 4;

        boolean ok = verify(token, index, nonce, saltLen, difficulty);

        System.out.println("valid = " + ok);
    }
}
