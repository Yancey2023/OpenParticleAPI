package yancey.openparticle.api.particle;

import yancey.openparticle.api.common.data.particle.DataParticle;
import yancey.openparticle.api.common.data.particle.DataParticleSingle;
import yancey.openparticle.api.type.ParticleType;
import yancey.openparticle.api.util.version.Version;

import java.util.function.IntSupplier;

public class ParticleDefault extends Particle {

    private final ParticleType particleType;
    private final IntSupplier age;

    public ParticleDefault(ParticleType particleType, IntSupplier age) {
        this.particleType = particleType;
        this.age = age;
    }

    public ParticleDefault(ParticleType particleType, int age) {
        this(particleType, () -> age);
    }

    @Override
    protected DataParticle run(Version version) {
        return new DataParticleSingle(particleType.getIdentifier(version), age.getAsInt());
    }

}
