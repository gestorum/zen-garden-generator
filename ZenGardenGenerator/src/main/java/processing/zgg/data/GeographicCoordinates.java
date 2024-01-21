/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
