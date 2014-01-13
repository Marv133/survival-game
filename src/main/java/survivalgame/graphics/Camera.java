package survivalgame.graphics;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

abstract class Camera {
    private Vector2f m_Screen;
    protected float m_Near;
    protected float m_Far;
    protected Vector3f m_Position;
    protected Quaternion m_Rotation;
    protected boolean m_NeedsUpdate;
    protected Matrix4f m_ProjectionMatrix;
    protected Matrix4f m_ModelViewMatrix;


    public Camera(){
        m_Screen = new Vector2f(1,1);
        m_Near = 0.1f;
        m_Far = 100;
        m_Position = new Vector3f(0,0,2);
        m_Rotation = new Quaternion(0,0,0,1);
        m_NeedsUpdate = true;
    }

    public abstract void update();

//    public void upload()
//    {
//        glMatrixMode(GL_PROJECTION);
//        glLoadMatrix(FloatBuffer.allocate(GL_PROJECTION_MATRIX));
//
//    }

    public Vector2f getScreen() {
        return m_Screen;
    }

    public void setScreen(Vector2f screen) {
        m_NeedsUpdate = true;
        this.m_Screen = screen;
    }

    public float getNear() {
        m_NeedsUpdate = true;
        return m_Near;
    }

    public void setNear(float near) {
        m_NeedsUpdate = true;
        this.m_Near = near;
    }

    public float getFar() {
        return m_Far;
    }

    public void setFar(float far) {
        m_NeedsUpdate = true;
        this.m_Far = far;
    }

    public Vector3f getPosition() {
        return m_Position;
    }

    public void setPosition(Vector3f position) {
        m_NeedsUpdate = true;
        this.m_Position = position;
    }

    public Quaternion getRotation() {
        return m_Rotation;
    }

    public void setRotation(Quaternion rotation) {
        m_NeedsUpdate = true;
        this.m_Rotation = rotation;
    }
}
