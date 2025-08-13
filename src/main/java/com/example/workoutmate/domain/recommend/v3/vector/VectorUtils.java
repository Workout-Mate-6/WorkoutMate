package com.example.workoutmate.domain.recommend.v3.vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public final class VectorUtils {
    private VectorUtils() {}

    public static void l2Normalize(float[] v) {
        double sum = 0.0;
        for (float x : v) sum += x * x;
        double norm = Math.sqrt(sum);
        if (norm == 0) return;
        for (int i = 0; i < v.length; i++) v[i] /= (float) norm;
    }
    public static double dot(float[] a, float[] b) {
        double s = 0.0;
        for (int i = 0; i < a.length; i++) s += a[i] * b[i];
        return s;
    }
    public static byte[] toBytes(float[] v) {
        ByteBuffer buf = ByteBuffer.allocate(v.length * 4).order(ByteOrder.BIG_ENDIAN);
        for (float x : v) buf.putFloat(x);
        return buf.array();
    }
    public static float[] fromBytes(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        float[] v = new float[bytes.length / 4];
        for (int i = 0; i < v.length; i++) v[i] = buf.getFloat();
        return v;
    }
    public static float[] zeros(int dim) { return new float[dim]; }
    public static float[] clone(float[] v) { return Arrays.copyOf(v, v.length); }
    public static void addInPlace(float[] a, float[] b, double weight) {
        for (int i = 0; i < a.length; i++) a[i] += b[i] * weight;
    }
}
