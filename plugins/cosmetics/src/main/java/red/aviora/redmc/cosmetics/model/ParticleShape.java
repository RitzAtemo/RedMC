package red.aviora.redmc.cosmetics.model;

import java.util.Arrays;
import java.util.Optional;

public enum ParticleShape {
    POINT,
    RING,
    SPHERE,
    SPIRAL,
    DOUBLE_HELIX,
    STAR,
    WINGS_SHAPE,
    CROWN_SHAPE,
    HALO_SHAPE,
    RANDOM;

    public static Optional<ParticleShape> fromString(String name) {
        return Arrays.stream(values())
            .filter(s -> s.name().equalsIgnoreCase(name))
            .findFirst();
    }

    public static String allNames() {
        return String.join(", ", Arrays.stream(values()).map(Enum::name).toList());
    }
}
