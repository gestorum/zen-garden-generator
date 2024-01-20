/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package processing.zgg;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import processing.core.PApplet;
import processing.zgg.sketch.ZenGardenSketch;
import processing.zgg.utils.ResourceUtils;

/**
 *
 * @author pbergeron
 */
public class ZenGardenGenerator {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("You must provide at least a class name as argument");
        }

        final String className = args[0];
        try {
            final List<Class> allSketches = ResourceUtils
                    .findClasses(ZenGardenSketch.class.getPackageName(),
                            ZenGardenSketch.class);
            final Class clazz = allSketches.stream()
                    .filter(c -> c.getSimpleName().equalsIgnoreCase(className))
                    .findFirst().orElse(null);
            
            if (clazz != null) {
                PApplet.main(clazz.getCanonicalName(), Arrays.copyOfRange(args, 1, args.length));
            } else {
                throw new IllegalArgumentException(String.format("%s class not found!", className));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Cannot scan sketch classes");
        }
    }
}
