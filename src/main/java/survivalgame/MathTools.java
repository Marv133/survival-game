package survivalgame;

public class MathTools {

    public static float lerp(float a, float b, float t) {

        return a+t*(b-a);
    }

    /**
     * The result is always between min and max.
     * If value is smaller min the result will be min
     * and if its larger than max the result will be max.
     */
    public static float boundBy(float min, float max, float value) {

        if(value < min) {
            return min;
        }
        else if(value > max) {
            return max;
        }
        else {
            return value;
        }
    }

    /**
     * value <  0.5 => 0
     * value >= 0.5 => 1
     */
    public static float step(float value) {

        return (value < 0.5f) ? 0.0f : 1.0f;
    }

    public static float stair(float steps, float value) {

        return (float)Math.floor(value*steps)/steps;
    }

    public static float abs(float value) {

        return (value < 0.0f) ? -value : value;
    }

    public static float min(float a, float b) {

        return (a < b) ? a : b;
    }

    public static float max(float a, float b) {

        return (a > b) ? a : b;
    }

    public static int mod(int a, int b) {
        return (a % b + b) % b;
    }
}
