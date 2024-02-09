package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.util.version.Version;

import java.util.List;
import java.util.function.Function;

public class ParticleFunction extends Particle {

    private final Particle particle;
    private final Function<Particle, Particle> function;

    public ParticleFunction(Particle particle, Function<Particle, Particle> function) {
        this.particle = particle;
        this.function = function;
    }

    protected List<Particle> getChildren() {
        return List.of(particle);
    }

    @Override
    protected DataParticle run(Version version) {
        return function.apply(particle).run(version);
    }
}
