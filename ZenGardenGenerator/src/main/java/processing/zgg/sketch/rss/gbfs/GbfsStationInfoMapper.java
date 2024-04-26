package processing.zgg.sketch.rss.gbfs;

import com.fasterxml.jackson.core.JsonProcessingException;
import processing.zgg.sketch.rss.gbfs.data.GbfsStationInfo;

/**
 *
 * @author gestorum
 */
public class GbfsStationInfoMapper implements GbfsObjectMapper<GbfsStationInfo> {

    @Override
    public GbfsStationInfo map(final String jsonPayload) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonPayload, GbfsStationInfo.class);
    }
}
