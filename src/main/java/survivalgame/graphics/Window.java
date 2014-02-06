package survivalgame.graphics;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBDebugOutputCallback;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import survivalgame.Config;
import survivalgame.map.generation.MapGenerator;

import static org.lwjgl.opengl.ARBDebugOutput.glDebugMessageCallbackARB;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

public class Window {

    private final String windowTitle = Config.getString("window.title", "Survival-Game");
    private final boolean fullscreen = Config.getBoolean("window.fullscreen", false);
    private final boolean highDPI = Config.getBoolean("window.high-dpi", true);
    private final int width = Config.getInt("window.width", 320);
    private final int height = Config.getInt("window.height", 240);
    private final int sampleCount = Config.getInt("opengl.samples", 0);
    private final boolean useSRGB = Config.getBoolean("opengl.srgb", false);
    private final int openGLMajor = Config.getInt("opengl.context-major", 2);
    private final int openGLMinor = Config.getInt("opengl.context-minor", 1);
    private final boolean useDebug = Config.getBoolean("opengl.debug", false);
    private final boolean vsync = Config.getBoolean("opengl.vsync", true);


    private float[] map = new float[512];

    public void start(String[] args) {
        Config.parseCommandLineArguments(args);
        try {
            Display.setTitle(windowTitle);
            Display.setFullscreen(fullscreen);
            Display.setVSyncEnabled(vsync);
            if (!fullscreen) {
                Display.setDisplayMode(new DisplayMode(width, height));
            }
            Display.setResizable(!fullscreen);

            final PixelFormat pixelFormat = new PixelFormat()
                .withBitsPerPixel(Display.getDesktopDisplayMode().getBitsPerPixel())
                .withSamples(sampleCount)
                .withSRGB(useSRGB);

            final ContextAttribs contextAttribs = new ContextAttribs(openGLMajor, openGLMinor)
                .withDebug(useDebug);

            Display.create(pixelFormat, contextAttribs);

            if (useDebug && GLContext.getCapabilities().GL_ARB_debug_output) {
                glDebugMessageCallbackARB(new ARBDebugOutputCallback());
            }

            setMatrices();

            glClearDepth(1);
            glDepthFunc(GL_LEQUAL);
            glEnable(GL_DEPTH_TEST);
            glShadeModel(GL_SMOOTH);
        }
        catch (LWJGLException e) {
            e.printStackTrace();
        }

        Mouse.setGrabbed(true);
        MapGenerator generator = new MapGenerator(512);
        map = generator.generate();
        gameLoop();
        Display.destroy();
    }

    private void setMatrices() {
        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(90f, width / height, 0.1f, 100);
        glMatrixMode(GL_MODELVIEW);
    }

    private void render2() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBegin(GL_QUADS);
        for (int x = 0; x < 512 - 1; x++) {
            for (int z = 0; z < 512 - 1; z++) {
                putVertexAt(x, z);
                putVertexAt(x, z + 1);
                putVertexAt(x + 1, z + 1);
                putVertexAt(x + 1, z);
            }
        }
        glEnd();
    }

    public float getHeightAt(int x, int z) {
        return map[x + z * 512];
    }

    public void putVertexAt(int x, int z) {

        final float y = getHeightAt(x, z);

        if (y > 0.7f) {
            glColor3f(1 - y / 10, 1 - y / 10, 1 - y / 10);
        }
        else {
            glColor3f(0, 0, 1);
        }

        glVertex3f(x, y, z);
    }

    private void gameLoop() {
        Mouse.setCursorPosition(width / 2, height / 2);
        MovementController controller = new MovementController();

        GhostCamera player = new GhostCamera();
        player.setScreen(new Vector2f(width, height));
        player.setNear(0.1f);
        player.setFar(100);
        player.setPosition(new Vector3f(0, 0, 0));

        while (!Display.isCloseRequested()) {
            int dx = Mouse.getDX();
            int dy = Mouse.getDY();

            controller.onMouseMoveWithCanteredMouse(new Vector2f(-dx, 0));
//            controller.onMouseMove(new Vector2f(Mouse.getX(),height / 2));
//            System.out.println(Mouse.getX() + " |-| " +  Mouse.getY());
//            System.out.println(dx + " |+| " +  dy);
            Mouse.setCursorPosition(width / 2, height / 2);
            controller.update();
            player.update(controller);
            player.upload();
//            System.out.println(player.getPosition().toString());
            renderNetz();
            Display.update();
            Display.sync(60);
        }
    }

    private void renderNetz(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBegin(GL_LINES);
        for (int j= -100; j < 100; j++){
            for(int i = -100; i < 100; i++) {
                if(i % 5 == 0) {
                    glColor3f(1,0,0);
                }
                else {
                    glColor3f(0, 0, 0);
                }
                glVertex3f(i,j,-100);
                glVertex3f(i,j,+100);
            }

            for(int i = -100; i < 100; i++) {
                if(i % 5 == 0) {
                    glColor3f(0,0,1);
                }
                else {
                    glColor3f(0, 0, 0);
                }
                glVertex3f(i,-100,j);
                glVertex3f(i,+100,j);
            }
        }
        glEnd();
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glColor3f(0.5f, 0.5f, 0.5f);

        glBegin(GL_QUADS);
        glColor3f(0.5f, 0.5f, 0.5f);
        glVertex3f(0, 0, 0);
        glVertex3f(1, 0, 0);
        glVertex3f(1, 1, 0);
        glVertex3f(0, 1, 0);
        //-----------
        glColor3f(0.4f, 0.1f, 0.4f);
        glVertex3f(5, 5, 0);
        glVertex3f(10, 5, 0);
        glVertex3f(10, 10, 0);
        glVertex3f(5, 10, 0);
        //-----------
        glColor3f(0.1f, 0.1f, 0.1f);
        glVertex3f(5, 5, 0);
        glVertex3f(5, 5, 5);
        glVertex3f(5, 10, 5);
        glVertex3f(5, 10, 0);
        //-----------
        glColor3f(0.5f, 0.9f, 0.7f);
        glVertex3f(5, 5, 5);
        glVertex3f(10, 5, 5);
        glVertex3f(10, 10, 5);
        glVertex3f(5, 10, 5);
        //-----------
        glColor3f(0.1f, 0.3f, 0.2f);
        glVertex3f(10, 5, 0);
        glVertex3f(10, 5, 5);
        glVertex3f(10, 10, 5);
        glVertex3f(10, 10, 0);
        //-----------
        glColor3f(0.4f, 0.2f, 0.8f);
        glVertex3f(5, 10, 0);
        glVertex3f(10, 10, 0);
        glVertex3f(10, 10, 5);
        glVertex3f(5, 10, 5);
        //-----------
        glColor3f(0.3f, 0.5f, 0.1f);
        glVertex3f(5, 5, 0);
        glVertex3f(10, 5, 0);
        glVertex3f(10, 5, 5);
        glVertex3f(5, 5, 5);
        glEnd();
    }

    public static void main(String[] args) {
        Window window = new Window();
        window.start(args);
    }
}
