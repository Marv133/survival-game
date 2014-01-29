package survivalgame.graphics;

import org.lwjgl.util.vector.Vector3f;

public class PlayerCamera extends PerspectivicCamera {

    private final float[] map;
    private long lastUpdate;
    private Vector3f velocity;
    private float m_MovementMaxSpeed;
    private float m_MovementAcceleration;
    private float m_MovementFriction;

    public PlayerCamera(float[] map){
        this.lastUpdate = System.currentTimeMillis();
        this.velocity = new Vector3f(0,0,0);
        this.map = map;
        this.m_MovementMaxSpeed = 999;
        this.m_MovementAcceleration = 6.0f;
        this.m_MovementFriction = 0.994f;
    }

    public void update(MovementController controller){
        long now = System.currentTimeMillis();
        float time = (float)(now - lastUpdate)/1000.0f;
        lastUpdate = now;

        Vector3f op = operation(controller.getRotation(), controller.getDirection());
        op.x *= m_MovementAcceleration * time;
        op.y *= m_MovementAcceleration * time;
        op.z *= m_MovementAcceleration * time;

        velocity = Vector3f.add(velocity,op,null);

        if (velocity.length() > m_MovementMaxSpeed) {
            float value = m_MovementMaxSpeed / velocity.length();
            velocity.x *= value;
            velocity.y *= value;
            velocity.z *= value;
        }

        float vX = velocity.getX() * time;
        float vY = velocity.getY() * time;
        float vZ = velocity.getZ() * time;

        Vector3f.add(m_Position, new Vector3f(vX, vY, vZ), m_Position);



        velocity.x *= 1.0f - time * m_MovementFriction;
        velocity.y *= 1.0f - time * m_MovementFriction;
        velocity.z *= 1.0f - time * m_MovementFriction;
    }

}
