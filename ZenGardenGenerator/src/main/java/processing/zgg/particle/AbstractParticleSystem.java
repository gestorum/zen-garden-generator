/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.particle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import processing.zgg.particle.data.AbstractParticle;
import processing.zgg.particle.event.ParticleSystemEvent;
import processing.zgg.particle.event.ParticleSystemEventListener;

/**
 *
 * @author gestorum
 */
public abstract class AbstractParticleSystem<T> implements ParticleSystem<T> {
    
    protected T system;
    protected int width;
    protected int height;
    protected int depth;
    
    private int speedUpFactor;
    
    private Set<ParticleSystemEventListener> eventListeners = new HashSet<>();

    public AbstractParticleSystem(final T system) {
        this.system = system;
    }

    @Override    
    public int getSpeedUpFactor() {
        return this.speedUpFactor;
    }
    
    @Override    
    public void setSpeedUpFactor(final int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Cannot set a negative factor!");
        }
                
        this.speedUpFactor = factor;
    }
    
    @Override
    public void addEventListener(@NonNull final ParticleSystemEventListener eventListener) {
        eventListeners.add(eventListener);
    }
    
    @Override
    public void removeEventListener(@NonNull final ParticleSystemEventListener eventListener) {
        eventListeners.remove(eventListener);
    }
    
    protected void publishEvent(@NonNull ParticleSystemEvent event) {
        eventListeners.forEach(l -> l.processEvent(event));
    }
    
    protected void purgeDeadParticles(@NonNull final Collection<? extends AbstractParticle> particles) {
        particles.removeAll(particles.stream().filter(AbstractParticle::isDead).toList());
    }
}
