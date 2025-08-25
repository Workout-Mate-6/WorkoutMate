package com.example.workoutmate.domain.recommend.v3.vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class VectorUtils {
    private VectorUtils() {
    }


    /**
     * L2 정규화: 벡터의 길이를 1로 맞춤
     * - sum = 각 요소 제곱의 합
     * - norm = sqrt(sum)
     * - 각 요소를 norm으로 나누어 길이를 1로 만든다.
     */
    public static void l2Normalize(float[] v) {
        double sum = 0.0;
        for (float x : v) sum += x * x;
        double norm = Math.sqrt(sum);
        if (norm == 0) return;
        for (int i = 0; i < v.length; i++) v[i] /= (float) norm;
    }

    /**
     * 두 벡터의 내적(dot product) 계산
     * - 코사인 유사도 계산 시에도 사용 가능 (정규화된 벡터일 경우)
     */
    public static double dot(float[] a, float[] b) {
        double s = 0.0;
        for (int i = 0; i < a.length; i++) s += a[i] * b[i];
        return s;
    }


    /**
     * float 배열 → byte 배열 직렬화
     * - 4바이트(32비트) 부동소수점 × 길이만큼 ByteBuffer에 저장
     * - BIG_ENDIAN 바이트 순서 사용
     */
    public static byte[] toBytes(float[] v) {
        ByteBuffer buf = ByteBuffer.allocate(v.length * 4).order(ByteOrder.BIG_ENDIAN);
        for (float x : v) buf.putFloat(x);
        return buf.array();
    }


    /**
     * byte 배열 → float 배열 역직렬화
     * - 4바이트 단위로 float 값 읽어오기
     * - BIG_ENDIAN 바이트 순서 사용
     */
    public static float[] fromBytes(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        float[] v = new float[bytes.length / 4];
        for (int i = 0; i < v.length; i++) v[i] = buf.getFloat();
        return v;
    }

    /**
     * 지정된 차원의 0으로 채워진 벡터 생성
     */
    public static float[] zeros(int dim) {
        return new float[dim];
    }

//    public static float[] clone(float[] v) {
//        return Arrays.copyOf(v, v.length);
//    }

    /**
     * 벡터 a에 벡터 b × weight 값을 더함 (제자리 수정)
     * - a[i] = a[i] + b[i] * weight
     */
    public static void addInPlace(float[] a, float[] b, double weight) {
        for (int i = 0; i < a.length; i++) a[i] += b[i] * weight;
    }

    public static float l2Norm(float[] vector) {
        double sum = 0.0;
        for (float value : vector) {
            sum += value * value;
        }
        return (float) Math.sqrt(sum);
    }
}
