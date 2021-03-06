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
import android.text.TextUtils;
import com.gdg.andconlab.utils.CompatibilityUtils;
import com.gdg.andconlab.utils.PackageUtils;
import com.gdg.andconlab.utils.SLog;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple subclass of {@link ImageResizer} that fetches and resizes images fetched from a URL.
 */
public class ImageFetcher extends ImageResizer {
    private static final String TAG = "ImageFetcher";
    private static final long HTTP_CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String HTTP_CACHE_DIR = "http";


    /**
     * Initialize providing a target image width and height for the processing images.
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
    public ImageFetcher(Context context, int imageWidth, int imageHeight) {
        super(context, imageWidth, imageHeight);
    }


    /**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageSize
     */
    public ImageFetcher(Context context, int imageSize) {
        super(context, imageSize);
    }

    /**
     * IInitialize providing no target image size - will not sample any bitmaps
     *
     * @param context
     */
    public ImageFetcher(Context context) {
        super(context);
    }

    /**
     * The main process method, which will be called by the ImageWorker in the AsyncTask background thread.
     *
     * @param data The data to load the bitmap, in this case, a regular http URL
     * @return The downloaded and resized bitmap
     */
    private Bitmap processBitmap(String data) {
        SLog.d(TAG, "processBitmap - %s", data);

        if (TextUtils.isEmpty(data)) {
            return null;
        }

        // Download a bitmap, write it to a file
        final File f = downloadBitmap(mContext, data);

        if (f != null) {
            // Return a sampled down version
            return decodeSampledBitmapFromFile(f.toString(), mImageWidth, mImageHeight);
        }

        return null;
    }

    @Override
    protected Bitmap processBitmap(Object data) {
        Bitmap bitmap = null;
        if (data instanceof Integer) {
            // Application resource
            bitmap = decodeSampledBitmapFromResource(mContext.getResources(), (Integer) data, mImageWidth, mImageHeight);
        } else {
            // URL
            bitmap = processBitmap((data != null) ? String.valueOf(data) : null);
        }

        return bitmap;
    }


    /**
     * Download a bitmap from a URL, write it to a disk and return the File pointer. This implementation uses a simple disk cache.
     *
     * @param context   The context to use
     * @param urlString The URL to fetch
     * @return A File pointing to the fetched bitmap
     */
    public static File downloadBitmap(Context context, String urlString) {
        File cacheFile = null;
        final File cacheDir = DiskLruCache.getDiskCacheDir(context, HTTP_CACHE_DIR);
        final DiskLruCache cache;
        InputStream stream = null;
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        try {
            cache = DiskLruCache.open(cacheDir, PackageUtils.getApplicationVersionCode(context), 1, HTTP_CACHE_SIZE);
            DiskLruCache.Snapshot snapshot = cache.get(urlString);
            if (snapshot != null) {
                // Found in disk cache
                stream = snapshot.getInputStream(0);
            } else {
                // Get from the server
                CompatibilityUtils.disableConnectionReuseIfNecessary();
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                stream = urlConnection.getInputStream();
            }

            // Create file
            // We use a file in order to sample and decode the bitmap with optimum values
            if (stream != null) {
                final InputStream in = new BufferedInputStream(stream, CompatibilityUtils.IO_BUFFER_SIZE);
                cacheFile = cache.createTmpFile(urlString);
                out = new BufferedOutputStream(new FileOutputStream(cacheFile), CompatibilityUtils.IO_BUFFER_SIZE);

                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
            }
        } catch (Exception e) {
            String message = e.getMessage();
            SLog.i(TAG, "Couldn't download bitmap since it was not found: [%s]", (message != null) ? message : "unknown");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    SLog.e(TAG, "Error in downloadBitmap", e);
                }
            }
        }

        return cacheFile;
    }
}
