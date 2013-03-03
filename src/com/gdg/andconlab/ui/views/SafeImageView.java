package com.gdg.andconlab.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.gdg.andconlab.utils.SLog;

/**
 * Same like {@link ImageView} with only two small changes:<br/>
 * <ul>
 *     <li>Implementing Ran Nachmany's drawable pattern to avoid unnecessary layout requests</li>
 *     <li>Safely drawing to avoid drawing recycled bitmaps</li>
 * </ul>
 *
 * @author Amir Lazarovich
 */
public class SafeImageView extends ImageView {
    ///////////////////////////////////////////////
    // Constants
    ///////////////////////////////////////////////
    private static final String TAG = "SafeImageView";

    ///////////////////////////////////////////////
    // Members
    ///////////////////////////////////////////////
    private boolean mPreventLayout;
    ///////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////

    public SafeImageView(Context context) {
        super(context);
    }

    public SafeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SafeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    ///////////////////////////////////////////////
    // Public
    ///////////////////////////////////////////////

    public void setImageDrawableNoLayout(Drawable drawable) {
        mPreventLayout = true;
        setImageDrawable(drawable);
        mPreventLayout = false;
    }

    ///////////////////////////////////////////////
    // Overrides & Implementations
    ///////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            SLog.e(TAG, e, "Couldn't draw image view. Probably because its underlying image was recycled - clearing drawable");
            setImageDrawable(null);
        }
    }

    @Override
    public void requestLayout() {
        if (!mPreventLayout) super.requestLayout();
    }

///////////////////////////////////////////////
    // Getters & Setters
    ///////////////////////////////////////////////


    ///////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////

}
