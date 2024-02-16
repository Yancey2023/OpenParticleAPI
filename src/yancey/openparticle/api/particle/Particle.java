package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.color.DataColor;
import yancey.openparticle.api.common.data.color.DataColorStatic;
import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.vec3.DataVec3;
import yancey.openparticle.api.common.data.vec3.DataVec3Static;
import yancey.openparticle.api.common.math.Vec3;
import yancey.openparticle.api.util.version.Version;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class Particle {

    private boolean isUseCache = false;
    private DataParticle cache;

    protected abstract DataParticle run(Version version);

    public DataParticle runWithCache(Version version) {
        if (isUseCache && cache != null) {
            return cache;
        }
        DataParticle dataParticle = run(version);
        if (isUseCache) {
            cache = dataParticle;
        }
        return dataParticle;
    }

    public Particle useCache(boolean isUseCache) {
        this.isUseCache = isUseCache;
        return this;
    }

    public Particle repeat(int times) {
        List<Particle> children = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            children.add(this);
        }
        return new ParticleCompound(children).useCache(isUseCache);
    }

    public Particle compound(Function<Particle, List<Particle>> function) {
        return compound(function.apply(this));
    }

    public static Particle compound(List<Particle> children) {
        boolean isUseCache = true;
        for (Particle particle : children) {
            if (!particle.isUseCache) {
                isUseCache = false;
                break;
            }
        }
        return new ParticleCompound(children).useCache(isUseCache);
    }

    public static Particle compound(Particle... children) {
        return compound(List.of(children));
    }

    /**
     * 使用这个方法会对每一个经过方法的粒子重新计算，所以支持随机
     */
    public Particle apply(Function<Particle, Particle> function) {
        return new ParticleFunction(this, function).useCache(false);
    }

    public Particle offset(DataVec3 offset) {
        return new ParticleOffset(this, offset).useCache(isUseCache);
    }

    public Particle offset(Vec3 offset) {
        return offset(new DataVec3Static(offset));
    }

    public Particle offset(float x, float y, float z) {
        return offset(new Vec3(x, y, z));
    }

    public Particle rotate(DataVec3 rotate) {
        return new ParticleRotate(this, rotate).useCache(isUseCache);
    }

    public Particle rotate(Vec3 rotate) {
        return rotate(new DataVec3Static(rotate));
    }

    public Particle rotate(float x, float y, float z) {
        return rotate(new Vec3(x, y, z));
    }

    public Particle tick(int tick) {
        return new ParticleTick(this, tick).useCache(isUseCache);
    }

    public Particle color(DataColor color) {
        return new ParticleColor(this, color).useCache(isUseCache);
    }

    public Particle color(Color color) {
        return color(new DataColorStatic(color.getRGB()));
    }

}
