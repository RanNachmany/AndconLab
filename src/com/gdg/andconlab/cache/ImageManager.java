
package com.gdg.andconlab.cache;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.gdg.andconlab.App;
import com.gdg.andconlab.R.dimen;
import com.gdg.andconlab.utils.CompatibilityUtils;
import com.gdg.andconlab.utils.SLog;


/**
 * Handles image download and cache
 *
 * @author Amir Lazarovich
 * @version 0.1
 */
public class ImageManager {
    // /////////////////////////////////////////////
    // Constants
    // /////////////////////////////////////////////
    private static final String TAG = "ImageManager";
    private static final int BITS_IN_MB = 1048576; // 1024 * 1024
    private static final int DEFAULT_AVAILABLE_MEMORY = 32; // MB
    private static final float MAX_TESTED_MEMORY = 96f; // MB

    private static final String LOGO_CACHE_DIR = "logo";
    private static final String SPEAKER_CACHE_DIR = "speaker";

    // /////////////////////////////////////////////
    // Members
    // /////////////////////////////////////////////
    private final ImageFetcher logoFetcher;
    private final ImageFetcher speakerFetcher;

    public enum ImageType {
        LOGO,
        SPEAKER
    }

    // /////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////

    /**
     * Construct a new Cache Manager
     *
     * @param context
     */
    public ImageManager(Context context) {
        int totalAvailableMemoryInMB = CompatibilityUtils.getMemoryClass(context);
        if (totalAvailableMemoryInMB <= 0) {
            // memory class not found, using default value
            totalAvailableMemoryInMB = DEFAULT_AVAILABLE_MEMORY;
        }

        long totalAvailableMemoryInBits = BITS_IN_MB * totalAvailableMemoryInMB;
        int factor = (int) (MAX_TESTED_MEMORY / totalAvailableMemoryInMB);
        if (factor <= 0) {
            factor = 1;
        }

        SLog.i(TAG, "Total available memory in MB: %d. Computed factor: %d", totalAvailableMemoryInMB, factor);
        final Resources r = context.getResources();
        // define cache size according to total available memory
        // the values chosen below should be selected according to each application's need

        // logo cache
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(LOGO_CACHE_DIR);
        cacheParams.memCacheSize = (int) (totalAvailableMemoryInBits / (16 * factor));
        ImageCache logoCache = new ImageCache(context, cacheParams);

        int logoWidth = r.getDimensionPixelSize(dimen.event_list_item_image_width);
        int logoHeight = r.getDimensionPixelSize(dimen.event_list_item_image_height);
        logoFetcher = new ImageFetcher(App.getInstance(), logoWidth, logoHeight);
        logoFetcher.setImageCache(logoCache);

        // speaker cache
        ImageCache.ImageCacheParams speakerCacheParams = new ImageCache.ImageCacheParams(SPEAKER_CACHE_DIR);
        speakerCacheParams.memCacheSize = (int) (totalAvailableMemoryInBits / (16 * factor));
        ImageCache speakerLogoCache = new ImageCache(context, speakerCacheParams);

        int speakerWidth = r.getDimensionPixelSize(dimen.speaker_image_width);
        int speakerHeight = r.getDimensionPixelSize(dimen.speaker_image_height);
        speakerFetcher = new ImageFetcher(App.getInstance(), speakerWidth, speakerHeight);
        speakerFetcher.setImageCache(speakerLogoCache);
    }


    // /////////////////////////////////////////////
    // Public
    // /////////////////////////////////////////////
    public void setDefaultLogoImage(Bitmap img) {
        logoFetcher.setLoadingImage(img);
    }

    public void setDefaultSpeakerImage(Bitmap img) {
        speakerFetcher.setLoadingImage(img);
    }

    public void clearCache() {
        logoFetcher.getImageCache().clearCaches();
        speakerFetcher.getImageCache().clearCaches();
    }

    /**
     * Load image asynchronously. <br/>
     * First try fetching the image from the memory cache, then from the disk cache and then from the server.
     *
     * @param context
     * @param imageType    The type of image to load
     * @param url_or_resId
     * @param imageView
     */
    public static void loadImage(Context context, ImageType imageType, String url_or_resId, ImageView imageView) {
        if (context == null) {
            return;
        }

        final Context application = context.getApplicationContext();
        if (application instanceof ImageManagerHolder) {
            ImageManager imageManager = ((ImageManagerHolder) application).getImageManager();
            imageManager.loadImage(imageType, url_or_resId, imageView);
        } else {
            throw new IllegalArgumentException("Application context must implement ImageManagerHolder");
        }
    }

    /**
     * Load image asynchronously. <br/>
     * First try fetching the image from the memory cache, then from the disk cache and then from the server.
     *
     * @param imageType    The type of image to load
     * @param url_or_resId
     * @param imageView
     */
    public void loadImage(ImageType imageType, Object url_or_resId, ImageView imageView) {
        loadImage(imageType, url_or_resId, imageView, -1, null);
    }


    /**
     * Load image asynchronously. <br/>
     * First try fetching the image from the memory cache, then from the disk cache and then from the server.
     *
     * @param imageType     The type of image to load
     * @param url_or_resId
     * @param imageView
     * @param requestCode   The value that will be passed with the callback {@code imageReceiver}
     * @param imageReceiver A callback that will be invoked when image is retrieved
     */
    public void loadImage(ImageType imageType, Object url_or_resId, ImageView imageView, int requestCode, ImageWorker.ImageReceiver imageReceiver) {
        loadImage(imageType, url_or_resId, imageView, -1, requestCode, imageReceiver);
    }


    /**
     * Load image asynchronously. <br/>
     * First try fetching the image from the memory cache, then from the disk cache and then from the server.
     *
     * @param imageType      The type of image to load
     * @param url_or_resId
     * @param imageView
     * @param defaultImageId The default image that will be set in case no image was found
     */
    public void loadImage(ImageType imageType, Object url_or_resId, ImageView imageView, int defaultImageId) {
        loadImage(imageType, url_or_resId, imageView, defaultImageId, -1, null);
    }


    /**
     * Load image asynchronously. <br/>
     * First try fetching the image from the memory cache, then from the disk cache and then from the server.
     *
     * @param imageType      The type of image to load
     * @param url_or_resId
     * @param imageView
     * @param defaultImageId The default image that will be set in case no image was found
     * @param requestCode    The value that will be passed with the callback {@code imageReceiver}
     * @param imageReceiver  A callback that will be invoked when image is retrieved
     */
    public void loadImage(ImageType imageType, Object url_or_resId, ImageView imageView, int defaultImageId, int requestCode,
                          ImageWorker.ImageReceiver imageReceiver) {
        ImageFetcher imageFetcher = getImageFetcher(imageType);
        if (imageFetcher != null) {
            loadImage(imageFetcher, url_or_resId, imageView, defaultImageId, requestCode, imageReceiver);
        }
    }

    /**
     * Preload image for future uses
     *
     * @param imageType
     * @param url_or_resId
     */
    public void preloadImage(ImageType imageType, String url_or_resId) {
        ImageFetcher imageFetcher = getImageFetcher(imageType);
        if (imageFetcher != null) {
            imageFetcher.loadImage(url_or_resId, null);
        }
    }

    public Bitmap getImage(ImageType imageType, Object url_or_resId) {
        ImageFetcher imageFetcher = getImageFetcher(imageType);
        if (imageFetcher == null) {
            return null;
        }

        return imageFetcher.getImage(url_or_resId);
    }

    // /////////////////////////////////////////////
    // Getters & Setters
    // /////////////////////////////////////////////
    public ImageFetcher getLogoFetcher() {
        return logoFetcher;
    }


    // /////////////////////////////////////////////
    // Private
    // /////////////////////////////////////////////
    private ImageFetcher getImageFetcher(ImageType imageType) {
        switch (imageType) {
            case LOGO:
                return logoFetcher;

            case SPEAKER:
                return speakerFetcher;

            default:
                return null;
        }
    }

    /**
     * Load image
     *
     * @param fetcher
     * @param url_or_resId
     * @param imageView
     * @param defaultImageId
     * @param requestCode
     * @param imageReceiver
     */
    private void loadImage(ImageFetcher fetcher, Object url_or_resId, ImageView imageView, int defaultImageId, int requestCode,
                           ImageWorker.ImageReceiver imageReceiver) {
        fetcher.loadImage(url_or_resId, imageView, defaultImageId, requestCode, imageReceiver);
    }

    //////////////////////////////////////////
    // Inner classes
    //////////////////////////////////////////
    public interface ImageManagerHolder {
        /**
         * Get the {@link ImageManager} that handles asynchronous image download and cache
         *
         * @return
         */
        ImageManager getImageManager();
    }
}
