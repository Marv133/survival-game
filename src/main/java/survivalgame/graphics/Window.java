package survivalgame.graphics;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBDebugOutputCallback;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Quaternion;
import survivalgame.Config;

import static org.lwjgl.opengl.ARBDebugOutput.glDebugMessageCallbackARB;
import static org.lwjgl.opengl.GL11.*;
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

            final ContextAttribs contextAttribs = new ContextAttribs(openGLMajor,openGLMinor)
                .withDebug(useDebug);

            Display.create(pixelFormat,contextAttribs);

            if(useDebug && GLContext.getCapabilities().GL_ARB_debug_output) {
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

        gameLoop();
        Display.destroy();
    }

    private void setMatrices() {
        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(90f, width / height, -1.0f, 100);
        glMatrixMode(GL_MODELVIEW);
    }

    private void gameLoop() {
        while (!Display.isCloseRequested()){
            render();
            Display.update();
            Display.sync(60);
        }
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glColor3f(0.5f,0.5f, 0.5f);

        glBegin(GL_QUADS);
        glVertex3f(0,0,0);
        glVertex3f(1,0,0);
        glVertex3f(1,1,0);
        glVertex3f(0,1,0);

        glVertex3f(50,50,0);
        glVertex3f(100,50,0);
        glVertex3f(100,100,0);
        glVertex3f(50,100,0);
        //-----------
        glVertex3f(50,50,0);
        glVertex3f(50,50,50);
        glVertex3f(50,100,50);
        glVertex3f(50,100,0);
        //-----------
        glVertex3f(50,50,50);
        glVertex3f(100,50,50);
        glVertex3f(100,100,50);
        glVertex3f(50,100,50);
        //-----------
        glVertex3f(100,50,0);
        glVertex3f(100,50,50);
        glVertex3f(100,100,50);
        glVertex3f(100,100,0);
        //-----------
        glVertex3f(50,100,0);
        glVertex3f(100,100,0);
        glVertex3f(100,100,50);
        glVertex3f(50,100,50);
        //-----------
        glVertex3f(50,50,0);
        glVertex3f(100,50,0);
        glVertex3f(100,50,50);
        glVertex3f(50,50,50);
        glEnd();
    }

    public static void main(String[] args){
        Window window = new Window();
        window.start(args);
    }
}
