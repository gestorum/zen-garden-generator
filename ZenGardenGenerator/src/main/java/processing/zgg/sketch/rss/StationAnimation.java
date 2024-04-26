package processing.zgg.sketch.rss;

import java.awt.Color;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author gestorum
 */
@Data
@Builder
public class StationAnimation {

    private String stationId;
    private List<Integer> widthList;
    private Color color;
}
