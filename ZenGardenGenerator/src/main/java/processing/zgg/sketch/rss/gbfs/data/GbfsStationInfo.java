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
public class GbfsStationInfo {

    @JsonProperty("station_id")
    private String stationId;

    @JsonProperty("lat")
    private Double latitude;

    @JsonProperty("lon")
    private Double longitude;

    private Integer capacity;
}
