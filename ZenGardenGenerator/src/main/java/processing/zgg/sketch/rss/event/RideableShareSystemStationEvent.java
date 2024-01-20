/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.rss.event;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author pbergeron
 */
@Data
@Builder
public class RideableShareSystemStationEvent implements RideableShareSystemEvent {
    
    private String stationId;
    private RideableShareSystemEventType type;

    @Override
    public RideableShareSystemEventType getType() {
        return type;
    }
}
