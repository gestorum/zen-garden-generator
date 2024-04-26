package processing.zgg.sketch.rss.gbfs.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author gestorum
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GbfsStationStatus {

    @JsonProperty("station_id")
    private String stationId;

    @JsonProperty("num_vehicles_available")
    private Integer numVehiculesAvailable;

    @JsonProperty("num_docks_available")
    private Integer numDocksAvailable;
    
    @JsonProperty("is_installed")
    private Boolean isInstalled;
}
