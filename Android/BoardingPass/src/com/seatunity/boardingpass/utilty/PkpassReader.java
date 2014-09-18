package com.seatunity.boardingpass.utilty;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

/**
 * This class contains all the utility methods to read a pkpass file using
 * mainly the zip-utilities of Java.
 * 
 * @author Dell
 * 
 */
public class PkpassReader {
	private static String path = Environment.getExternalStorageDirectory() + "/pass/";
	private static String passFileName = "pass.pkpass";

	/**
	 * @debug This method was written for debugging purposes, which actually
	 *        logs & returns the barcode's json-string
	 * @param fileName
	 * @return
	 */
	public static String getFormattedStringFromPassFile(String fileName) {

		JSONObject jObj = getJSONFromPass(fileName);
		String retStr = "";
		try {
			retStr += "\n barcode: " + jObj.get("barcode");
		} catch (JSONException e) {
			Log.e("getFormattedStringFromPassFile", "JSONException", e);
		}
		return retStr;
	}

	/**
	 * Just pass the required file's(pass, manifest etc.) name, while saving the
	 * pkpass file's absolute path in the global path variable - then I'll
	 * return you the whole {@link JSONObject} extracted from the required file
	 * using mainly the zip-utilities of Java.
	 * 
	 * @param fileName
	 * @return
	 */
	private static JSONObject getJSONFromPass(String fileName) {
		InputStream is;
		ZipInputStream zis;
		Log.d("Touhid", fileName);
		try {
			String zfileName;
			is = new FileInputStream(path + passFileName);
			zis = new ZipInputStream(new BufferedInputStream(is));
			ZipEntry ze;
			byte[] buffer = new byte[8192];
			int count;

			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory())
					continue;
				zfileName = ze.getName();
				System.out.println("Reading file: " + zfileName);

				if (zfileName.startsWith(fileName)) {
					FileOutputStream fout = new FileOutputStream(path + zfileName);
					while ((count = zis.read(buffer)) != -1) {
						fout.write(buffer, 0, count);
					}
					fout.close();
					zis.closeEntry();
					Log.d("Touhid_uzip", "ZipInputStream (zis) entry closed.\nNow reading text...");
					FileInputStream stream = new FileInputStream(new File(path + zfileName));
					String jsonStr = null;
					try {
						FileChannel fc = stream.getChannel();
						MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
						jsonStr = Charset.defaultCharset().decode(bb).toString();
						JSONObject jObj = new JSONObject(jsonStr);
						Log.i("Touhid_uzip", "Returning pass.json: " + jsonStr);
						return jObj;
					} catch (Exception e) {
						Log.e("Touhid_uzip_Excp", "JSONException/IOException");
					} finally {
						stream.close();
					}
				}

			}
			zis.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * Static function returning the barcode information as a {@link JSONObject}
	 * , which is formatted as described <a href=
	 * "https://developer.apple.com/library/ios/documentation/userexperience/Reference/PassKit_Bundle/Chapters/LowerLevel.html#//apple_ref/doc/uid/TP40012026-CH3-SW3"
	 * >here.</a>
	 * 
	 * @param pkpassFilePath
	 *            The absolute file path of the {@code*.pkpass} file, usually
	 *            got from {@code file.getAbsolutePath()}, where {@code file} is
	 *            the {@link File} object of type {@code*.pkpass} only.
	 * @return {@link JSONObject} formatted as described <a href=
	 *         "https://developer.apple.com/library/ios/documentation/userexperience/Reference/PassKit_Bundle/Chapters/LowerLevel.html#//apple_ref/doc/uid/TP40012026-CH3-SW3"
	 *         >here</a>, or {@code  null} if file is invalid or any other error
	 *         occurs.
	 * @throws JSONException
	 *             if the pass.json is ill-formed or doesn't have the field
	 *             "barcode" as defined <a href=
	 *             "https://developer.apple.com/library/ios/documentation/userexperience/Reference/PassKit_Bundle/Chapters/LowerLevel.html#//apple_ref/doc/uid/TP40012026-CH3-SW3"
	 *             >here</a>.
	 * */
	public static JSONObject getPassbookBarcode(String pkpassFilePath) throws JSONException {
		path = "";
		StringTokenizer tokens = new StringTokenizer(pkpassFilePath, "/");
		while (tokens.hasMoreTokens()) {
			String str = tokens.nextToken();
			passFileName = str;
			if (!pkpassFilePath.equals(path + "/" + str))
				path += "/" + str;
		}
		path += "/";
		Log.d("getPassbookBarcode", path + ", " + passFileName);
		return (JSONObject) getJSONFromPass("pass").get("barcode");
	}

	/**
	 * Static function returning the barcode information as a String formattable
	 * as {@link JSONObject} , which can be formatted as described <a href=
	 * "https://developer.apple.com/library/ios/documentation/userexperience/Reference/PassKit_Bundle/Chapters/LowerLevel.html#//apple_ref/doc/uid/TP40012026-CH3-SW3"
	 * >here</a>.
	 * 
	 * @param pkpassFilePath
	 *            The absolute file path of the {@code*.pkpass} file, usually
	 *            got from {@code file.getAbsolutePath()}, where {@code file} is
	 *            the {@link File} object of type {@code*.pkpass} only.
	 * @return String Formattable as a {@link JSONObject}
	 * 
	 * @throws JSONException
	 *             if the pass.json is ill-formed or doesn't have the field
	 *             "barcode" as defined <a href=
	 *             "https://developer.apple.com/library/ios/documentation/userexperience/Reference/PassKit_Bundle/Chapters/LowerLevel.html#//apple_ref/doc/uid/TP40012026-CH3-SW3"
	 *             >here</a>.
	 * */
	public static String getPassbookBarcodeString(String pkpassFilePath) throws JSONException {
		path = "";
		StringTokenizer tokens = new StringTokenizer(pkpassFilePath, "/");
		while (tokens.hasMoreTokens()) {
			String str = tokens.nextToken();
			passFileName = str;
			if (!pkpassFilePath.equals(path + "/" + str))
				path += "/" + str;
		}
		path += "/";
		Log.d("getPassbookBarcodeString", path + ", " + passFileName);
		String barcodeStr = getJSONFromPass("pass").get("barcode").toString();
		Log.i("T_pass_barode", "Got brcode :D :: " + barcodeStr);
		return barcodeStr;
	}

}
