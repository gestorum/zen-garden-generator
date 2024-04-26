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
public class ParticleCollisionEvent implements ParticleSystemEvent {

    private AbstractParticle particle;
    private Set<AbstractParticle> otherParticles;
    
    @Override
    public ParticleSystemEventType getType() {
        return ParticleSystemEventType.COLLISION;
    }
}
