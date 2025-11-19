package com.example.ecommerce;

import com.example.ecommerce.MyTestWatcher.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum DataStore {
  instance;
  private final Map<String, Data> tracker = new ConcurrentHashMap<>();

  public Map<String, Data> getTracker() {
    return tracker;
  }


}
