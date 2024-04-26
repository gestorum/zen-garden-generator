package processing.zgg.sketch.rss.data;

import processing.zgg.data.GeographicCoordinates;
import lombok.Data;
import lombok.NonNull;

/**
 *
 * @author gestorum
 */
@Data
public class OperationArea {
    
    private Double minLatitude;
    private Double maxLatitude;
    private Double minLongitude;
    private Double maxLongitude;
    
    public void setFromGeographicCoordinates(@NonNull final GeographicCoordinates geographicCoordinates) {
        if (!geographicCoordinates.isFilled()) {
            return;
        }
        
        if (minLatitude == null || geographicCoordinates.getLatitude() < minLatitude) {
            minLatitude = geographicCoordinates.getLatitude();
        }
        
        if (maxLatitude == null || geographicCoordinates.getLatitude() > maxLatitude) {
            maxLatitude = geographicCoordinates.getLatitude();
        }
        
        if (minLongitude == null || geographicCoordinates.getLongitude() < minLongitude) {
            minLongitude = geographicCoordinates.getLongitude();
        }
        
        if (maxLongitude == null || geographicCoordinates.getLongitude() > maxLongitude) {
            maxLongitude = geographicCoordinates.getLongitude();
        }
    }
    
    public boolean isFilled() {
        return minLatitude != null && maxLatitude != null
                && minLongitude != null && maxLongitude != null;
    }
    
    // Not used yet
    public boolean isOutside(@NonNull final GeographicCoordinates geographicCoordinates) {
        if (!isFilled() || !geographicCoordinates.isFilled()) {
            return true;
        }
        
        if (geographicCoordinates.getLatitude() < minLatitude
                || geographicCoordinates.getLatitude() > maxLatitude) {
            return true;
        }
        
        if (geographicCoordinates.getLongitude() < minLongitude
                || geographicCoordinates.getLongitude() > maxLongitude) {
            return true;
        }
        
        return false;
    }
}
