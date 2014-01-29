package survivalgame.graphics;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class GhostCamera extends PerspectivicCamera {

    private long lastUpdate;
    private Vector3f velocity;
    private float movementMaxSpeed;
    private float movementAcceleration;
    private float movementFriction;

    public GhostCamera() {
        lastUpdate = System.currentTimeMillis(); //todo durch eine globale klasse ersetzen
        velocity = new Vector3f(0, 0, 0);

        movementMaxSpeed = 999;
        movementAcceleration = 20.0f;
        movementFriction = 0.99f;
    }

    public void update(MovementController controller) {
        long now = System.currentTimeMillis();
        float time = (float)(now - lastUpdate)/1000.0f;
        lastUpdate = now;

        Vector3f op = operation(controller.getRotation(), controller.getDirection());
        op.x *= movementAcceleration * time;
        op.y *= movementAcceleration * time;
        op.z *= movementAcceleration * time;

        velocity = Vector3f.add(velocity,op,null);

        if (velocity.length() > movementMaxSpeed) {
            float value = movementMaxSpeed / velocity.length();
            velocity.x *= value;
            velocity.y *= value;
            velocity.z *= value;
        }

        float vX = velocity.getX() * time;
        float vY = velocity.getY() * time;
        float vZ = velocity.getZ() * time;

        Vector3f.add(m_Position, new Vector3f(vX, vY, vZ), m_Position);
        velocity.x *= 1.0f - time * movementFriction;
        velocity.y *= 1.0f - time * movementFriction;
        velocity.z *= 1.0f - time * movementFriction;

        m_Rotation = controller.getRotation();

        m_NeedsUpdate = true;
        super.update();
    }
}
