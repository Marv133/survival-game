package survivalgame.graphics;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.input.Keyboard.KEY_A;
import static org.lwjgl.input.Keyboard.KEY_D;
import static org.lwjgl.input.Keyboard.KEY_LSHIFT;
import static org.lwjgl.input.Keyboard.KEY_S;
import static org.lwjgl.input.Keyboard.KEY_SPACE;
import static org.lwjgl.input.Keyboard.KEY_W;

public class MovementController {

    private final Vector3f cUp = new Vector3f(0,1,0);
    private final Vector3f cRight = new Vector3f(1,0,0);

    private Vector3f m_Direction;
    private boolean m_MouseCatched;
    private float m_MouseSensitivity;
    private Vector2f m_MouseLastPos;
    private Vector2f m_EulerRotation;
    private Quaternion m_Rotation;

    public MovementController(){
        m_Direction = new Vector3f(0,0,0);
        m_MouseCatched = true;
        m_MouseSensitivity = 0.01f;
        m_MouseLastPos = new Vector2f(Mouse.getX(),Mouse.getY());
        m_EulerRotation = new Vector2f(0,0);
        m_Rotation = new Quaternion(0,0,0,1);
    }

    public void update(){
        m_Direction = new Vector3f(0,0,0);

        if (Keyboard.isKeyDown(KEY_W)){
            m_Direction.z += 1;
        }
        if (Keyboard.isKeyDown(KEY_S)){
            m_Direction.z -= 1;
        }
        if (Keyboard.isKeyDown(KEY_A)){
            m_Direction.x += 1;
        }
        if (Keyboard.isKeyDown(KEY_D)){
            m_Direction.x -= 1;
        }
        if (Keyboard.isKeyDown(KEY_LSHIFT)){
            m_Direction.y += 1;
        }
        if (Keyboard.isKeyDown(KEY_SPACE)){
            m_Direction.y -= 1;
        }

    }

//    public void onKeyAction(int key, int state){
//        if (m_MouseCatched){
//
//        }
//    }

    public void onMouseMove(Vector2f pos){
        if (!m_MouseCatched){
            m_MouseLastPos = pos;
            return;
        }

        Vector2f move = Vector2f.sub(m_MouseLastPos,pos,null);
        move.x *= m_MouseSensitivity;
        move.y *= m_MouseSensitivity;

        m_MouseLastPos = pos;

        m_EulerRotation = Vector2f.add(m_EulerRotation,move,null);

        Quaternion xRotation = new Quaternion(cUp.x,cUp.y,cUp.z,m_EulerRotation.x);
        Quaternion yRotation = new Quaternion(cRight.x,cRight.y,cRight.z,m_EulerRotation.y);

        m_Rotation = Quaternion.mul(xRotation,yRotation,null);
    }

    public void onMouseMoveWithCanteredMouse(Vector2f move){
        move.x *= m_MouseSensitivity;
        move.y *= m_MouseSensitivity;

        m_EulerRotation = Vector2f.add(m_EulerRotation,move,m_EulerRotation);

        Quaternion xRotation = new Quaternion(cUp.x,cUp.y,cUp.z,m_EulerRotation.x);
        Quaternion yRotation = new Quaternion(cRight.x,cRight.y,cRight.z,m_EulerRotation.y);

        m_Rotation = Quaternion.mul(xRotation,yRotation,null);
        System.out.println(m_Rotation.toString());
    }

    public Vector3f getDirection() {
        return m_Direction;
    }

    public Quaternion getRotation() {
        return m_Rotation;
    }
}
