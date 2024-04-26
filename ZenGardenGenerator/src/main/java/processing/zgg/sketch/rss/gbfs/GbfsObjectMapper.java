package processing.zgg.sketch.rss.gbfs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author gestorum
 */
public interface GbfsObjectMapper<T> {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    T map(String jsonPayload) throws JsonProcessingException;
}
