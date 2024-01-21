/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package processing.zgg.sketch.rss;

import java.io.IOException;

/**
 *
 * @author gestorum
 */
public interface RideableShareSystemBuilder {
    
    RideableShareSystem build(String name) throws IOException;
}
