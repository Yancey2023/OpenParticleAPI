package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.particle.DataParticleTick;
import yancey.openparticle.api.util.version.Version;

public class ParticleTick extends Particle {

    private final Particle particle;
    private final int tick;

    public ParticleTick(Particle particle, int tick) {
        this.particle = particle;
        this.tick = tick;
    }

    @Override
    protected DataParticle run(Version version) {
        return new DataParticleTick(particle.runWithCache(version), tick);
    }
}
