package selab.hanyang.ac.kr.platformmanager.controller;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

public class OTP {

    private String key;
    private static final long DISTANCE = 30000;
    private static final String ALGORITHM = "HmacSHA256";

    private static long create(String SECRET_KEY, long time) throws Exception {
        byte[] data = new byte[8];

        long value = time;
        for(int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM));

        byte[] hash = mac.doFinal(data);
        int offset = hash[20-1] & 0xF;

        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= hash[offset+i] & 0xFF;
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;

        return truncatedHash;
    }

    public static String create(String SECRET_KEY) throws Exception {
        return String.format("%06d", create(SECRET_KEY, new Date().getTime()/DISTANCE));
    }

}