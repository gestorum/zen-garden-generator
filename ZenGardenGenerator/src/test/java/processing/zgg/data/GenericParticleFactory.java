/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.data;

import lombok.NonNull;
import processing.core.PVector;

/**
 *
 * @author gestorum
 */
public class GenericParticleFactory {

    private static final float PARTICLE_RADIUS_DEFAULT = 5;
    private static final float PARTICLE_MAX_VELOCITY_MAGNITUDE_DEFAULT = 0.2f;
    private static final float PARTICLE_MAX_FORCE_MAGNITUDE_DEFAULT = 8;

    private GenericParticleFactory() {
    }

    public static GenericParticle build(@NonNull final PVector position) {
        return GenericParticle.builder()
                .position(position)
                .velocity(new PVector(1, 1, 1))
                .acceleration(new PVector())
                .radius(PARTICLE_RADIUS_DEFAULT)
                .personalSpaceRadiusFactor(1)
                .maxVelocityMagnitude(PARTICLE_MAX_VELOCITY_MAGNITUDE_DEFAULT)
                .maxForceMagnitude(PARTICLE_MAX_FORCE_MAGNITUDE_DEFAULT)
                .build();
    }
}
