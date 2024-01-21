/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.rss;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import processing.core.PVector;
import processing.zgg.data.AbstractParticle;
import processing.zgg.data.AbstractParticleSystem;
import processing.zgg.sketch.rss.data.OperationArea;
import processing.zgg.sketch.rss.data.RideableParticle;
import processing.zgg.sketch.rss.data.Station;
import processing.zgg.sketch.rss.data.StationParticle;
import processing.zgg.utils.CoordinatesConverter;

/**
 *
 * @author gestorum
 */
public class RideableShareParticleSystem
        extends AbstractParticleSystem<RideableShareSystem> {
    
    private static final int STATION_MIN_WIDTH = 3;
    private static final float STATION_WIDTH_RATIO = 0.007f;
    
    private static final int RIDEABLE_MIN_WIDTH = 1;
    private static final float RIDEABLE_WIDTH_RATIO = 0.002f;
    private static final int RIDEABLE_PERSONAL_SPACE_RADIUS_FACTOR = 20;
    
    private static final int RIDEABLE_MIN_VELOCITY = 1;
    private static final int RIDEABLE_MAX_VELOCITY = 10;
    
    private final Map<String, StationParticle> stationParticleByIdMap = new HashMap<>();
    private final List<StationParticle> stationParticles = new ArrayList<>();
    private final List<RideableParticle> rideableParticles = new ArrayList<>();
    
    private int stationWidth;
    private int rideableWidth;

    public RideableShareParticleSystem(@NonNull final RideableShareSystem rideableShareSystem) {
        super(rideableShareSystem);
    }
    
    @Override
    public void init(final int width, final int height) {
        final OperationArea operationArea = system.getOperationArea();
        if (operationArea == null || !operationArea.isFilled()) {
            return;
        }

        final double latDelta = Math.abs(operationArea.getMaxLatitude() - operationArea.getMinLatitude());
        final double lonDelta = Math.abs(operationArea.getMaxLongitude() - operationArea.getMinLongitude());
        final double maxDelta = Math.max(latDelta, lonDelta);
        final int zoomFactor = (int) Math.floor(360 / maxDelta);

        this.width = width;
        this.height = height;
        
        this.stationWidth = Math.max(Math.round(width * STATION_WIDTH_RATIO), STATION_MIN_WIDTH);
        this.rideableWidth = Math.max(Math.round(width * RIDEABLE_WIDTH_RATIO), RIDEABLE_MIN_WIDTH);
        
        final Point minScreenCoordinates = CoordinatesConverter
                .toPoint(operationArea.getMinLatitude(),
                        operationArea.getMinLongitude(), width,
                        height, zoomFactor);
        
        stationParticleByIdMap.clear();
        stationParticles.clear();
        rideableParticles.clear();
        
        system.getStations().forEach(s -> {
            final Point screenCoordinates = CoordinatesConverter
                    .toPoint(s.getGeographicCoordinates(),
                            width, height, zoomFactor);
            screenCoordinates.setLocation(Math.abs(screenCoordinates.getX() - minScreenCoordinates.getX()),
                    height - Math.abs(screenCoordinates.getY() - minScreenCoordinates.getY()));
            final StationParticle stationParticule = StationParticle.builder()
                    .id(s.getId())
                    .position(new PVector(screenCoordinates.x, screenCoordinates.y))
                    .radius(stationWidth)
                    .build();
            
            stationParticleByIdMap.put(stationParticule.getId(), stationParticule);
            
            if (!s.isInstalled()) {
                stationParticles.add(0, stationParticule);
            } else {
                stationParticles.add(stationParticule);
            }
            
            for (int d = 0; d < s.getAvailableDocks(); d++) {
                final RideableParticle rideableParticule = RideableParticle.builder()
                        .id(UUID.randomUUID().toString())
                        .position(randomizeRideablePosition())
                        .velocity(randomizeRideableVelocity(null))
                        .acceleration(new PVector())
                        .radius(rideableWidth)
                        .personalSpaceRadiusFactor(RIDEABLE_PERSONAL_SPACE_RADIUS_FACTOR)
                        .build();
                rideableParticles.add(rideableParticule);
            }
        });
        
        rideableParticles.stream().forEach(p -> {
            final StationParticle stationParticle = getStationParticleById(randomizeStationId(null));
            p.setStationId(stationParticle.getId());
            p.seek(stationParticle.getPosition());
        });
    }
    
    public long getStationCount() {
        return stationParticles.size();
    }
    
    public StationParticle getStationParticleById(final String stationId) {
        if (stationId == null) {
            return null;
        }
        
        return stationParticleByIdMap.get(stationId);
    }
    
    public long getRideableCount() {
        return rideableParticles.size();
    }

    @Override
    public void update() {
        purgeDeadParticles(rideableParticles);
        
        rideableParticles.forEach(p -> {
            final String stationId = p.getStationId();
            final StationParticle stationParticle = stationId != null ?
                    stationParticleByIdMap.get(p.getStationId()) : null;
            
            if (stationParticle != null) {
                p.seek(stationParticle.getPosition());
                p.setSpeedUpFactor(getSpeedUpFactor());
            
                final Optional<RideableParticle> detectedParticle = rideableParticles
                        .stream().filter(p::isCollisionDetected).findFirst();

                if (detectedParticle.isPresent()) {
                    p.applyForce(detectedParticle.get().getVelocity());
                }
                
                p.update();
            }
        });
    }
    
    @Override
    public List<AbstractParticle> getParticles() {
        return Stream.of(rideableParticles, stationParticles)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }
    
    public PVector randomizeRideablePosition() {
        final Random random = new Random();
        
        final int x = random.ints(0, width)
                .findFirst().getAsInt();
        final int y = random.ints(0, height)
                .findFirst().getAsInt();
        
        return new PVector(x, y);
    }
    
    public PVector randomizeRideableVelocity(final PVector currentVelocity) {
        final int min;
        final int max;
        
        if (currentVelocity != null) {
            min = Math.max((int)currentVelocity.x - 1, RIDEABLE_MIN_VELOCITY);
            max = Math.min((int)currentVelocity.x + 1, RIDEABLE_MAX_VELOCITY);
        } else {
            min = RIDEABLE_MIN_VELOCITY;
            max = RIDEABLE_MAX_VELOCITY;
        }

        final Random random = new Random();
        int velocity = max > min ? random.ints(min, max)
                .findFirst().getAsInt() : min;
        
        return new PVector(velocity, velocity);
    }
    
    public String randomizeStationId(final String currentStationId) {
        final Random random = new Random();
        
        final List<String> stationIds = system.getStations().stream()
                .filter(id -> currentStationId != null ? !currentStationId.equals(id) : true)
                .filter(Station::isInstalled).map(Station::getId)
                .toList();
        if (stationIds.isEmpty()) {
            return null;
        }
        
        final int randomIndex = random.ints(0, stationIds.size())
                .findFirst().getAsInt();
        
        return stationIds.get(randomIndex);
    }
    
    public int getStationWidth() {
        return stationWidth;
    }
    
    public int getRideableWidth() {
        return rideableWidth;
    }
}
