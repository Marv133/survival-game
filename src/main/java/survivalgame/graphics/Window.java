package survivalgame.graphics;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import survivalgame.Config;
import survivalgame.map.generation.MapGenerator;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import static survivalgame.map.generation.MapGenerator.maxHeight;

public class Window {

    private final String windowTitle = Config.getString("window.title", "Survival Game");
    private final boolean fullscreen = Config.getBoolean("window.fullscreen", false);
    private final boolean highDPI = Config.getBoolean("window.high-dpi", true);
    private final int windowWidth = Config.getInt("window.width", 500);
    private final int windowHeight = Config.getInt("windows.height", 500);
    private final int sampleCount = Config.getInt("opengl.samples", 0);
    private final boolean useSRGB = Config.getBoolean("opengl.srgb", false);
    private final int openGLMajor = Config.getInt("opengl.context-major", 2);
    private final int openGLMinor = Config.getInt("opengl.context-minor", 1);
    private final boolean useDebug = Config.getBoolean("opengl.debug", false);
    private final boolean vsync = Config.getBoolean("opengl.vsync", true);
    private static final double GAME_HERTZ = 30.0f;

    public final static int mapSize = 128;

    private float[] map;
    private float delta;
    private long timing;
    private Camera camera;

    private void start(String[] args) {

        try {
            Config.parseCommandLineArguments(args);

            System.setProperty("org.lwjgl.opengl.Display.enableHighDPI", highDPI ? "true" : "false");
            System.setProperty("org.lwjgl.util.NoChecks", "false");

            Display.setTitle(windowTitle);
            if (fullscreen) {
                Display.setFullscreen(fullscreen);
            }
            else {
                Display.setDisplayMode(new DisplayMode(windowWidth, windowHeight));
            }
            Display.setResizable(!fullscreen);
            Display.setVSyncEnabled(vsync);

            final PixelFormat pixelFormat = new PixelFormat()
                .withBitsPerPixel(Display.getDesktopDisplayMode().getBitsPerPixel())
                .withSamples(sampleCount)
                .withSRGB(useSRGB);

            final ContextAttribs contextAttributes = new ContextAttribs(openGLMajor, openGLMinor)
                .withDebug(useDebug);

            Display.create(pixelFormat, contextAttributes);
        }
        catch (LWJGLException e) {
            e.printStackTrace();
        }

        setupOpenGL();

        MapGenerator generator = new MapGenerator(mapSize);
        map = generator.generate();

        camera = new Camera();
        camera.reset();
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        gameLoop();
        Display.destroy();
    }

    private void setupOpenGL() {
        glEnable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45f, windowWidth / windowHeight, 0.1f, 400);
        glMatrixMode(GL_MODELVIEW);
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBegin(GL_QUADS);
        for (int x = 0; x < mapSize - 1; x++) {
            for (int z = 0; z < mapSize - 1; z++) {
                putVertexAt(x, z);
                putVertexAt(x, z + 1);
                putVertexAt(x + 1, z + 1);
                putVertexAt(x + 1, z);
            }
        }
        glEnd();
    }

    public float getHeightAt(int x, int z) {
        return map[x + z * mapSize];
    }

    public void putVertexAt(int x, int z) {

        final float y = getHeightAt(x, z);

        if (y > 0.7f) {
            glColor3f(1 - y / maxHeight, 1 - y / maxHeight, 1 - y / maxHeight);
        }
        else {
            glColor3f(0, 0, 1);
        }

        glVertex3f(x, y, z);
    }

    public static void main(String[] args) {
        Config.parseCommandLineArguments(args);
        Window window = new Window();
        window.start(args);
    }

    private void gameLoop() {

        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;

        final int MAX_UPDATES_BEFORE_RENDER = 5;

        double lastUpdateTime = System.nanoTime();

        double lastRenderTime = System.nanoTime();

        final double TARGET_FPS = 60;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        while (!Display.isCloseRequested()) {
            double now = System.nanoTime();
            int updateCount = 0;

            if (Display.isActive()) {
                while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                    //updateGame();
                    lastUpdateTime += TIME_BETWEEN_UPDATES;
                    updateCount++;
                }

                if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                    lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                }

                float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
                camera.update(interpolation);
                camera.setMatrix();
                render();
                Display.update();
                Display.sync(60);

                while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                    now = System.nanoTime();
                }
            }
        }
    }
}
