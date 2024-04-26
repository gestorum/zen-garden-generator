package processing.zgg.sketch.rss.event;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author gestorum
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
