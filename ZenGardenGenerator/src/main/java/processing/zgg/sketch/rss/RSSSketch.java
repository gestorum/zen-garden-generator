/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package processing.zgg.sketch.rss;

import processing.zgg.sketch.rss.event.RideableShareSystemStationEvent;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.NonNull;
import processing.core.PVector;
import processing.zgg.sketch.ZenGardenSketch;
import processing.zgg.audio.ZenGardenSoundGenerator;
import processing.zgg.data.AbstractParticle;
import processing.zgg.sketch.rss.data.RideableParticle;
import processing.zgg.sketch.rss.data.Station;
import processing.zgg.sketch.rss.data.StationParticle;
import processing.zgg.sketch.rss.gbfs.GbfsRideableShareSystemBuilder;
import processing.zgg.sketch.rss.event.RideableShareSystemEventType;

/**
 *
 * @author gestorum
 */
public class RSSSketch extends ZenGardenSketch {

    private static final int INITIAL_MAP_WIDTH = 512;
    private static final int INITIAL_MAP_HEIGHT = INITIAL_MAP_WIDTH;

    private static final int FRAME_RATE = 24;

    private static final Color STATION_FILL_COLOR = Color.GREEN;
    private static final Color STATION_NOT_INSTALLED_FILL_COLOR = Color.GRAY;
    private static final Color STATION_FULL_FILL_COLOR = Color.RED;
    private static final Color STATION_RIDEABLE_DOCKED_FILL_COLOR = Color.GREEN;
    private static final Color STATION_RIDEABLE_REFUSED_FILL_COLOR = Color.RED;
    
    private static final Color RIDEABLE_STROKE_COLOR = Color.WHITE;
    private static final Color RIDEABLE_TRAIL_STROKE_COLOR = Color.GRAY;
    private static final int RIDEABLE_TRAIL_MAX_LENGTH = 6;

    private RideableShareSystem rideableShareSystem;
    private RideableShareParticleSystem rssParticleSystem;

    private int rideableSpeedUpFactor = 1;
    private boolean rideableTrailEnabled = true;
    private boolean updateRideables = true;
    private boolean displayRideables = true;
    private boolean displayStations = true;
    
    private final Map<String, List<RideableShareSystemStationEvent>> stationEventsByStationId = new HashMap<>();
    private final Map<String, List<StationAnimation>> stationAnimationListByStationId = new HashMap<>();
    private final Map<String, List<PVector>> previousPositionListByRideableId = new HashMap<>();

    @Override
    public void settings() {
        super.settings();
        
        if (args.length <= 0) {
            throw new IllegalArgumentException("You must provide the rideable share system name as program argument");
        }

        try {
            rideableShareSystem = new GbfsRideableShareSystemBuilder().build(args[0]);
            rssParticleSystem = new RideableShareParticleSystem(rideableShareSystem);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot init rideable share system", e);
        }

        initSoundGenerator();

        size(INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT);
    }

    @Override
    public void setup() {
        super.setup();
        
        frameRate(FRAME_RATE);

        surface.setResizable(true);
        
        windowResized();
    }

    @Override
    protected void drawFrame() {
        if (updateRideables) {
            rssParticleSystem.update();
        }
        
        rssParticleSystem.getParticles().forEach(this::drawParticle);
    }

    @Override
    public void keyPressed() {
        super.keyPressed();
        
        switch (keyCode) {
            case UP, 33, 107 -> { // Up, Page Up or Num +
                rideableSpeedUpFactor++;
                rssParticleSystem.setSpeedUpFactor(rideableSpeedUpFactor);
            }

            case DOWN, 34, 109 -> { // Down, Page Down or Num -
                rideableSpeedUpFactor = Math.max(rideableSpeedUpFactor - 1, 1);
                rssParticleSystem.setSpeedUpFactor(rideableSpeedUpFactor);
            }
        }
        
        switch (key) {
            case 't', 'T' ->
                rideableTrailEnabled = !rideableTrailEnabled;

            case 'u', 'U' ->
                updateRideables = !updateRideables;

            case 'd' ->
                displayRideables = !displayRideables;

            case 'D' ->
                displayStations = !displayStations;
        }
    }

    @Override
    public void windowResized() {
        rssParticleSystem.init(width, height);
        
        previousPositionListByRideableId.clear();
    }

    @Override
    protected String getFrameRecordingFilenamePrefix() {
        return rideableShareSystem.getName();
    }

    @Override
    protected String getFrameCaptureFilenamePrefix() {
        return rideableShareSystem.getName();
    }

    @Override
    protected String getWindowTitle() {
        return String.format("%s: %d stations | %d rides%s",
                rideableShareSystem.getName().toUpperCase(),
                rssParticleSystem.getStationCount(),
                rssParticleSystem.getRideableCount(),
                rideableSpeedUpFactor > 1 ? " x" + rideableSpeedUpFactor : "");
    }
    
    private void drawParticle(@NonNull final AbstractParticle particle) {
        if (displayStations && particle.getClass().isAssignableFrom(StationParticle.class)) {
            drawStation((StationParticle)particle);
        } else if (displayRideables && particle.getClass().isAssignableFrom(RideableParticle.class)) {
            drawRideable((RideableParticle)particle);
        }
    }
    
    private void drawStation(@NonNull final StationParticle stationParticle) {
        final Point screenCoordinates = new Point((int) stationParticle.getPosition().x,
                (int) stationParticle.getPosition().y);
        final Station station = rideableShareSystem
                .getStationById(stationParticle.getId());
        
        Color fillColor;
        if (station.isInstalled()) {
            fillColor = station.getAvailableDocks() > 0 ? STATION_FILL_COLOR : STATION_FULL_FILL_COLOR;
        } else {
            fillColor = STATION_NOT_INSTALLED_FILL_COLOR;
        }
        
        int stationWidth = rssParticleSystem.getStationWidth();

        final StationAnimation stationAnimation;
        final List<StationAnimation> stationAnimations = stationAnimationListByStationId.get(stationParticle.getId());
        if (stationAnimations != null && !stationAnimations.isEmpty()) {
            stationAnimation = stationAnimations.get(0);
            fillColor = stationAnimation.getColor();

            final List<Integer> stationWidthList = stationAnimation.getWidthList();
            if (stationWidthList != null && !stationWidthList.isEmpty()) {
                stationWidth = stationWidthList.remove(0);

                if (stationWidthList.isEmpty()) {
                    stationAnimations.remove(stationAnimation);
                    stationAnimationListByStationId.remove(stationParticle.getId());
                }
            }
        } else {
            final RideableShareSystemStationEvent stationEvent = getNextStationEvent(stationParticle.getId());
            if (stationEvent != null) {
                final StationAnimation.StationAnimationBuilder animationBuilder = StationAnimation.builder();
                animationBuilder.stationId(stationParticle.getId());
                animationBuilder.widthList(generateWidthList(station, stationWidth));

                final float amplitude = Math.min(ZenGardenSoundGenerator.Amplitude.MID.getValue()
                        + station.getCapacity() / 100f,
                        ZenGardenSoundGenerator.Amplitude.MAX.getValue());
                final int freq = 250 + station.getAvailableRideables() * 10;
                final float panZero = width / 2;
                final float relativeLocation = (float) screenCoordinates.getX() / width;
                final float pan = screenCoordinates.getX() < panZero ? relativeLocation - 1 : relativeLocation;

                switch (stationEvent.getType()) {
                    case RIDEABLE_DOCKED_TO_STATION -> {
                        animationBuilder.color(STATION_RIDEABLE_DOCKED_FILL_COLOR);
                        playFreq(ZenGardenSoundGenerator.Instrument.WHITE_WAVE,
                                amplitude, freq, null, pan);
                    }

                    case RIDEABLE_RETURN_TO_STATION_REFUSED -> {
                        animationBuilder.color(STATION_RIDEABLE_REFUSED_FILL_COLOR);
                        playFreq(ZenGardenSoundGenerator.Instrument.DRUM_WOOD,
                                amplitude, freq, null, pan);
                    }

                    default ->
                        animationBuilder.color(STATION_FILL_COLOR);
                }

                List<StationAnimation> curStationAnimations = stationAnimationListByStationId
                        .get(stationParticle.getId());
                if (curStationAnimations == null) {
                    curStationAnimations = new ArrayList<>();
                    stationAnimationListByStationId.put(stationParticle.getId(), curStationAnimations);
                }
                curStationAnimations.add(animationBuilder.build());
            }
        }

        push();
        fill(fillColor.getRGB());
        stroke(fillColor.getRGB(), 100);
        strokeWeight(2);
        ellipse((float) screenCoordinates.getX(), (float) screenCoordinates.getY(),
                stationWidth, stationWidth);
        pop();
    }

    private void drawRideable(@NonNull final RideableParticle rideableParticle) {
        final int rideableWidth = rssParticleSystem.getRideableWidth();
        final PVector rideablePosition = rideableParticle.getPosition();
        
        if (rideableTrailEnabled) {
            List<PVector> previousPositions = previousPositionListByRideableId.get(rideableParticle.getId());
            if (previousPositions == null) {
                previousPositions = new ArrayList<>();
                previousPositionListByRideableId.put(rideableParticle.getId(), previousPositions);
            } else if (!previousPositions.isEmpty()) {
                int previousRideableWidth = 1;
                int previousAlpha = 10;
                int alphaIncrement = 255 / previousPositions.size();
                for (int i = 0; i < previousPositions.size(); i++) {
                    push();
                    stroke(RIDEABLE_TRAIL_STROKE_COLOR.getRGB(), previousAlpha);
                    strokeWeight(previousRideableWidth);

                    final PVector previousPosition = previousPositions.get(i);
                    point(previousPosition.x, previousPosition.y);
                    pop();

                    previousRideableWidth = Math.min(previousRideableWidth + 1, rideableWidth);
                    previousAlpha = Math.min(previousAlpha + alphaIncrement, 255);
                }
                
                if (previousPositions.size() > RIDEABLE_TRAIL_MAX_LENGTH) {
                    previousPositions.remove(0);
                }
            }

            if (frameCount % Math.floor(FRAME_RATE / 4) == 0) { // Take a snapshot of the rideable position every 500 milliseconds
                previousPositions.add(rideablePosition.copy());
            }
        } else {
            previousPositionListByRideableId.clear();
        }
        
        push();
        fill(RIDEABLE_STROKE_COLOR.getRGB());
        stroke(RIDEABLE_STROKE_COLOR.getRGB());
        strokeWeight(rideableWidth);
//        translate(rideablePosition.x, rideablePosition.y);
//        rotate(rideableParticle.getVelocity().heading());
//        beginShape();
//        vertex(rideableParticle.getRadius(), 0);
//        vertex(-rideableParticle.getRadius(), -rideableParticle.getRadius());
//        vertex(-rideableParticle.getRadius(), rideableParticle.getRadius());
//        endShape(CLOSE);
        point(rideablePosition.x, rideablePosition.y);
        pop();
        
        final StationParticle stationParticle = rssParticleSystem
                .getStationParticleById(rideableParticle.getStationId());
        if (stationParticle != null) {
            final PVector stationPosition = stationParticle.getPosition();
            if (rideablePosition.dist(stationPosition) < stationParticle.getRadius()) {
                final Station station = rideableShareSystem
                        .getStationById(stationParticle.getId());
                
                if (station != null) {
                    if (station.isInstalled() && station.getAvailableDocks() > 0) {
                        rideableParticle.setDead(true);
                        station.setAvailableDocks(station.getAvailableDocks() - 1);
                        station.setAvailableRideables(station.getAvailableRideables() + 1);
                        
                        createStationEvent(stationParticle.getId(),
                                RideableShareSystemEventType.RIDEABLE_DOCKED_TO_STATION);
                    } else {
                        rideableParticle.setStationId(rssParticleSystem
                                .randomizeStationId(station.getId()));
                        
                        createStationEvent(stationParticle.getId(),
                                RideableShareSystemEventType.RIDEABLE_RETURN_TO_STATION_REFUSED);
                    }
                }
            }
        }
    }
    
    private List<Integer> generateWidthList(@NonNull final Station station,
            final int stationWidth) {
        final List<Integer> stationWidthList = new ArrayList<>();

        final int extendedWidth = Math.round(stationWidth + station.getCapacity() * 0.33f);
        stationWidthList.addAll(IntStream.range(1,
                stationWidth).boxed()
                .sorted(Collections.reverseOrder()).toList());
        stationWidthList.addAll(IntStream.range(0,
                extendedWidth).boxed().toList());
        stationWidthList.addAll(IntStream.range(stationWidth + 1,
                extendedWidth - 1).boxed()
                .sorted(Collections.reverseOrder()).toList());

        return stationWidthList;
    }

    private RideableShareSystemStationEvent getNextStationEvent(@NonNull final String stationId) {
        final List<RideableShareSystemStationEvent> stationEvents = stationEventsByStationId.get(stationId);
        if (stationEvents == null || stationEvents.isEmpty()) {
            return null;
        }

        return stationEvents.remove(0);
    }
    
    private void createStationEvent(@NonNull final String stationId,
            @NonNull final RideableShareSystemEventType eventType) {
        final RideableShareSystemStationEvent stationEvent = RideableShareSystemStationEvent
                .builder().stationId(stationId).type(eventType).build();
        
        List<RideableShareSystemStationEvent> events = stationEventsByStationId.get(stationId);
        if (events == null) {
            events = new ArrayList<>();
            stationEventsByStationId.put(stationId, events);
        }
        
        events.add(stationEvent);
    }
}
