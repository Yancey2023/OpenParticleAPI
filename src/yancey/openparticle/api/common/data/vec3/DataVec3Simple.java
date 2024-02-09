package yancey.openparticle.api.common.data.vec3;

import yancey.openparticle.api.common.util.Pair;
import yancey.openparticle.api.common.math.Vec3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataVec3Simple extends DataVec3 {

    private final Vec3 position, speed;
    private final float g, f;

    public DataVec3Simple(Vec3 position, Vec3 speed, float g, float f) {
        this.position = position;
        this.speed = speed;
        this.g = g;
        this.f = f;
    }

    public DataVec3Simple(DataInputStream dataInputStream) throws IOException {
        this.position = new Vec3(dataInputStream);
        this.speed = new Vec3(dataInputStream);
        this.g = dataInputStream.readFloat();
        this.f = dataInputStream.readFloat();
    }

    protected byte getType() {
        return SIMPLE;
    }

    @Override
    public void writeToFile(DataOutputStream dataOutputStream) throws IOException {
        super.writeToFile(dataOutputStream);
        position.writeToFile(dataOutputStream);
        speed.writeToFile(dataOutputStream);
        dataOutputStream.writeFloat(g);
        dataOutputStream.writeFloat(f);
    }

    /**
     * 左边位置，右边速度
     */
    private final List<Pair<Vec3, Vec3>> cache = new ArrayList<>();

    @Override
    public Vec3 getVec3(int tick, int age) {
        int lastMaxTick = cache.size() - 1;
        if (lastMaxTick > tick) {
            return cache.get(tick).first;
        }
        Pair<Vec3, Vec3> lastPair = lastMaxTick == -1 ? new Pair<>(position, speed) : cache.get(lastMaxTick);
        float x = lastPair.first.x;
        float y = lastPair.first.y;
        float z = lastPair.first.z;
        float vx = lastPair.second.x;
        float vy = lastPair.second.y;
        float vz = lastPair.second.z;
        for (int i = lastMaxTick + 1; i <= tick; i++) {
            x += vx;
            y += vy;
            z += vz;
            vx *= f;
            vy = vy * f - g;
            vz *= f;
            cache.add(new Pair<>(new Vec3(x, y, z), new Vec3(vx, vy, vz)));
        }
        return cache.get(tick).first;
    }
}
