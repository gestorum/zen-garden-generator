/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import processing.core.PVector;

/**
 *
 * @author gestorum
 */
public class MotionUtils {

    private MotionUtils() {
    }

    // ease in and out, time = [0,1], with a parameter g:
    // https://patakk.tumblr.com/post/88602945835/heres-a-simple-function-you-can-use-for-easing
    public static float ease(final float time, final float g) {
        final float ease;

        if (time < 0.5) {
            ease = (float) (0.5 * Math.pow(2 * time, g));
        } else {
            ease = (float) (1 - 0.5 * Math.pow(2 * (1 - time), g));
        }

        return ease;
    }

    // easing function taken from https://easings.net/#easeOutElastic, slightly modified
    public static float easeOutElastic(final float time) {
        final float c4 = (float) (2 * Math.PI) / 3;

        if (time <= 0) {
            return 0;
        }

        if (time >= 1) {
            return 1;
        }

        return (float) (Math.pow(2, -10 * time) * Math.sin((time * 10 - 0.75) * c4)) + 1;
    }

    public static List<PVector> interpolateVectors(@NonNull final List<PVector> vectors,
            final int maxSamples) {

        if (maxSamples <= 0) {
            return vectors;
        }

        final List<PVector> allVectors = new ArrayList<>();
        for (int i = 0; i < vectors.size(); i++) {
            final int nextIndex = i + 1;
            if (nextIndex >= vectors.size()) {
                break;
            }

            final PVector p1 = vectors.get(i);
            allVectors.add(p1);

            final PVector p2 = vectors.get(nextIndex);

            final int diff = (int) Math.min(Math.max(Math.max(Math.abs(p1.x - p2.x),
                    Math.abs(p1.y - p2.y)), Math.abs(p1.z - p2.z)), maxSamples);
            if (diff > 0) {
                if (diff > 1) {
                    final List<PVector> newVectors = new ArrayList<>();
                    PVector previousVector = p1;
                    for (int s = 0; s < diff; s++) {
                        final PVector newVector = PVector.lerp(p1, p2, (float) s / diff);

                        // No needs to keep it same as previous
                        if (!newVector.equals(previousVector)) {
                            newVectors.add(newVector);
                            previousVector = newVector;
                        }
                    }

                    allVectors.addAll(newVectors);
                }

                allVectors.add(p2);
            }
        }

        return allVectors;
    }
    
    public static List<Point> interpolatePoints(@NonNull final List<Point> points,
            final int maxSamples) {
        final List<PVector> vectors = points.stream()
                .map(p -> new PVector((float) p.getX(), (float) p.getY())).toList();
        final List<PVector> allVectors = interpolateVectors(vectors, maxSamples);
        
        return allVectors.stream().map(v -> new Point((int) v.x, (int) v.y)).toList();
    }

    public static float map(float value, float start1, float stop1,
            float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }
    
    private static int lerp(final double a, final double b, final float time) {
        return (int) ((1 - time) * a + time * b);
    }
}
