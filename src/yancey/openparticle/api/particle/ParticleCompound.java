package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.particle.DataParticleCompound;
import yancey.openparticle.api.util.version.Version;

import java.util.List;

public class ParticleCompound extends Particle {

    private final List<Particle> children;

    public ParticleCompound(List<Particle> children) {
        this.children = children;
    }

    @Override
    protected DataParticle run(Version version) {
        return new DataParticleCompound(false, children.stream()
                .map(particle -> particle.runWithCache(version))
                .toArray(DataParticle[]::new));
    }
}
