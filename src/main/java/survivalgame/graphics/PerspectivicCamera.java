package survivalgame.graphics;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class PerspectivicCamera extends Camera {

    protected float m_Fov;
    protected float m_AspectModifier;

    public PerspectivicCamera() {
        super();
        m_Fov = 75;
        m_AspectModifier = 1;
    }

    @Override
    public void update() {
        if(!m_NeedsUpdate)
            return;
        calcMatrix();
        m_NeedsUpdate = false;
    }

    protected void calcMatrix() {
        CreatePerspectivicMatrix(m_ProjectionMatrix, m_Fov, m_AspectModifier, m_Near, m_Far);
        setRotation(m_ModelViewMatrix, m_Rotation);
        m_ModelViewMatrix.translate(m_Position);
    }

    public void setRotation(Matrix4f matrix, Quaternion rotation) {
        float qw = rotation.getW();
        float qx = rotation.getX();
        float qy = rotation.getY();
        float qz = rotation.getZ();

        float n = (float) (1.0f / Math.sqrt(qx * qx + qy * qy + qz * qz + qw * qw));

        qx *= n;
        qy *= n;
        qz *= n;
        qw *= n;

        matrix.m00 = 1.0f - 2.0f * qy * qy - 2.0f * qz * qz;
        matrix.m10 = 2 * qx * qy - 2 * qz * qw;
        matrix.m20 = 2 * qx * qz + 2 * qy * qw;
        matrix.m30 = 0;

        matrix.m01 = 2.0f * qx * qy + 2.0f * qz * qw;
        matrix.m11 = 1.0f - 2.0f * qx * qx - 2.0f * qz * qz;
        matrix.m21 = 2.0f * qy * qz - 2.0f * qx * qw;
        matrix.m31 = 0;

        matrix.m02 = 2.0f * qx * qz - 2.0f * qy * qw;
        matrix.m12 = 2.0f * qy * qz + 2.0f * qx * qw;
        matrix.m22 = 1.0f - 2.0f * qx * qx - 2.0f * qy * qy;
        matrix.m32 = 0;

        matrix.m03 = 0;
        matrix.m13 = 0;
        matrix.m23 = 0;
        matrix.m33 = 1;
    }

    private float cotan(float v) {
        return (float) Math.tan(Math.PI / 2 - v);
    }

    private void CreatePerspectivicMatrix(Matrix4f out, float fov, float aspect, float near, float far) {
        float f = cotan(Deg2Rad(fov) / 2.0f);

        out.m00 = f / aspect;
        out.m10 = 0;
        out.m20 = 0;
        out.m30 = 0;

        out.m01 = 0;
        out.m11 = f;
        out.m21 = 0;
        out.m31 = 0;

        out.m02 = 0;
        out.m12 = 0;
        out.m22 = (far + near) / (near - far);
        out.m32 = (2.0f * far * near) / (near - far);

        out.m03 = 0;
        out.m13 = 0;
        out.m23 = -1;
        out.m33 = 0;
    }

    protected Vector3f operation(Quaternion quaternion, Vector3f vector) {
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

    private float Deg2Rad(float deg) {
        return (float) (deg * Math.PI / 180.0f);
    }

    public void setFov(float m_Fov) {
        m_NeedsUpdate = true;
        this.m_Fov = m_Fov;
    }

    public float getFov() {
        return m_Fov;
    }

    public void setAspectModifier(float m_AspectModifier) {
        m_NeedsUpdate = true;
        this.m_AspectModifier = m_AspectModifier;
    }

    public float getAspectModifier() {
        return m_AspectModifier;
    }
}
