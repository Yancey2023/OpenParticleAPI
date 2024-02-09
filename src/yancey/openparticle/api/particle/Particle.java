package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.vec3.DataVec3;
import yancey.openparticle.api.common.data.vec3.DataVec3Static;
import yancey.openparticle.api.common.math.Vec3;
import yancey.openparticle.api.util.version.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Particle {

    /**
     * 因为有些数据是随机产生的，使用缓存会减少随机性
     */
    private boolean isUseCache = false;
    private DataParticle cache;

    protected abstract DataParticle run(Version version);

    protected abstract List<Particle> getChildren();

    public DataParticle execute0(Version version) {
        if (isUseCache && cache != null) {
            return cache;
        }
        DataParticle dataParticle = run(version);
        if (isUseCache) {
            cache = dataParticle;
        }
        return dataParticle;
    }

    public Particle useCache() {
        isUseCache = true;
        return this;
    }

    public Particle repeat(int times) {
        List<Particle> children = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            children.add(this);
        }
        return compound(children);
    }

    public Particle compound(Function<Particle, List<Particle>> function) {
        return compound(function.apply(this));
    }

    public static Particle compound(List<Particle> children) {
        return new ParticleCompound(children);
    }

    public static Particle compound(Particle... children) {
        return compound(List.of(children));
    }

    public Particle apply(Function<Particle, Particle> function) {
        return new ParticleFunction(this, function);
    }

    public Particle offset(Supplier<DataVec3> offset) {
        return new ParticleOffset(this, offset);
    }

    public Particle offset(DataVec3 offset) {
        return new ParticleOffset(this, offset);
    }

    public Particle offsetStatic(Supplier<Vec3> offset) {
        return offset(() -> new DataVec3Static(offset.get()));
    }

    public Particle offsetStatic(Vec3 offset) {
        return offset(new DataVec3Static(offset));
    }

    public Particle offsetStatic(float x, float y, float z) {
        return offsetStatic(new Vec3(x, y, z));
    }

    public Particle rotate(DataVec3 rotate) {
        return new ParticleRotate(this, rotate);
    }

    public Particle rotate(Vec3 rotate) {
        return rotate(new DataVec3Static(rotate));
    }

    public Particle rotate(float x, float y, float z) {
        return rotate(new Vec3(x, y, z));
    }

    public Particle tick(int tick) {
        return new ParticleTick(this, tick);
    }

}
