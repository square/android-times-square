package com.squareup.timessquare;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Take advantage of LinkedHashMap's iterable ordering but also keep track of the indexes and allow
 * - Fast key lookup by index
 * - Fast index lookup by key
 */
class IndexedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
  private final Map<Integer, K> indexToKey = new LinkedHashMap<>();
  private final Map<K, Integer> keyToIndex = new LinkedHashMap<>();
  private int index = 0;

  @Override public V put(K key, V value) {
    indexToKey.put(index, key);
    keyToIndex.put(key, index);
    index++;
    return super.put(key, value);
  }

  @Override public void clear() {
    super.clear();
    index = 0;
    indexToKey.clear();
    keyToIndex.clear();
  }

  @Override public V remove(Object key) {
    throw new UnsupportedOperationException("IndexedLinkedHashMap is put/clear only");
  }

  V getValueAtIndex(int index) {
    return get(indexToKey.get(index));
  }

  int getIndexOfKey(K key) {
    return keyToIndex.get(key);
  }
}
