/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.particle.data;

import java.util.Optional;
import lombok.NonNull;
import processing.core.PVector;
import processing.zgg.data.BinaryTree;

/**
 * Binary tree implementation for abstract particles.
 * 
 * @author gestorum
 */
public class ParticleTree extends BinaryTree<AbstractParticle> {
    
    @Override
    protected float[] getPointFromValue(@NonNull AbstractParticle value) {
        return Optional.ofNullable(value.getPosition())
                .orElse(new PVector()).array();
    }
}