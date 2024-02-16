package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.particle.DataParticleOffset;
import yancey.openparticle.api.common.data.vec3.DataVec3;
import yancey.openparticle.api.util.version.Version;

public class ParticleOffset extends Particle {

    private final Particle particle;
    private final DataVec3 offset;

    public ParticleOffset(Particle particle, DataVec3 offset) {
        this.particle = particle;
        this.offset = offset;
    }

    @Override
    protected DataParticle run(Version version) {
        return new DataParticleOffset(particle.runWithCache(version), offset);
    }
}
