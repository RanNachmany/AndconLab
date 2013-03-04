package com.gdg.andconlab;

import android.app.Application;
import com.gdg.andconlab.cache.ImageManager;
import com.gdg.andconlab.cache.ImageManager.ImageManagerHolder;
import com.gdg.andconlab.utils.SLog;

/**
 * @author Amir Lazarovich
 */
public class App extends Application implements ImageManagerHolder {

    ///////////////////////////////////////////////
    // Constants
    ///////////////////////////////////////////////
    private static final String TAG = "App";

    ///////////////////////////////////////////////
    // Members
    ///////////////////////////////////////////////
    private static App instance;
    private ImageManager imageManager;

    ///////////////////////////////////////////////
    // Overrides & Implementations
    ///////////////////////////////////////////////

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        imageManager = new ImageManager(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        SLog.d(TAG, "Application low on memory - clearing image cache");

        imageManager.clearCache();
    }

    ///////////////////////////////////////////////
    // Getters & Setters
    ///////////////////////////////////////////////

    /**
     * Get the (only) instance of this class
     *
     * @return
     */
    public static App getInstance() {
        return instance;
    }

    @Override
    public ImageManager getImageManager() {
        return imageManager;
    }
}
