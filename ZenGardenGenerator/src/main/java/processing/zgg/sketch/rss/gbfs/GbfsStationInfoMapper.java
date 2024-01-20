/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.rss.gbfs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import processing.data.JSONObject;
import processing.zgg.sketch.rss.gbfs.data.GbfsStationInfo;

/**
 *
 * @author pbergeron
 */
public class GbfsStationInfoMapper implements GbfsObjectMapper<GbfsStationInfo> {

    @Override
    public GbfsStationInfo map(final String jsonPayload) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonPayload, GbfsStationInfo.class);
    }
}
