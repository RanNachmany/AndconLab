package com.gdg.andconlab.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Amir Lazarovich
 * @version 0.1
 */
public class PackageUtils {
    private static final String TAG = "PackageUtils";

    /**
     * Get application version code
     *
     * @param context
     * @return
     */
    public static int getApplicationVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            SLog.e(TAG, "Couldn't find package name", e);
        }

        return versionCode;
    }

    /**
     * Get application hash key
     *
     * @param context
     */
    public static String getApplicationHashKey(Context context) {
        String hashKey = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;

                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashKey = new String(Base64.encode(md.digest(), 0));
                SLog.i(TAG, "Application hash key: %s", hashKey);
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            SLog.e(TAG, "Couldn't get hash key: name not found", e);
        }
        catch (NoSuchAlgorithmException e) {
            SLog.e(TAG, "Couldn't get hash key: no such an algorithm", e);
        }
        catch (Exception e){
            SLog.e(TAG, "Couldn't get hash key", e);
        }

        return hashKey;
    }

    /**
     * Check if <code>packageName</code> exists
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isPackageExists(Context context, String packageName)
    {
        try
        {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }
}
