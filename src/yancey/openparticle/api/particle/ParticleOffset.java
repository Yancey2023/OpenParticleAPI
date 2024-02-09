package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.particle.DataParticleOffset;
import yancey.openparticle.api.common.data.vec3.DataVec3;
import yancey.openparticle.api.util.version.Version;

import java.util.List;
import java.util.function.Supplier;

public class ParticleOffset extends Particle {

    private final Particle particle;
    private final Supplier<DataVec3> offset;

    public ParticleOffset(Particle particle, Supplier<DataVec3> offset) {
        this.particle = particle;
        this.offset = offset;
    }

    public ParticleOffset(Particle particle, DataVec3 offset) {
        this(particle, () -> offset);
    }

    protected List<Particle> getChildren() {
        return List.of(particle);
    }

    @Override
    protected DataParticle run(Version version) {
        return new DataParticleOffset(particle.execute0(version), offset.get());
    }
}
