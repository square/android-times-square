package com.squareup.timessquare;

import android.util.SparseArray;
import java.util.LinkedHashMap;

/**
 * Take advantage of LinkedHashMap's iterable ordering but also keep track of the indexes and allow
 * - Fast key lookup by index
 * - Fast index lookup by key
 */
class IndexedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
  private final SparseArray<K> indexLookup = new SparseArray<>();
  private int index = 0;

  @Override public V put(K key, V value) {
    indexLookup.put(index, key);
    index++;
    return super.put(key, value);
  }

  @Override public void clear() {
    super.clear();
    index = 0;
    indexLookup.clear();
  }

  @Override public V remove(Object key) {
    throw new UnsupportedOperationException("IndexedLinkedHashMap is put/clear only");
  }

  V getValueAtIndex(int index) {
    return get(indexLookup.get(index));
  }

  int getIndexOfKey(K key) {
    return indexLookup.indexOfValue(key);
  }
}
