package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.particle.DataParticleTick;
import yancey.openparticle.api.common.data.vec3.DataVec3;
import yancey.openparticle.api.util.version.Version;

import java.util.List;
import java.util.function.IntSupplier;

public class ParticleTick extends Particle {

    private final Particle particle;
    private final IntSupplier tick;

    public ParticleTick(Particle particle, IntSupplier tick) {
        this.particle = particle;
        this.tick = tick;
    }

    public ParticleTick(Particle particle, int tick) {
        this.particle = particle;
        this.tick = () -> tick;
    }

    protected List<Particle> getChildren() {
        return List.of(particle);
    }

    @Override
    protected DataParticle run(Version version) {
        return new DataParticleTick(particle.execute0(version), tick.getAsInt());
    }
}
