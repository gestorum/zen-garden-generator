/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.tuto;

import processing.zgg.sketch.ZenGardenSketch;

/**
 *
 * @author gestorum
 */
public class ThreeDMotionSketch extends ZenGardenSketch {

    private static final int INITIAL_MAP_WIDTH = 400;
    private static final int INITIAL_MAP_HEIGHT = INITIAL_MAP_WIDTH;

    private float x, y, z;
    private int radius = 1;

    @Override
    public void settings() {
        size(INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT, P3D);
        smooth(8);

        x = width / 2;
        y = height / 2;
        z = 0;
    }
    
    @Override
    protected void drawFrame() {
        translate(x, y, z);

        lights();
        
        noFill();
        stroke(255);
        
        //rotateX(PI/2);
        //rotateZ(-PI/6);
        
        sphere(radius++);
    }
}
