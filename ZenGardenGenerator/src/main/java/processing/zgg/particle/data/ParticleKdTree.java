/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.particle.data;

import java.util.Optional;
import lombok.NonNull;
import processing.core.PVector;
import processing.zgg.data.KdTree;

/**
 * K-d tree implementation for abstract particles.
 * 
 * @author gestorum
 */
public class ParticleKdTree extends KdTree<AbstractParticle> {
    
    public ParticleKdTree() {
        super(3);
    }
    
    @Override
    protected float[] getPointFromValue(@NonNull AbstractParticle value) {
        return Optional.ofNullable(value.getPosition())
                .orElse(new PVector()).array();
    }
}