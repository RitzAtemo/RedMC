package red.aviora.redmc.cosmetics.model;

import java.util.Arrays;
import java.util.Optional;

public enum CosmeticSlot {
    TRAIL,
    HEAD,
    BACK,
    FEET,
    ORBIT,
    AURA,
    WINGS,
    CROWN,
    HALO,
    SHOULDER_LEFT,
    SHOULDER_RIGHT;

    public static Optional<CosmeticSlot> fromString(String name) {
        return Arrays.stream(values())
            .filter(s -> s.name().equalsIgnoreCase(name))
            .findFirst();
    }

    public static String allNames() {
        return String.join(", ", Arrays.stream(values()).map(Enum::name).toList());
    }
}
