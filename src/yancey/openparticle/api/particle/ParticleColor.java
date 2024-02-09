package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.color.DataColor;
import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.particle.DataParticleColor;
import yancey.openparticle.api.common.data.particle.DataParticleOffset;
import yancey.openparticle.api.common.data.vec3.DataVec3;
import yancey.openparticle.api.util.version.Version;

import java.util.List;
import java.util.function.Supplier;

public class ParticleColor extends Particle {

    private final Particle particle;
    private final Supplier<DataColor> color;

    public ParticleColor(Particle particle, Supplier<DataColor> color) {
        this.particle = particle;
        this.color = color;
    }

    public ParticleColor(Particle particle, DataColor offset) {
        this(particle, () -> offset);
    }

    protected List<Particle> getChildren() {
        return List.of(particle);
    }

    @Override
    protected DataParticle run(Version version) {
        return new DataParticleColor(particle.execute0(version), color.get());
    }
}
