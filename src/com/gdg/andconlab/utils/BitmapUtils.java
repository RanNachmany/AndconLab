
package com.gdg.andconlab.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;
import com.gdg.andconlab.App;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;


/**
 * @author Amir Lazarovich
 * @version 0.1
 */
public class BitmapUtils
{
    private static final String TAG = "BitmapUtils";

    /**
	 * Recycle bitmap if necessary and possible
	 * 
	 * @param bitmap
	 */
	public static void recycle(Bitmap bitmap)
	{
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
	}


    /**
     * Recycle the underlying bitmap inside given drawable
     *
     * @param drawable
     */
    public static void recycle(Drawable drawable)
    {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            ((BitmapDrawable) drawable).getBitmap().recycle();
        }
    }

    /**
     * Safely recycle the selected image drawable
     *
     * @param view
     */
    public static void recycle(ImageView view) {
        if (view == null) {
            return;
        }

        Drawable drawable = view.getDrawable();
        view.setImageDrawable(null);
        recycle(drawable);
    }


	/**
	 * Taken from <a href="http://developer.android.com/training/displaying-bitmaps/load-bitmap.html">developer.android.com</a>
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight)
	{
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return decodeResource(res, resId, options);
	}


	/**
	 * Taken from <a href="http://developer.android.com/training/displaying-bitmaps/load-bitmap.html">developer.android.com</a>
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			}
			else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}


	/**
	 * Safely decode resource to bitmap
	 * 
	 * @param res
	 * @param resId
	 * @return
	 */
	public static Bitmap decodeResource(Resources res, int resId)
	{
		return decodeResource(res, resId, null);
	}


	/**
	 * Safely decode resource to bitmap
	 * 
	 * @param res
	 * @param resId
	 * @param options
	 * @return
	 */
	public static Bitmap decodeResource(Resources res, int resId, BitmapFactory.Options options)
	{
		Bitmap result = null;
		try {
			result = BitmapFactory.decodeResource(res, resId, options);
		}
		catch (OutOfMemoryError e) {
            SLog.e(TAG, "Couldn't decode bitmap from resource due to low memory, Clearing image cache..", e);
            App.getInstance().getImageManager().clearCache();
		}

		return result;
	}

    /**
     * Safely create bitmap
     *
     * @param source
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) {
        Bitmap result = null;

        try {
            result = Bitmap.createBitmap(source, x, y, width, height);
        }
        catch (OutOfMemoryError e) {
            SLog.e(TAG, "Couldn't create bitmap due to low memory, Clearing image cache..", e);
            App.getInstance().getImageManager().clearCache();
        }

        return result;
    }

    /**
     * Safely decode file to bitmap
     *
     * @param filePath
     * @return
     */
    public static Bitmap decodeFile(String filePath) {
        return decodeFile(filePath, null);
    }

    /**
     * Safely decode file to bitmap
     *
     * @param filePath
     * @param options
     * @return
     */
    public static Bitmap decodeFile(String filePath, BitmapFactory.Options options) {
        Bitmap result = null;

        try {
            result = BitmapFactory.decodeFile(filePath, options);
        }
        catch (OutOfMemoryError e) {
            SLog.e(TAG, "Couldn't decode file to bitmap due to low memory, Clearing image cache..", e);
            App.getInstance().getImageManager().clearCache();
        }

        return result;
    }

    /**
     * Safely decode stream to bitmap
     *
     * @param inputStream
     * @return
     */
    public static Bitmap decodeStream(InputStream inputStream) {
        return decodeStream(inputStream, null);
    }

    /**
     * Safely decode stream to bitmap
     *
     * @param inputStream
     * @param options
     * @return
     */
    public static Bitmap decodeStream(InputStream inputStream, BitmapFactory.Options options) {
        Bitmap result = null;

        try {
            result = BitmapFactory.decodeStream(inputStream, null, options);
        }
        catch (OutOfMemoryError e) {
            SLog.e(TAG, "Couldn't decode stream to bitmap due to low memory, Clearing image cache..", e);
            App.getInstance().getImageManager().clearCache();
        }

        return result;

    }

    /**
     * Safely calculate bitmap size from stream
     *
     * @param inputStream
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static BitmapFactory.Options calculateSampledBitmapFromStream(InputStream inputStream, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        decodeStream(inputStream, options);

        return options;
    }

    /**
     * Safely decode stream to bitmap while maintaining required dimensions
     *
     *
     * @param client
     * @param getRequest
     *@param inputStream
     * @param reqWidth
     * @param reqHeight    @return
     */
    public static Bitmap decodeSampledBitmapFromStream(HttpClient client, HttpGet getRequest, InputStream inputStream, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        decodeStream(inputStream, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Since the sampling phase depleted our InputStream we need to resend the server a request
        Bitmap sampledBitmap = null;
        HttpEntity entity = null;
        try {
            inputStream.close();
            HttpResponse response = client.execute(getRequest);

            entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                sampledBitmap = decodeStream(new FlushedInputStream(inputStream), options);
            }
        } catch (Exception e) {
            SLog.e(TAG, "Couldn't get input stream after calculated its sample size", e);
        } finally {
            try {
                if (entity != null) {
                    entity.consumeContent();
                }

                inputStream.close();
            } catch (Exception e) {}
        }

        return sampledBitmap;
    }

    /**
     * Safely decode drawable
     *
     * @param resources
     * @param resId
     * @return
     */
    public static Drawable getDrawable(Resources resources, int resId) {
        Drawable result = null;
        try {
            result = resources.getDrawable(resId);
        } catch (Exception e) {
            SLog.e(TAG, "Couldn't decode drawable", e);
        }

        return result;
    }

    /**
     * Check for duplicate bitmaps
     *
     * @param imageView
     * @param newBitmap
     * @return
     */
    public static boolean isDuplicateBitmaps(ImageView imageView, Bitmap newBitmap) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof TransitionDrawable) {
            Drawable imageDrawable = ((TransitionDrawable) drawable).getDrawable(1);
            if (imageDrawable != null && imageDrawable instanceof LayerDrawable) {
                imageDrawable = ((LayerDrawable) imageDrawable).getDrawable(0);
            }

            if (imageDrawable != null && imageDrawable instanceof BitmapDrawable) {
                Bitmap oldBitmap = ((BitmapDrawable) imageDrawable).getBitmap();
                if (oldBitmap != null && oldBitmap.equals(newBitmap)) {
                    // duplicate images detected. no need to refresh the image
                    return true;
                }
            }
        }

        return false;
    }
}
