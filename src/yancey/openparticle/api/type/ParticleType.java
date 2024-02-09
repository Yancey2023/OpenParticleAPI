package yancey.openparticle.api.type;

import yancey.openparticle.api.common.data.identifier.Identifier;
import yancey.openparticle.api.common.data.identifier.IdentifierCache;
import yancey.openparticle.api.particle.Particle;
import yancey.openparticle.api.particle.ParticleDefault;
import yancey.openparticle.api.util.version.Version;

import java.util.Map;
import java.util.function.IntSupplier;

public class ParticleType {

    public static final ParticleType END_ROD = new ParticleType(Map.of(
            Version.Java1_12, new Identifier("endRod"),
            Version.Java1_16, new Identifier("end_rod")
    ));

    private final Map<Version, Identifier> identifierMap;

    public ParticleType(Map<Version, Identifier> identifierMap) {
        this.identifierMap = identifierMap;
    }

    public Identifier getIdentifier(Version version) {
        Identifier identifier = identifierMap.get(version);
        IdentifierCache.add(identifier);
        return identifier;
    }

    public void addVersion(Version version, Identifier name) {
        identifierMap.put(version, name);
    }

    public Particle createParticle(IntSupplier age) {
        return new ParticleDefault(this, age);
    }

    public Particle createParticle(int age) {
        return new ParticleDefault(this, age).useCache();
    }
}
