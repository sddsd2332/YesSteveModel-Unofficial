package com.fox.ysmu.util;

public final class MathUtil {
    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static float rotLerp(float delta, float start, float end) {
        return start + delta * wrapDegrees(end - start);
    }

    public static int wrapDegrees(int angle) {
        int i = angle % 360;
        if (i >= 180) {
            i -= 360;
        }

        if (i < -180) {
            i += 360;
        }

        return i;
    }

    public static float wrapDegrees(float value) {
        float f = value % 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public static double wrapDegrees(double value) {
        double d0 = value % (double)360.0F;
        if (d0 >= (double)180.0F) {
            d0 -= 360.0F;
        }

        if (d0 < (double)-180.0F) {
            d0 += 360.0F;
        }

        return d0;
    }
}
