/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdg.andconlab.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.v4.util.LruCache;
import com.gdg.andconlab.utils.BitmapUtils;
import com.gdg.andconlab.utils.CompatibilityUtils;
import com.gdg.andconlab.utils.PackageUtils;
import com.gdg.andconlab.utils.SLog;

import java.io.File;
import java.io.IOException;


/**
 * This class holds our bitmap caches (memory and disk).
 */
public class ImageCache
{
	private static final String TAG = "ImageCache";

	// Default memory cache size
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB

	// Default disk cache size
	private static final long DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

	// Default value for number of values for each cache entity
	private static final int DEFAULT_CACHE_ENTITY_VALUES = 1;

	// Compression settings when writing images to disk cache
	private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
	private static final int DEFAULT_COMPRESS_QUALITY = 70;

	// Constants to easily toggle various caches
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
	private static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = false;

	// Constants for bitmap compression
	private static final CompressFormat COMPRESS_FORMAT = CompressFormat.PNG;
	private static final int COMPRESS_QUALITY = 70;

	private DiskLruCache mDiskCache;
	private LruCache<String, Bitmap> mMemoryCache;


	/**
	 * Creating a new ImageCache object using the specified parameters.
	 * 
	 * @param context
	 *            The context to use
	 * @param cacheParams
	 *            The cache parameters to use to initialize the cache
	 */
	public ImageCache(Context context, ImageCacheParams cacheParams)
	{
		init(context, cacheParams);
	}


	/**
	 * Creating a new ImageCache object using the default parameters.
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique name that will be appended to the cache directory
	 */
	public ImageCache(Context context, String uniqueName)
	{
		init(context, new ImageCacheParams(uniqueName));
	}


	/**
	 * Initialize the cache, providing all parameters.
	 * 
	 * @param context
	 *            The context to use
	 * @param cacheParams
	 *            The cache parameters to initialize the cache
	 */
	private void init(Context context, ImageCacheParams cacheParams)
	{
		final File diskCacheDir = DiskLruCache.getDiskCacheDir(context, cacheParams.uniqueName);

		// Set up disk cache
		if (cacheParams.diskCacheEnabled) {
			try {
				mDiskCache = DiskLruCache.open(diskCacheDir, PackageUtils.getApplicationVersionCode(context),
						cacheParams.cacheEntityValues, cacheParams.diskCacheSize);
			}
			catch (IOException e) {
				SLog.e(TAG, "Couldn't create/open disk cache", e);
			}

			// mDiskCache = DiskLruCacheOLD.openCache(context, diskCacheDir, cacheParams.diskCacheSize);
			// mDiskCache.setCompressParams(cacheParams.compressFormat, cacheParams.compressQuality);
			if (cacheParams.clearDiskCacheOnStart) {
				try {
					mDiskCache.clearCache();
				}
				catch (IOException e) {
					SLog.e(TAG, "Couldn't clear disk cache", e);
				}
			}
		}

		// Set up memory cache
		if (cacheParams.memoryCacheEnabled) {
			mMemoryCache = new LruCache<String, Bitmap>(cacheParams.memCacheSize)
			{
				/**
				 * Measure item size in bytes rather than units which is more practical for a bitmap cache
				 */
				@Override
				protected int sizeOf(String key, Bitmap bitmap)
				{
					return CompatibilityUtils.getBitmapSize(bitmap);
				}
			};
		}
	}


	public void addBitmapToCache(String data, Bitmap bitmap)
	{
		if (data == null || bitmap == null) {
			return;
		}

		// Add to memory cache
		if (mMemoryCache != null && mMemoryCache.get(data) == null) {
			mMemoryCache.put(data, bitmap);
		}

		// Add to disk cache
		if (mDiskCache != null && !mDiskCache.containsKey(data)) {
			DiskLruCache.Editor creator = null;
			try {
				creator = mDiskCache.edit(data);
				if (creator != null) {
					boolean success = creator.set(0, bitmap, COMPRESS_FORMAT, COMPRESS_QUALITY);
					if (success) {
						creator.commit();
					}
					else {
						creator.abort();
					}
				}
			}
			catch (Exception e) {
				SLog.e(TAG, "Couldn't persist bitmap in disk cache", e);
				if (creator != null) {
					try {
						creator.abort();
					}
					catch (Exception e2) {
					}
				}
			}
			// mDiskCache.put(data, bitmap);
		}
	}


	/**
	 * Get from memory cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap if found in cache, null otherwise
	 */
	public Bitmap getBitmapFromMemCache(String data)
	{
		if (mMemoryCache != null) {
			final Bitmap memBitmap = mMemoryCache.get(data);
			if (memBitmap != null) {
				SLog.d(TAG, "Memory cache hit");
				return memBitmap;
			}
		}
		return null;
	}


	/**
	 * Get from disk cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap if found in cache, null otherwise
	 */
	public Bitmap getBitmapFromDiskCache(String data)
	{
		DiskLruCache.Snapshot snapshot = null;
		if (mDiskCache != null) {
			try {
				snapshot = mDiskCache.get(data);
			}
			catch (Exception e) {
				SLog.e(TAG, "Couldn't read from disk cache", e);
			}
		}

		if (snapshot != null) {
			return BitmapUtils.decodeStream(snapshot.getInputStream(0));
		}

		return null;
	}


	public void clearCaches()
	{
		try {
			mDiskCache.clearCache();
		}
		catch (IOException e) {
			SLog.e(TAG, "Couldn't clear disk cache", e);
		}

		// TODO ImageCache: think about maybe implementing a different cache that can recycle images
		mMemoryCache.evictAll();
	}

	/**
	 * A holder class that contains cache parameters.
	 */
	public static class ImageCacheParams
	{
		public String uniqueName;
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		public long diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
		public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
		public int compressQuality = DEFAULT_COMPRESS_QUALITY;
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
		public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;
		public final int cacheEntityValues = DEFAULT_CACHE_ENTITY_VALUES;


		public ImageCacheParams(String uniqueName)
		{
			this.uniqueName = uniqueName;
		}
	}
}
