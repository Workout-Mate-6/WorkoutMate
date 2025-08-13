package com.example.workoutmate.domain.recommend.v3.vector;

import java.nio.charset.StandardCharsets;

public class HashingEncoder {
    private final int dim;
    public HashingEncoder(int dim) { this.dim = dim; }

    public float[] oneHot(String feature) {
        int idx = Math.floorMod(hash(feature), dim);
        float[] v = new float[dim];
        v[idx] = 1f;
        return v;
    }
    public void addFeature(float[] acc, String feature, double weight) {
        int idx = Math.floorMod(hash(feature), dim);
        acc[idx] += weight;
    }
    private int hash(String s) {
        // 간단 고정 해시. 필요하면 Murmur3 32-bit로 교체 가능.
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        int h = 0;
        for (byte x : b) h = 31 * h + (x & 0xff);
        return h;
    }
}
