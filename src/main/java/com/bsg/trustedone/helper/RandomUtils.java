package com.bsg.trustedone.helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

public final class RandomUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private RandomUtils() {
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static String nextString(int size) {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    public static long nextLong(long min, long max) {
        return RANDOM.nextLong(min, max + 1);
    }

    public static int nextInt(int min, int max) {
        return RANDOM.nextInt(min, max + 1);
    }

    public static BigDecimal nextBigDecimal(long min, long max) {
        return BigDecimal.valueOf(nextLong(min, max + 1));
    }

    public static BigDecimal nextBigDecimal(long min, long max, int scale) {
        return BigDecimal.valueOf(nextLong(min, max + 1), scale);
    }

    public static BigInteger nextBigInteger(long min, long max) {
        return BigInteger.valueOf(nextLong(min, max + 1));
    }

    public static double nextDouble(int min, int max) {
        return RANDOM.nextDouble(min, max + 1);
    }

    public static UUID nextUUID() {
        return UUID.randomUUID();
    }

    public static <T> T randomItemArray(T[] arr) {
        return arr[nextInt(0, arr.length - 1)];
    }

    public static <E extends Enum<E>> E randomItemEnum(Class<E> enumClass) {
        E[] values = enumClass.getEnumConstants();
        return randomItemArray(values);
    }
}