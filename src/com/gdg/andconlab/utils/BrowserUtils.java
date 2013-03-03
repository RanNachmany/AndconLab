
package com.gdg.andconlab.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * @author Amir Lazarovich
 * @version 0.1
 */
public class BrowserUtils
{
	/**
	 * Opens the browser for the given url string.
	 */
	public static void openBrowserForUrl(Context context, String url) throws URISyntaxException
	{
		URI uri = new URI(url);

		if (android.text.TextUtils.isEmpty(uri.getScheme())) {
			url = "http://" + url;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		context.startActivity(intent);
	}
}
