/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
