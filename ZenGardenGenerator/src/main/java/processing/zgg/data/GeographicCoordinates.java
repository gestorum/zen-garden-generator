package processing.zgg.data;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author gestorum
 */
@Data
@Builder
public class GeographicCoordinates {

    private Double latitude;
    private Double longitude;
    
    public boolean isFilled() {
        return latitude != null && longitude != null;
    }
}
