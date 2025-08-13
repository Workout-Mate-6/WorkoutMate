package com.example.workoutmate.domain.recommend.v3.vector;

import java.nio.charset.StandardCharsets;

//누적형 해시 인코더
public class HashingEncoder {
    private final int dim; // 벡터 차원 수 (고정 길이 벡터 크기)

    // 생성자 : 사용할 벡터 차워 수를 지정
    public HashingEncoder(int dim) {
        this.dim = dim;
    }


    /**
     * 주어진 feature를 해시하여 벡터의 특정 인덱스에 weight를 누적
     * @param acc      누적할 대상 벡터 (외부에서 생성된 벡터)
     * @param feature  문자열 형태의 feature key (예: "type:SOCCER")
     * @param weight   해당 feature의 가중치 (더할 값)
     */
    public void addFeature(float[] acc, String feature, double weight) {
        int idx = Math.floorMod(hash(feature), dim);
        acc[idx] += weight;
    }


    /**
     * 문자열을 정수 해시값으로 변환
     * - 현재는 간단한 31배수 곱셈 기반 해시
     * - 필요 시 더 좋은 분산 특성을 가진 Murmur3 등으로 교체 가능
     */
    private int hash(String s) {
        // 간단 고정 해시. 필요하면 Murmur3 32-bit로 교체 가능.
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        int h = 0;
        for (byte x : b) h = 31 * h + (x & 0xff);
        return h;
    }
}
