package processing.zgg.particle.data;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private String id = UUID.randomUUID().toString();
    
    private float maxVelocityMagnitude;
    private float maxForceMagnitude;
}
