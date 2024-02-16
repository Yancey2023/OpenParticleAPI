package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.color.DataColor;
import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.particle.DataParticleColor;
import yancey.openparticle.api.util.version.Version;

public class ParticleColor extends Particle {

    private final Particle particle;
    private final DataColor color;

    public ParticleColor(Particle particle, DataColor color) {
        this.particle = particle;
        this.color = color;
    }

    @Override
    protected DataParticle run(Version version) {
        return new DataParticleColor(particle.runWithCache(version), color);
    }
}
