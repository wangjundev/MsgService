package com.stv.msgservice.core.mmslib;

import android.util.Log;

import androidx.collection.SimpleArrayMap;

public abstract class AbstractCache<K, V> {
    private static final String TAG = "AbstractCache";
    private static final boolean LOCAL_LOGV = false;

    private static final int MAX_CACHED_ITEMS = 500;

    private final SimpleArrayMap<K, CacheEntry<V>> mCacheMap;

    protected AbstractCache() {
        mCacheMap = new SimpleArrayMap<K, CacheEntry<V>>();
    }

    public boolean put(K key, V value) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "Trying to put " + key + " into cache.");
        }

        if (mCacheMap.size() >= MAX_CACHED_ITEMS) {
            // TODO: Should remove the oldest or least hit cached entry
            // and then cache the new one.
            if (LOCAL_LOGV) {
                Log.v(TAG, "Failed! size limitation reached.");
            }
            return false;
        }

        if (key != null) {
            CacheEntry<V> cacheEntry = new CacheEntry<V>();
            cacheEntry.value = value;
            mCacheMap.put(key, cacheEntry);

            if (LOCAL_LOGV) {
                Log.v(TAG, key + " cached, " + mCacheMap.size() + " items total.");
            }
            return true;
        }
        return false;
    }

    public V get(K key) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "Trying to get " + key + " from cache.");
        }

        if (key != null) {
            CacheEntry<V> cacheEntry = mCacheMap.get(key);
            if (cacheEntry != null) {
                cacheEntry.hit++;
                if (LOCAL_LOGV) {
                    Log.v(TAG, key + " hit " + cacheEntry.hit + " times.");
                }
                return cacheEntry.value;
            }
        }
        return null;
    }

    public V purge(K key) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "Trying to purge " + key);
        }

        CacheEntry<V> v = mCacheMap.remove(key);

        if (LOCAL_LOGV) {
            Log.v(TAG, mCacheMap.size() + " items cached.");
        }

        return v != null ? v.value : null;
    }

    public void purgeAll() {
        if (LOCAL_LOGV) {
            Log.v(TAG, "Purging cache, " + mCacheMap.size()
                    + " items dropped.");
        }
        mCacheMap.clear();
    }

    public int size() {
        return mCacheMap.size();
    }

    private static class CacheEntry<V> {

        int hit;

        V value;
    }
}
