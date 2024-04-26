/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.tuto.collision;

import java.awt.Color;
import java.util.Set;
import lombok.NonNull;
import processing.core.PVector;
import processing.event.MouseEvent;
import processing.zgg.audio.ZenGardenSoundGenerator;
import processing.zgg.particle.data.AbstractParticle;
import processing.zgg.particle.event.BoxCollisionEvent;
import processing.zgg.particle.event.ParticleCollisionEvent;
import processing.zgg.particle.event.ParticleSystemEvent;
import processing.zgg.particle.event.ParticleSystemEventListener;
import processing.zgg.sketch.ZenGardenSketch;

/**
 *
 * @author gestorum
 */
public class SphereCollisionSketch extends ZenGardenSketch
        implements ParticleSystemEventListener {

    private static final int INITIAL_MAP_WIDTH = 800;
    private static final int INITIAL_MAP_HEIGHT = INITIAL_MAP_WIDTH;

    private static final int FRAME_RATE = 24;
    
    private static final float SCENE_ROTATION_STEP = 0.2f;
    
    private static final Color BIG_BOX_STROKE_COLOR = Color.DARK_GRAY;
    
    private static final Color SPHERE_FILL_COLOR = Color.WHITE;
    private static final Color SPHERE_STROKE_COLOR = Color.GREEN;
    
    private static final Color DIRECTIONAL_LIGHT_COLOR = Color.ORANGE;
    
    private float x, y, z;
    
    private SphereCollisionParticleSystem boxRestrictedParticleSystem;
    
    private int bigBoxDimension;
    private PVector sceneRotation;
    private PVector directionalLightDirection;
    private int sphereSpeedUpFactor = 1;

    @Override
    public void settings() {
        size(INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT, P3D);
        
        initSoundGenerator();
        
        this.boxRestrictedParticleSystem = new SphereCollisionParticleSystem();
        this.boxRestrictedParticleSystem.addEventListener(this);
    }

    @Override
    public void setup() {
        super.setup();

        frameRate(FRAME_RATE);

        surface.setResizable(true);

        windowResized();
    }

    @Override
    protected void drawFrame() {
        final float t = ((frameCount % FRAME_RATE) / (float) FRAME_RATE);

        translate(x, y, z);
        
        rotateX(sceneRotation.x);
        rotateY(sceneRotation.y);
        rotateZ(sceneRotation.z);

        push();
        noFill();
        stroke(BIG_BOX_STROKE_COLOR.getRGB());
        strokeWeight(1);
        box(bigBoxDimension);
        pop();

        boxRestrictedParticleSystem.update();
        
        boxRestrictedParticleSystem.getParticles().forEach(p -> {
            final PVector position = p.getPosition();
            
            push();
            translate(position.x, position.y, position.z);
            
            if (t > 0 && p.getVelocity().mag() != 0) {
                final float signum = Math.signum(p.getVelocity().heading()) * -1;
                rotateX(signum * PI / t);
            }

            rotateZ(-PI / 6);

            stroke(SPHERE_STROKE_COLOR.getRGB());
            fill(SPHERE_FILL_COLOR.getRGB());
            
            directionalLight(DIRECTIONAL_LIGHT_COLOR.getRed(),
                    DIRECTIONAL_LIGHT_COLOR.getGreen(),
                    DIRECTIONAL_LIGHT_COLOR.getBlue(),
                    directionalLightDirection.x,
                    directionalLightDirection.y,
                    directionalLightDirection.z);
            
            sphere(p.getRadius());
            pop();
        });
    }
    
    @Override
    public void keyPressed() {
        super.keyPressed();
        
        switch (keyCode) {
            case UP -> rotateScene(X, SCENE_ROTATION_STEP);
            case RIGHT -> rotateScene(Y, SCENE_ROTATION_STEP);
            case DOWN -> rotateScene(X, SCENE_ROTATION_STEP * -1);
            case LEFT -> rotateScene(Y, SCENE_ROTATION_STEP * -1);
            
            case 139 -> { // Num +
                sphereSpeedUpFactor++;
                boxRestrictedParticleSystem.setSpeedUpFactor(sphereSpeedUpFactor);
            }

            case 140 -> { // Num -
                sphereSpeedUpFactor = Math.max(sphereSpeedUpFactor - 1, 1);
                boxRestrictedParticleSystem.setSpeedUpFactor(sphereSpeedUpFactor);
            }
        }

        switch (Character.toLowerCase(key)) {
            case 'g' -> boxRestrictedParticleSystem.generateParticules();
        }
    }

    @Override
    public void mouseWheel(final MouseEvent event) {
        super.mouseWheel(event);
        
        rotateScene(X, SCENE_ROTATION_STEP * Math.signum(event.getCount()) * -1);
    }
    
    @Override
    public void windowResized() {
        super.windowResized();
        
        x = width / 2;
        y = height / 2;
        z = 0;
        
        this.bigBoxDimension = (int)Math.floor((x + y) / 2);
        boxRestrictedParticleSystem.init(this.bigBoxDimension,
                this.bigBoxDimension, this.bigBoxDimension);
        boxRestrictedParticleSystem.generateParticules();
        
        sceneRotation = new PVector(-PI / 6, PI / 3);
        directionalLightDirection = new PVector(0, -1, 0);
    }

    @Override
    public void processEvent(@NonNull ParticleSystemEvent event) {
        
        if (BoxCollisionEvent.class.isAssignableFrom(event.getClass())) {
            final BoxCollisionEvent boxCollisionEvent = (BoxCollisionEvent)event;
            final Set<BoxCollisionEvent.Border> borders = boxCollisionEvent.getBorders();
            borders.forEach(b -> playHitBorderSound(0));
        } else if (ParticleCollisionEvent.class.isAssignableFrom(event.getClass())) {
            final ParticleCollisionEvent particleCollisionEvent = (ParticleCollisionEvent)event;
            final Set<AbstractParticle> otherParticles = particleCollisionEvent.getOtherParticles();
            otherParticles.forEach(op -> playSphereCollisionSound(0));
        }
    }
    
    private void rotateScene(final int axis, final float value) {
        final float valueSignum = Math.signum(value);
        final float maxPI = PI * valueSignum;
        final float maxLightDirection = 1f * valueSignum;
        
        switch (axis) {
            case X -> {
                sceneRotation.x = (sceneRotation.x + value) % maxPI;
                directionalLightDirection.x += value % maxLightDirection;
            }

            case Y -> {
                sceneRotation.y = (sceneRotation.y + value) % maxPI;
                directionalLightDirection.y += value % maxLightDirection;
            }

            case Z -> {
                sceneRotation.z = (sceneRotation.z + value) % maxPI;
                directionalLightDirection.z += value % maxLightDirection;
            }
        }
    }
    
    private void playHitBorderSound(final float pan) {
        playFreq(ZenGardenSoundGenerator.Instrument.DRUM_WOOD,
                ZenGardenSoundGenerator.Amplitude.MID.getValue(),
                440, 1f, pan);
    }
    
    private void playSphereCollisionSound(final float pan) {
        playFreq(ZenGardenSoundGenerator.Instrument.WHITE_WAVE,
                ZenGardenSoundGenerator.Amplitude.MID.getValue(),
                440, 1f, pan);
    }
}
