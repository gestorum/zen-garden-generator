package processing.zgg.sketch.rss.gbfs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import lombok.NonNull;
import processing.zgg.sketch.rss.RideableShareSystem;
import processing.zgg.sketch.rss.gbfs.data.GbfsStationInfo;
import processing.zgg.sketch.rss.gbfs.data.GbfsStationStatus;
import processing.zgg.utils.ResourceUtils;
import processing.zgg.sketch.rss.RideableShareSystemBuilder;

/**
 *
 * @author gestorum
 */
public class GbfsRideableShareSystemBuilder implements RideableShareSystemBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String GBFS_HOSTS_PROPERTY_FILENAME = "gbfs_hosts.properties";
    
    private static final String GBFS_HOST_PROTOCOL = "https://";
    private static final String GBFS_BASE_PATH = "/en";
    private static final String GBFS_STATION_INFO_PATH = GBFS_BASE_PATH + "/station_information.json";
    private static final String GBFS_STATION_STATUS_PATH = GBFS_BASE_PATH + "/station_status.json";
    
    private static final String DUMMY_NAME = "dummy";

    @Override
    public RideableShareSystem build(@NonNull final String name) throws IOException {
        if (DUMMY_NAME.equalsIgnoreCase(name))
            return buildDummy(DUMMY_NAME);
        
        final Properties sketchProperties = ResourceUtils
                .loadAllProperties(GBFS_HOSTS_PROPERTY_FILENAME);
        final String bssHost = (String) sketchProperties.get(name);
        final String baseUrl = GBFS_HOST_PROTOCOL + bssHost;
                
        final URL stationInfoUrl = new URL(baseUrl + GBFS_STATION_INFO_PATH);
        final JsonNode stationInfoJsonNode = OBJECT_MAPPER.readTree(stationInfoUrl);
        final JsonNode stationInfoStationsJsonNode = stationInfoJsonNode.get("data").get("stations");
        
        final List<GbfsStationInfo> gbfsStationInfoList = new ArrayList<>();
        if (stationInfoStationsJsonNode.isArray()) {
            final GbfsStationInfoMapper gbfsStationInfoMapper = new GbfsStationInfoMapper();
            for (JsonNode curNode : stationInfoStationsJsonNode) {
                final GbfsStationInfo gbfsStationInfo = gbfsStationInfoMapper
                        .map(curNode.toString());
                if (isStationInfoValid(gbfsStationInfo)) {
                    gbfsStationInfoList.add(gbfsStationInfo);
                }
            }
        }
        
        final URL stationStatusUrl = new URL(baseUrl + GBFS_STATION_STATUS_PATH);
        final JsonNode stationStatusJsonNode = OBJECT_MAPPER.readTree(stationStatusUrl);
        final JsonNode stationStatusStationsJsonNode = stationStatusJsonNode.get("data").get("stations");

        final List<GbfsStationStatus> gbfsStationStatusList = new ArrayList<>();
        if (stationStatusStationsJsonNode.isArray()) {
            final GbfsStationStatusMapper gbfsStationStatusMapper = new GbfsStationStatusMapper();
            for (JsonNode curNode : stationStatusStationsJsonNode) {
                final GbfsStationStatus gbfsStationStatus = gbfsStationStatusMapper
                        .map(curNode.toString());
                gbfsStationStatusList.add(gbfsStationStatus);
            }
        }
        
        final RideableShareSystem bikeShareSystem = new RideableShareSystem(name);
        
        bikeShareSystem.initFromGbfs(gbfsStationInfoList, gbfsStationStatusList);
        
        return bikeShareSystem;
    }
    
    /**
     * Tests if the station can handle at least one rideable and is not located
     * on <a href="https://en.wikipedia.org/wiki/Null_Island">Null Island</a>.
     * 
     * @param stationInfo
     * @return true if the station info is valid
     */
    private boolean isStationInfoValid(@NonNull final GbfsStationInfo stationInfo) {
        return Optional.ofNullable(stationInfo.getCapacity()).orElse(0) > 0
                && (Optional.ofNullable(stationInfo.getLatitude()).orElse(0d) > 0
                || Optional.ofNullable(stationInfo.getLongitude()).orElse(0d) > 0);
    }
    
    private RideableShareSystem buildDummy(@NonNull final String name) {
        final List<GbfsStationInfo> gbfsStationInfoList = new ArrayList<>();
        final List<GbfsStationStatus> gbfsStationStatusList = new ArrayList<>();
        
        double curLatitude = 46.82472635206527;
        double curLongitude = -71.22830601858517;
        for (int i = 0; i < 10; i++) {
            final String stationId = String.valueOf(i+1);
            
            final GbfsStationInfo gbfsStationInfo = new GbfsStationInfo();
            gbfsStationInfo.setStationId(stationId);
            gbfsStationInfo.setCapacity(10);
            gbfsStationInfo.setLatitude(curLatitude);
            gbfsStationInfo.setLongitude(curLongitude);
            gbfsStationInfoList.add(gbfsStationInfo);
            
            curLatitude += 0.0001;
            curLongitude += 0.0001;
            
            final GbfsStationStatus gbfsStationStatus = new GbfsStationStatus();
            gbfsStationStatus.setStationId(stationId);
            gbfsStationStatus.setNumDocksAvailable(5);
            gbfsStationStatus.setNumVehiculesAvailable(5);
            gbfsStationStatus.setIsInstalled(true);
            gbfsStationStatusList.add(gbfsStationStatus);
        }
        
        final RideableShareSystem bikeShareSystem = new RideableShareSystem(name);
        
        bikeShareSystem.initFromGbfs(gbfsStationInfoList, gbfsStationStatusList);
        
        return bikeShareSystem;
    }
}
