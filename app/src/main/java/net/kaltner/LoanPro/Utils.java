package net.kaltner.LoanPro;

import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.SharedPreferences;
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
				InputStream is = am.open("Changelog.txt");
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				StringBuilder message = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
				    message.append(line + "\r\n");
				}
				r.close();
				is.close();

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Changelog")
					.setMessage(message.toString())
				    .setCancelable(false)
				    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    	public void onClick(DialogInterface dialog, int id) {
				    		dialog.cancel();
				    	}
				    });
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
		catch(Exception e) {

		}
	}
}
