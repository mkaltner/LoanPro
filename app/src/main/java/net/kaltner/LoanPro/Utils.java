package net.kaltner.LoanPro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.content.pm.PackageInfoCompat;

public class Utils
{
	private static SharedPreferences getDefaultSharedPreferences(Context context) {
		return context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
	}

	public static void ShowAlert(Context context, String message)
	{
		new AlertDialog.Builder(context)
		  .setMessage(message)
		  .setPositiveButton("OK", null)
		  .show();
    }

	public static void showHtmlDialog(Context context, String title, String html) {
		new AlertDialog.Builder(context)
			.setTitle(title)
			.setView(createHtmlView(context, html))
			.setPositiveButton("OK", null)
			.show();
	}

	private static int dpToPx(Context context, int dp) {
		return (int)(dp * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	public static void ShowToast(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void ShowLongToast(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void savePreferenceBoolean(Context context, String key, boolean value) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void savePreferenceFloat(Context context, String key, float value) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public static void savePreferenceDouble(Context context, String key, double value) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, Double.toString(value));
		editor.commit();
	}

	public static void savePreferenceInt(Context context, String key, int value) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void savePreferenceLong(Context context, String key, long value) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static void savePreferenceString(Context context, String key, String value) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void removePreference(Context context, String key) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(key);
		editor.commit();
	}

	public static boolean getPreferenceBoolean(Context context, String key) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		return settings.getBoolean(key, false);
	}

	public static float getPreferenceFloat(Context context, String key) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		return settings.getFloat(key, Float.MIN_VALUE);
	}

	public static double getPreferenceDouble(Context context, String key) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		Object value = settings.getAll().get(key);

		if (value instanceof String) {
			try {
				return Double.parseDouble((String)value);
			}
			catch (NumberFormatException e) {
				return Double.NaN;
			}
		}

		if (value instanceof Number) {
			return ((Number)value).doubleValue();
		}

		return Double.NaN;
	}

	public static int getPreferenceInt(Context context, String key) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		return settings.getInt(key, Integer.MIN_VALUE);
	}

	public static long getPreferenceLong(Context context, String key) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		return settings.getLong(key, Long.MIN_VALUE);
	}

	public static String getPreferenceString(Context context, String key) {
		SharedPreferences settings = getDefaultSharedPreferences(context);
		return settings.getString(key, "");
	}

	public static String readTextFile(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		}
		catch (IOException e) {

		}
		return outputStream.toString();
	}

	public static void showChangeLog(Context context) {
		try {
			int lastVersionCode = Utils.getPreferenceInt(context, "versionCode");

			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			int versionCode = (int) PackageInfoCompat.getLongVersionCode(pInfo);

			if (lastVersionCode < versionCode) {
				Utils.savePreferenceInt(context, "versionCode", versionCode);

				AssetManager am = context.getAssets();
				InputStream is = am.open("Changelog.htm");
				String html = Utils.readTextFile(is);

				AlertDialog alert = new AlertDialog.Builder(context)
					.setTitle("What's New")
					.setView(createHtmlView(context, html))
				    .setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					})
					.create();
				alert.show();
			}
		}
		catch(Exception e) {

		}
	}

	private static FrameLayout createHtmlView(Context context, String html) {
		WebView webView = new WebView(context);
		webView.setBackgroundColor(Color.TRANSPARENT);
		webView.setPadding(dpToPx(context, 18), dpToPx(context, 12), dpToPx(context, 18), 0);
		webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

		WebSettings settings = webView.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		settings.setTextZoom(100);

		FrameLayout container = new FrameLayout(context);
		container.addView(webView, new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT));
		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

		return container;
	}
}
