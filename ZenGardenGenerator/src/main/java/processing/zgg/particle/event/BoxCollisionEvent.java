package processing.zgg.particle.event;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import processing.zgg.particle.data.AbstractParticle;

/**
 *
 * @author gestorum
 */
@Data
@Builder
public class BoxCollisionEvent implements ParticleSystemEvent {

    public enum Border {
        X_LEFT,
        X_RIGHT,
        Y_TOP,
        Y_BOTTOM,
        Z_NEAREST,
        Z_FARTHEST
    }
    
    private AbstractParticle particle;
    private Set<Border> borders;
    
    @Override
    public ParticleSystemEventType getType() {
        return ParticleSystemEventType.COLLISION;
    }
}
