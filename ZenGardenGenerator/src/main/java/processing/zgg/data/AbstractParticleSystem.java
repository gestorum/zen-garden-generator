/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.data;

import java.util.Collection;
import lombok.NonNull;

/**
 *
 * @author pbergeron
 */
public abstract class AbstractParticleSystem<T> implements ParticleSystem<T> {
    
    protected T system;
    protected int width;
    protected int height;
    
    private int speedUpFactor;

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
    
    protected void purgeDeadParticles(@NonNull final Collection<? extends Particle> particles) {
        particles.removeAll(particles.stream().filter(Particle::isDead).toList());
    }
}