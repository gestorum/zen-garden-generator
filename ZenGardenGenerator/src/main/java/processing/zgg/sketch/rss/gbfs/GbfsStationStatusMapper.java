package processing.zgg.sketch.rss.gbfs;

import com.fasterxml.jackson.core.JsonProcessingException;
import processing.zgg.sketch.rss.gbfs.data.GbfsStationStatus;

/**
 *
 * @author gestorum
 */
public class GbfsStationStatusMapper implements GbfsObjectMapper<GbfsStationStatus> {

    @Override
    public GbfsStationStatus map(final String jsonPayload) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonPayload, GbfsStationStatus.class);
    }
}
