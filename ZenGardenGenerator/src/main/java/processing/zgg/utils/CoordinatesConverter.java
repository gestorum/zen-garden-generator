package processing.zgg.utils;

import java.awt.Point;
import lombok.NonNull;
import processing.zgg.data.GeographicCoordinates;

/**
 *
 * @author gestorum
 */
public class CoordinatesConverter {

    private static final int FE = 180;
    private static final double D2R = Math.PI / FE;
    private static final float EARTH_RADIUS = 6371.001f;

    private CoordinatesConverter() {
    }

    public static Point toPoint(final double latitude,
            final double longitude, final int mapWidth, final int mapHeight,
            final int zoomFactor) {
        final double radius = mapWidth * zoomFactor / (2 * Math.PI);
        final double lonRad = Math.toRadians(longitude + FE);
        final double latRad = Math.toRadians(latitude);

        final int x = (int) Math.round(lonRad * radius);
        final double yFromEquator = radius * Math.log(Math.tan(Math.PI / 4 + latRad / 2));
        final int y = (int) Math.round(mapHeight * zoomFactor / 2 - yFromEquator);

        return new Point(x, y);
    }

    public static Point toPoint(@NonNull final GeographicCoordinates geographicCoordinates,
            final int mapWidth, final int mapHeight, final int zoomFactor) {
        if (!geographicCoordinates.isFilled()) {
            throw new IllegalArgumentException("Wrong geographic coordinates");
        }

        return toPoint(geographicCoordinates.getLatitude(),
                geographicCoordinates.getLongitude(), mapWidth, mapHeight,
                zoomFactor);
    }

    public static double calculateDistanceInKilometers(@NonNull final GeographicCoordinates aCoords,
            @NonNull final GeographicCoordinates bCoords) {
        double dlong = (bCoords.getLongitude() - aCoords.getLongitude()) * D2R;
        double dlat = (bCoords.getLatitude() - aCoords.getLatitude()) * D2R;
        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(aCoords.getLatitude() * D2R)
                * Math.cos(bCoords.getLatitude() * D2R) * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
