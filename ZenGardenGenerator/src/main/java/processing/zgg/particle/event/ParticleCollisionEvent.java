/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
