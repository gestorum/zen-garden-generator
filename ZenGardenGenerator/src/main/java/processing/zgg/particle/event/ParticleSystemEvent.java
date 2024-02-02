/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package processing.zgg.particle.event;

import processing.zgg.particle.data.AbstractParticle;

/**
 *
 * @author gestorum
 */
public interface ParticleSystemEvent {
 
    ParticleSystemEventType getType();
    AbstractParticle getParticle();
}
