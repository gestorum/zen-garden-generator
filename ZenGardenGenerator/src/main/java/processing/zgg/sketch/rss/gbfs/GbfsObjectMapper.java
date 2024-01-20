/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package processing.zgg.sketch.rss.gbfs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author pbergeron
 */
public interface GbfsObjectMapper<T> {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    T map(String jsonPayload) throws JsonProcessingException;
}
