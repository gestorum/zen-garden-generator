package processing.zgg.particle.data;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author gestorum
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Builder
public class GenericParticle extends AbstractParticle {
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final String id = UUID.randomUUID().toString();
    
    @Override
    public String getId() {
        return id;
    }
    
    private float maxVelocityMagnitude;
    private float maxForceMagnitude;
}
