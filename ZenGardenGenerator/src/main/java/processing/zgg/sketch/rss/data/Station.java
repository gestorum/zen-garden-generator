/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.rss.data;

import processing.zgg.data.GeographicCoordinates;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author gestorum
 */
@Data
@Builder
public class Station {

    private String id;
    private int capacity;
    private int availableRideables;
    private int availableDocks;
    private GeographicCoordinates geographicCoordinates;
    private boolean isInstalled;
}
