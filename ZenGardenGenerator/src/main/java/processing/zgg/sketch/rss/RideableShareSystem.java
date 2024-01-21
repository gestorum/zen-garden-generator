/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.rss;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import processing.zgg.data.GeographicCoordinates;
import processing.zgg.sketch.rss.data.OperationArea;
import processing.zgg.sketch.rss.data.Station;
import processing.zgg.sketch.rss.gbfs.data.GbfsStationInfo;
import processing.zgg.sketch.rss.gbfs.data.GbfsStationStatus;

/**
 *
 * @author gestorum
 */
public class RideableShareSystem {
    
    private final Map<String, Station> stationByIdMap = new HashMap<>();
    
    private final String name;
    private OperationArea operationArea;
    
    public RideableShareSystem(final String name) {
        this.name = name;
    }
    
    public void initFromGbfs(@NonNull final List<GbfsStationInfo> stationInfoList,
            @NonNull final List<GbfsStationStatus> stationStatusList) {
        stationByIdMap.clear();

        final OperationArea operationArea = new OperationArea();
        stationInfoList.forEach(s -> {
            final GeographicCoordinates geographicCoordinates = GeographicCoordinates.builder()
                    .latitude(s.getLatitude())
                    .longitude(s.getLongitude())
                    .build();
            operationArea.setFromGeographicCoordinates(geographicCoordinates);

            final Station station = Station.builder()
                    .id(s.getStationId())
                    .capacity(Optional.ofNullable(s.getCapacity()).orElse(0))
                    .geographicCoordinates(geographicCoordinates)
                    .build();
            stationByIdMap.put(station.getId(), station);
        });
        
        this.operationArea = operationArea;

        stationStatusList.stream().forEach(s -> {
            final Station station = stationByIdMap.get(s.getStationId());
            if (station != null) {
                final boolean isInstalled = Optional.ofNullable(s.getIsInstalled()).orElse(false);
                station.setInstalled(isInstalled);
                
                if (isInstalled) {
                    station.setAvailableDocks(Optional.ofNullable(s.getNumDocksAvailable()).orElse(0));
                    station.setAvailableRideables(Optional.ofNullable(s.getNumVehiculesAvailable()).orElse(0));
                }
            }
        });
    }
    
    public String getName() {
        return name;
    }
    
    public OperationArea getOperationArea() {
        return operationArea;
    }
    
    public int getStationCount() {
        return stationByIdMap.size();
    }
    
    public List<Station> getStations() {
        return stationByIdMap.values().stream().toList();
    }
    
    public Station getStationById(final String stationId) {
        if (stationId == null) {
            return null;
        }
        
        return stationByIdMap.get(stationId);
    }
    
    public List<Station> getStationsByIds(final Set<String> stationIds) {
        return stationIds.stream().map(this::getStationById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
