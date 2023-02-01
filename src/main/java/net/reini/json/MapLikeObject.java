package net.reini.json;

import java.util.HashMap;
import java.util.Map;

import jakarta.json.bind.annotation.JsonbTransient;

public class MapLikeObject {
  @JsonbTransient
  private Map<String, SubDataObject> values;

  public void put(String key, SubDataObject subDataObject) {
    if (values == null) {
      values = new HashMap<>();
    }
    values.put(key, subDataObject);
  }
  
  
  
}
