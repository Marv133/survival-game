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

    private Vector3f operation(Quaternion quaternion, Vector3f vector) {
        Vector3f r = new Vector3f();
        float a00 = quaternion.w * quaternion.w;
        float a01 = quaternion.w * quaternion.x;
        float a02 = quaternion.w * quaternion.y;
        float a03 = quaternion.w * quaternion.z;
        float a11 = quaternion.x * quaternion.x;
        float a12 = quaternion.x * quaternion.y;
        float a13 = quaternion.x * quaternion.z;
        float a22 = quaternion.y * quaternion.y;
        float a23 = quaternion.y * quaternion.z;
        float a33 = quaternion.z * quaternion.z;
        r.x = vector.x * (+a00 + a11 - a22 - a33) + 2 * (a12 * vector.y + a13 * vector.z + a02 * vector.z - a03 * vector.y);
        r.y = vector.y * (+a00 - a11 + a22 - a33) + 2 * (a12 * vector.x + a23 * vector.z + a03 * vector.x - a01 * vector.z);
        r.z = vector.z * (+a00 - a11 - a22 + a33) + 2 * (a13 * vector.x + a23 * vector.y - a02 * vector.x + a01 * vector.y);
        return r;
    }
}
