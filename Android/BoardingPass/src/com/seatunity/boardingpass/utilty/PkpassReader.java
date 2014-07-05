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

import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import android.os.Environment;
import android.util.Log;

public class PkpassReader {
	private static String path = Environment.getExternalStorageDirectory()
			+ "/pass/";
	private static String passFileName = "pass.pkpass";

public static String getFormattedStringFromPassFile(String fileName) {
	
//	path = "";
//	StringTokenizer tokens = new StringTokenizer(fileName, "/");
//	while (tokens.hasMoreTokens()){
//		String str = tokens.nextToken();
//		passFileName = str;
//		if(!fileName.equals(path+str))
//			path += "/"+str;
//	}
//	QRCodeMultiReader q=
		JSONObject jObj = getJSONFromPass(fileName);
		String retStr = "";
		try {
//			retStr = " description: " + jObj.get("description");
//			retStr += "\n formatVersion: " + jObj.get("formatVersion");
//			retStr += "\n organizationName: " + jObj.get("organizationName");
//			retStr = "\n passTypeIdentifier: " + jObj.get("passTypeIdentifier");
//			retStr += "\n serialNumber: " + jObj.get("serialNumber");
//			retStr += "\n teamIdentifier: " + jObj.get("teamIdentifier");
			retStr += "\n barcode: " + jObj.get("barcode");
		} catch (JSONException e) {
			Log.e("getFormattedStringFromPassFile", "JSONException", e);
		}
		return retStr;
	}

	private static JSONObject getJSONFromPass(String fileName) {
		InputStream is;
		ZipInputStream zis;
		Log.d("Touhid", fileName);
		try {
			String zfileName;
			is = new FileInputStream(fileName);
			zis = new ZipInputStream(new BufferedInputStream(is));
			ZipEntry ze;
			byte[] buffer = new byte[8192];
			int count;

			while ((ze = zis.getNextEntry()) != null) {
				// zapis do souboru
				if (ze.isDirectory())
					continue;
				zfileName = ze.getName();
				System.out.println("Reading file: " + zfileName);

				if (zfileName.startsWith(fileName)) {
					FileOutputStream fout = new FileOutputStream(path
							+ zfileName);
					Log.d("Touhid_uzip", "FileOutputStream (fout) made as: "
							+ path + zfileName);

					while ((count = zis.read(buffer)) != -1) {
						fout.write(buffer, 0, count);
					}

					Log.d("Touhid_uzip", "Closing fout...");
					fout.close();
					zis.closeEntry();
					Log.d("Touhid_uzip",
							"ZipInputStream (zis) entry closed.\nNow reading text...");
					FileInputStream stream = new FileInputStream(new File(path
							+ zfileName));
					String jsonStr = null;
					try {
						FileChannel fc = stream.getChannel();
						MappedByteBuffer bb = fc.map(
								FileChannel.MapMode.READ_ONLY, 0, fc.size());
						jsonStr = Charset.defaultCharset().decode(bb)
								.toString();
						JSONObject jObj = new JSONObject(jsonStr);
						return jObj;
					} catch (Exception e) {
						Log.e("Touhid_uzip_Excp", "JSONException/IOException");
					} finally {
						stream.close();
					}
				}

			}
			zis.close();
			Log.d("Touhid_uzip", "ZipInputStream (zis) closed.");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
