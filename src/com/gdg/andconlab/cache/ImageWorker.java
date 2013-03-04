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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;
import com.gdg.andconlab.BuildConfig;
import com.gdg.andconlab.ui.views.SafeImageView;
import com.gdg.andconlab.utils.BitmapUtils;
import com.gdg.andconlab.utils.LifoBlockingDeque;
import com.gdg.andconlab.utils.ModernAsyncTask;
import com.gdg.andconlab.utils.SLog;

import java.lang.ref.WeakReference;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an ImageView. It handles things like using a memory and disk
 * cache, running the work in a background thread and setting a placeholder image.
 */
public abstract class ImageWorker {
    private static final String TAG = "ImageWorker";
    public static final int FADE_IN_TIME = 200;

    private ImageCache mImageCache;
    private Bitmap mLoadingBitmap;
    private boolean mFadeInBitmap = true;
    private boolean mExitTasksEarly = false;
    protected Context mContext;


    private static final int CORE_POOL_SIZE = 5; // the number of threads that will be kept alive
    private static final int MAXIMUM_POOL_SIZE = 10; // should be chosen upon need
    private static final int KEEP_ALIVE = 30; // seconds
    private static final int REQUEST_QUEUE_SIZE = 32;
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LifoBlockingDeque<Runnable>(REQUEST_QUEUE_SIZE);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "ModernAsyncTask-ImageWorker #" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    private static volatile Executor sDefaultExecutor = THREAD_POOL_EXECUTOR;


    protected ImageWorker(Context context) {
        mContext = context;
        sDefaultExecutor = THREAD_POOL_EXECUTOR;
    }


    /**
     * Load an image specified by the data parameter into an ImageView (override {@link com.gdg.andconlab.cache.ImageWorker#processBitmap(Object)} to define the processing logic).
     * A memory and disk cache will be used if an {@link ImageCache} has been set using {@link com.gdg.andconlab.cache.ImageWorker#setImageCache(ImageCache)}. If the image is
     * found in the memory cache, it is set immediately, otherwise an {@link android.os.AsyncTask} will be created to asynchronously load the bitmap.
     *
     * @param data      The URL of the image to download
     * @param imageView The ImageView to bind the downloaded image to
     */
    public void loadImage(Object data, ImageView imageView) {
        loadImage(data, imageView, -1);
    }


    /**
     * Load an image specified by the data parameter into an ImageView (override {@link com.gdg.andconlab.cache.ImageWorker#processBitmap(Object)} to define the processing logic).
     * A memory and disk cache will be used if an {@link ImageCache} has been set using {@link com.gdg.andconlab.cache.ImageWorker#setImageCache(ImageCache)}. If the image is
     * found in the memory cache, it is set immediately, otherwise an {@link android.os.AsyncTask} will be created to asynchronously load the bitmap.
     *
     * @param data           The URL of the image to download
     * @param imageView      The ImageView to bind the downloaded image to
     * @param defaultImageId The default image id that should be assigned to the image during image retrieval
     */
    public void loadImage(Object data, ImageView imageView, int defaultImageId) {
        loadImage(data, imageView, defaultImageId, -1, null);
    }


    /**
     * Load an image specified by the data parameter into an ImageView (override {@link com.gdg.andconlab.cache.ImageWorker#processBitmap(Object)} to define the processing logic).
     * A memory and disk cache will be used if an {@link ImageCache} has been set using {@link com.gdg.andconlab.cache.ImageWorker#setImageCache(ImageCache)}. If the image is
     * found in the memory cache, it is set immediately, otherwise an {@link android.os.AsyncTask} will be created to asynchronously load the bitmap.
     *
     * @param data           The URL of the image to download
     * @param imageView      The ImageView to bind the downloaded image to
     * @param defaultImageId The default image id that should be assigned to the image during image retrieval
     * @param requestCode    The request code that will be sent back through the {@code imageReceiver}
     * @param imageReceiver  Callback that will be invoked when bitmap is retrieved
     */
    public void loadImage(Object data, ImageView imageView, int defaultImageId, int requestCode, ImageReceiver imageReceiver) {
        loadImage(data, imageView, defaultImageId, requestCode, imageReceiver, null);
    }

    /**
     * Load an image specified by the data parameter into an ImageView (override {@link com.gdg.andconlab.cache.ImageWorker#processBitmap(Object)} to define the processing logic).
     * A memory and disk cache will be used if an {@link ImageCache} has been set using {@link com.gdg.andconlab.cache.ImageWorker#setImageCache(ImageCache)}. If the image is
     * found in the memory cache, it is set immediately, otherwise an {@link android.os.AsyncTask} will be created to asynchronously load the bitmap.
     *
     * @param data           The URL of the image to download
     * @param imageView      The ImageView to bind the downloaded image to
     * @param defaultImageId The default image id that should be assigned to the image during image retrieval
     * @param requestCode    The request code that will be sent back through the {@code imageReceiver}
     * @param imageReceiver  Callback that will be invoked when bitmap is retrieved
     * @param executor       Execute task on this executor or on default if non was given
     */
    public void loadImage(Object data, ImageView imageView, int defaultImageId, int requestCode, ImageReceiver imageReceiver, Executor executor) {
        Bitmap bitmap = null;
        boolean hasDefaultImage = (defaultImageId >= 0);
        if (data == null || String.valueOf(data).equals("")) {
            if (hasDefaultImage) {
                data = defaultImageId;
            } else {
                bitmap = mLoadingBitmap;
            }
        }

        if (bitmap == null && mImageCache != null) {
            bitmap = mImageCache.getBitmapFromMemCache(String.valueOf(data));
        }

        if (bitmap != null) {
            // Bitmap found in memory cache
            if (imageReceiver != null) {
                // Let the listener handle the Bitmap-ImageView association
                imageReceiver.imageReceived(bitmap, requestCode);
            } else {
                setImageBitmap(imageView, bitmap);
            }
        } else if (cancelPotentialWork(data, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, requestCode, imageReceiver);
            if (imageView != null) {
                try {
                    final AsyncDrawable asyncDrawable;
                    if (hasDefaultImage) {
                        asyncDrawable = new AsyncDrawable(mContext.getResources(), defaultImageId, task);
                    } else {
                        asyncDrawable = new AsyncDrawable(mContext.getResources(), mLoadingBitmap, task);
                    }

                    imageView.setImageDrawable(asyncDrawable);
                } catch (Exception e) {
                    SLog.e(TAG, e, "Couldn't find resource id");
                }
            }

            if (data != null && !String.valueOf(data).equals("")) {
                try {
                    task.executeOnExecutor((executor != null) ? executor : sDefaultExecutor, data);
                } catch (Exception e) {
                    SLog.e(TAG, "Couldn't execute BitmapWorkerTask probably due to creating far too many AsyncTasks than it is allowed to", e);
                }
            }
        }
    }

    /**
     * Get image synchronously
     *
     * @param data
     * @return
     */
    public Bitmap getImage(Object data) {
        Bitmap bitmap = null;
        if (mImageCache != null) {
            bitmap = mImageCache.getBitmapFromMemCache(String.valueOf(data));
        }

        if (bitmap == null) {
            bitmap = mImageCache.getBitmapFromDiskCache(String.valueOf(data));
        }

        if (bitmap == null) {
            bitmap = processBitmap(data);
            // If the bitmap was processed and the image cache is available, then add the processed
            // bitmap to the cache for future use
            if (bitmap != null && mImageCache != null) {
                mImageCache.addBitmapToCache(String.valueOf(data), bitmap);
            }
        }

        return bitmap;
    }


    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param bitmap
     */
    public void setLoadingImage(Bitmap bitmap) {
        mLoadingBitmap = bitmap;
    }


    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
    public void setLoadingImage(int resId) {
        mLoadingBitmap = BitmapUtils.decodeResource(mContext.getResources(), resId);
    }


    /**
     * Set the {@link ImageCache} object to use with this ImageWorker.
     *
     * @param cacheCallback
     */
    public void setImageCache(ImageCache cacheCallback) {
        mImageCache = cacheCallback;
    }


    public ImageCache getImageCache() {
        return mImageCache;
    }


    /**
     * If set to true, the image will fade-in once it has been loaded by the background thread.
     *
     * @param fadeIn
     */
    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }


    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
    }


    /**
     * Subclasses should override this to define any processing or work that must happen to produce the final bitmap. This will be executed in a background
     * thread and be long running. For example, you could resize a large bitmap here, or pull down an image from the network.
     *
     * @param data The data to identify which image to process, as provided by {@link #loadImage(Object, android.widget.ImageView)}
     * @return The processed bitmap
     */
    protected abstract Bitmap processBitmap(Object data);


    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            if (BuildConfig.DEBUG) {
                final Object bitmapData = bitmapWorkerTask.data;
                SLog.d(TAG, "cancelWork - cancelled work for " + bitmapData);
            }
        }
    }


    /**
     * Returns true if the current work has been canceled or if there was no work in progress on this image view. Returns false if the work in progress
     * deals with the same data. The work is not stopped in that case.
     */
    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.data;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
                SLog.d(TAG, "cancelPotentialWork - cancelled work for " + data);
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }


    /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView. null if there is no such task.
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class BitmapWorkerTask extends ModernAsyncTask<Object, Void, Bitmap> {
        private Object data;
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<ImageReceiver> receiverReference;
        private final int requestCode;


        public BitmapWorkerTask(ImageView imageView, int requestCode, ImageReceiver receiver) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.requestCode = requestCode;

            if (receiver != null) {
                receiverReference = new WeakReference<ImageReceiver>(receiver);
            } else {
                receiverReference = null;
            }
        }


        /**
         * Background processing.
         */
        @Override
        protected Bitmap doInBackground(Object... params) {
            data = params[0];
            final String dataString = String.valueOf(data);
            Bitmap bitmap = null;

            // If the image cache is available and this task has not been cancelled by another
            // thread and the ImageView that was originally bound to this task is still bound back
            // to this task and our "exit early" flag is not set then try and fetch the bitmap from
            // the cache
            if (mImageCache != null && !isCancelled() && getAttachedImageView() != null
                    && !mExitTasksEarly) {
                bitmap = mImageCache.getBitmapFromDiskCache(dataString);
            }

            // If the bitmap was not found in the cache and this task has not been cancelled by
            // another thread and the ImageView that was originally bound to this task is still
            // bound back to this task and our "exit early" flag is not set, then call the main
            // process method (as implemented by a subclass)
            if (bitmap == null && !isCancelled() && getAttachedImageView() != null
                    && !mExitTasksEarly) {
                bitmap = processBitmap(params[0]);
            }

            // If the bitmap was processed and the image cache is available, then add the processed
            // bitmap to the cache for future use. Note we don't check if the task was cancelled
            // here, if it was, and the thread is still running, we may as well add the processed
            // bitmap to our cache as it might be used again in the future
            if (bitmap != null && mImageCache != null) {
                mImageCache.addBitmapToCache(dataString, bitmap);
            }

            return bitmap;
        }


        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // if cancel was called on this task or the "exit early" flag is set then we're done
            if (isCancelled() || mExitTasksEarly) {
                bitmap = null;
            }

            final ImageReceiver receiver = (receiverReference != null) ? receiverReference.get() : null;
            if (receiver == null) {
                final ImageView imageView = getAttachedImageView();
                if (bitmap != null && imageView != null) {
                    setImageBitmap(imageView, bitmap);
                }
            } else {
                // Let the listener handle the Bitmap-ImageView association
                receiver.imageReceived(bitmap, requestCode);
            }
        }


        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    /**
     * A custom Drawable that will be attached to the imageView while the work is in progress. Contains a reference to the actual worker task, so that it
     * can be stopped if a new binding is required, and makes sure that only the last started worker process can bind its result, independently of the
     * finish order.
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, int resId, BitmapWorkerTask bitmapWorkerTask) {
            super(res, (resId > 0) ? BitmapUtils.decodeResource(res, resId) : null);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);

            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }


        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }


    /**
     * Called when the processing is complete and the final bitmap should be set on the ImageView.
     *
     * @param imageView
     * @param bitmap
     */
    private void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        if (imageView == null) {
            return;
        }

        if (isDuplicateBitmaps(imageView, bitmap)) {
            SLog.d(TAG, "duplicate images detected. Ignoring request");
            return;
        }

        if (mFadeInBitmap) {
            // Transition drawable with a transparent drawable and the final bitmap
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[]{
                            new ColorDrawable(android.R.color.transparent),
                            new BitmapDrawable(mContext.getResources(), bitmap)
                    });

            if (imageView instanceof SafeImageView) {
                ((SafeImageView) imageView).setImageDrawableNoLayout(td);
            } else {
                imageView.setImageDrawable(td);
            }

            td.startTransition(FADE_IN_TIME);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Check for duplicate bitmaps
     *
     * @param imageView
     * @param newBitmap
     * @return
     */
    private boolean isDuplicateBitmaps(ImageView imageView, Bitmap newBitmap) {
        if (imageView == null) {
            return false;
        }

        Drawable drawable = imageView.getDrawable();
        Drawable imageDrawable = null;
        if (drawable != null && drawable instanceof TransitionDrawable) {
            imageDrawable = ((TransitionDrawable) drawable).getDrawable(1);
        }

        if (imageDrawable != null && imageDrawable instanceof BitmapDrawable) {
            Bitmap oldBitmap = ((BitmapDrawable) imageDrawable).getBitmap();
            if (oldBitmap != null && oldBitmap.equals(newBitmap)) {
                // duplicate images detected. no need to refresh the image
                return true;
            }
        }

        return false;
    }

    public interface ImageReceiver {
        public void imageReceived(Bitmap bitmap, int requestCode);
    }
}
