package survivalgame.graphics;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.util.glu.GLU.gluLookAt;

public class Camera {

    private float horizontalAngle = 90.0f;
    private float verticalAngle = 90.0f;
    private float mouseSpeed = 0.0001f; //0.005f;
    private final float offset = 1.0f;

    private Vector3f position = new Vector3f(Window.mapSize / 2, 30f, 0f);
    private Vector3f forward = new Vector3f(0, -1, 1);
    private Vector3f up = new Vector3f(0, 1, 0);

    public Camera() {
    }

    public void setMatrix() {
        glLoadIdentity();
        final Vector3f posDir = Vector3f.add(position, forward, null);
        gluLookAt(
            position.x, position.y, position.z,
            posDir.x, posDir.y, posDir.z,
            up.x, up.y, up.z
        );
    }

    public void update(float timeDelta) {

        // -- Rotation --

        final int centerX = Display.getWidth() / 2;
        final int centerY = Display.getHeight() / 2;

        final int mouseDeltaX = centerX - Mouse.getX();
        final int mouseDeltaY = centerY - Mouse.getY();

        System.out.println("" + mouseDeltaX + "," + mouseDeltaY);
        if (centerX != Mouse.getX() || centerY != Mouse.getY()) {
            Mouse.setCursorPosition(centerX, centerY);
        }

        horizontalAngle -= mouseSpeed * timeDelta * mouseDeltaX;
        verticalAngle -= mouseSpeed * timeDelta * mouseDeltaY;

        final Vector3f forward = new Vector3f(
            (float) cos(verticalAngle) * (float) sin(horizontalAngle),
            (float) sin(verticalAngle),
            (float) cos(verticalAngle) * (float) cos(horizontalAngle));

        final Vector3f right = new Vector3f(
            (float) sin(horizontalAngle * Math.PI / 2.0f),
            0f,
            (float) cos(horizontalAngle * Math.PI / 2.0f));

        final Vector3f up = Vector3f.cross(right, forward, null);

        this.forward = forward;
        this.up = up;

        // -- Movement --

        /*
        final Vector3f dir = new Vector3f();

        // position.x
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            dir.x -= 1;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            dir.x += 1;
        }

        // position.y
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            dir.y += 1;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            dir.y -= 1;
        }

        // position.z
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            dir.z += 1;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
            dir.z -= 1;
        }

        dir.scale(timeDelta);

        Vector3f.add(position, (Vector3f) right.scale(dir.x), position);
        Vector3f.add(position, (Vector3f)up.scale(dir.y), position);
        Vector3f.add(position, (Vector3f)forward.scale(dir.z), position);

        // reset
        if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
            reset();
        }
        */
    }

    public void reset() {
        position.x = Window.mapSize / 2;
        position.y = 30;
        position.z = 0;
    }
}
