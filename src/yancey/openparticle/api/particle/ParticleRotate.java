package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.particle.DataParticleRotate;
import yancey.openparticle.api.common.data.vec3.DataVec3;
import yancey.openparticle.api.util.version.Version;

public class ParticleRotate extends Particle {

    private final Particle particle;
    private final DataVec3 rotate;

    public ParticleRotate(Particle particle, DataVec3 rotate) {
        this.particle = particle;
        this.rotate = rotate;
    }

    @Override
    protected DataParticle run(Version version) {
        return new DataParticleRotate(particle.runWithCache(version), rotate);
    }
}
