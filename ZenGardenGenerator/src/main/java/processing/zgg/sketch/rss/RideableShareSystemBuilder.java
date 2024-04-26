package processing.zgg.sketch.rss;

import java.io.IOException;

/**
 *
 * @author gestorum
 */
public interface RideableShareSystemBuilder {
    
    RideableShareSystem build(String name) throws IOException;
}
