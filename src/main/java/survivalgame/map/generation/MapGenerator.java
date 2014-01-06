package survivalgame.map.generation;

import survivalgame.MathTools;
import survivalgame.map.SimplexNoise;

import static survivalgame.MathTools.boundBy;

public class MapGenerator {

    private SimplexNoise simplex = new SimplexNoise();
    private final int width;
    private final int depth;
    public final static int maxHeight = 10;
    private float[] map;

    public MapGenerator(int size) {
        this.depth = size;
        this.width = size;
    }

    public float[] generate() {
        map = new float[width * depth];
        simplex.randomSeed();
        addNoise(1.5f,0.5f);
        perturb(8.0f, 9.0f);
        for (int i = 0; i < 10; i++) {
            erode(20.0f);
        }
        smoothen();
        return map;
    }

    private float length(float x, float z) {
        return (float) Math.sqrt(x*x + z*z);
    }

    private void addNoise(float f, float p) {
        float max = 0;
        for (float x = 0; x < width; x++) {
            for (float z = 0; z < depth; z++) {
                float density = 0;
                density +=  1-boundBy(0,1,length((x - width / 2) / (width / 2), (z - depth / 2) / (depth / 2)));
                density = (float) ((simplex.generate(x/width,z/depth,6,p,f)*0.5f+0.5)*maxHeight * density);
                max = max < density ? density : max;
                map[(int)(x + z * depth)] = density;
            }
        }
        System.out.println(max);
    }

    private void perturb(float f, float d) {
        int u, v;
        float[] tempMap = new float[width * depth];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                u = x + (int) ((simplex.generate(f * x / (float) width, f * z / (float) depth) * 0.5 + 0.5) * d);
                v = z + (int) ((simplex.generate(f * x / (float) width, f * z / (float) depth) * 0.5 + 0.5) * d);
                if (u < 0) { u = 0; }
                if (u >= width) { u = width - 1; }
                if (v < 0) { v = 0; }
                if (v >= depth) { v = depth - 1; }
                tempMap[x + z * depth] = map[u + v * depth];
            }
        }
        map = tempMap;
    }

    private void erode(float smoothness) {
        for (int x = 1; x < width - 1; x++) {
            for (int z = 1; z < depth - 1; z++) {
                float d_max = 0.0f;
                int[] match = {0, 0};

                for (int u = -1; u <= 1; u++) {
                    for (int v = -1; v <= 1; v++) {
                        if (MathTools.abs(u) + MathTools.abs(v) > 0) {
                            float d_i = map[x + z * depth] - map[(x + u) + (z + v) * depth];
                            if (d_i > d_max) {
                                d_max = d_i;
                                match[0] = u;
                                match[1] = v;
                            }
                        }
                    }
                }

                if (0 < d_max && d_max <= (smoothness / (float) width)) {
                    float d_h = 0.5f * d_max;
                    map[x + z * depth] -= d_h;
                    map[(x + match[0]) + (z + match[1]) * depth] += d_h;
                }
            }
        }
    }

    private void smoothen() {
        for (int x = 1; x < width - 1; ++x) {
            for (int z = 1; z < depth - 1; ++z) {
                float total = 0.0f;
                for (int u = -1; u <= 1; u++) {
                    for (int v = -1; v <= 1; v++) {
                        total += map[(x + u) + (z + v) * depth];
                    }
                }

                map[x + z * depth] = total / 9.0f;
            }
        }
    }
}
