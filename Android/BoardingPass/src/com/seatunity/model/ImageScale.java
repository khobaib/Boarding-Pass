package com.seatunity.model;

import java.io.File;
import java.io.FileInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

/**
 * This class holds the functions to scale or format image-files/bitmaps to a
 * desired bitmap.
 * 
 * @author Sumon
 * 
 */
public class ImageScale {

	/**
	 * Scales the file as a bitmap with max(width,height)=250
	 * 
	 * @param path
	 *            (absolute-path) of the image file
	 * @return a decoded Bitmap of size 250
	 */
	public Bitmap decodeFile(String path) {// you can provide file path here
		int orientation;
		try {
			if (path == null) {
				return null;
			}
			File file = new File(path);
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(new FileInputStream(file), null, o);
			final int REQUIRED_SIZE = 250;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			//
			// // decode image size
			// BitmapFactory.Options o = new BitmapFactory.Options();
			// o.inJustDecodeBounds = true;
			// // Find the correct scale value. It should be the power of 2.
			// final int REQUIRED_SIZE =120;
			// int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bm = BitmapFactory.decodeFile(path, o2);
			Bitmap bitmap = bm;

			ExifInterface exif = new ExifInterface(path);

			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

			Log.e("ExifInteface .........", "rotation =" + orientation);

			// exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

			Log.e("orientation", "" + orientation);
			Matrix m = new Matrix();

			if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
				m.postRotate(180);
				// m.postScale((float) bm.getWidth(), (float) bm.getHeight());
				// if(m.preRotate(90)){
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
				return bitmap;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				m.postRotate(90);
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
				return bitmap;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				m.postRotate(270);
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
				return bitmap;
			}
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Scales the file as a bitmap with max(width,height)=300
	 * 
	 * @param imagePath
	 *            (absolute-path) of the image file
	 * @return a decoded Bitmap of size 300
	 */
	public Bitmap decodeImagetoUpload(String imagePath) {
		Bitmap bitmap = null;

		try {

			File file = new File(imagePath);
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(new FileInputStream(file), null, o);
			final int REQUIRED_SIZE = 300;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;

			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp = width_tmp;
				scale *= 2;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inSampleSize = scale;
			bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
			Log.e("size", "" + bitmap.getWidth() + bitmap.getHeight());

		} catch (Exception e) {
			bitmap = null;
		}
		return bitmap;
	}

	/**
	 * Scales the file as a bitmap with max(width,height)=200
	 * 
	 * @param imagePath
	 *            (absolute-path) of the image file
	 * @return a decoded Bitmap of size 200
	 */
	public Bitmap decodeImage(String imagePath) {
		Bitmap bitmap = null;

		try {

			File file = new File(imagePath);
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(new FileInputStream(file), null, o);
			final int REQUIRED_SIZE = 200;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;

			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp = width_tmp;
				scale *= 2;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inSampleSize = scale;
			bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
			Log.e("size", "" + bitmap.getWidth() + bitmap.getHeight());

		} catch (Exception e) {
			bitmap = null;
		}
		return bitmap;
	}

	/**
	 * Scales the passed bitmap as a bitmap with max(width,height)=100
	 * 
	 * @param photo
	 *            to scale
	 * @return a decoded Bitmap of size 100
	 */
	public Bitmap decodeImageForProfileFromBitmap(Bitmap photo) {
		Bitmap bitmap = null;

		try {
			//
			// File file=new File(imagePath);
			// BitmapFactory.Options o = new BitmapFactory.Options();
			// // o.inJustDecodeBounds = true;
			// BitmapFactory.decodeStream(new FileInputStream(file),null,o);
			final int REQUIRED_SIZE = 100;
			int width_tmp = photo.getWidth(), height_tmp = photo.getHeight();
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp = width_tmp;
				scale *= 2;
			}
			bitmap = getResizedBitmap(photo, width_tmp / scale, width_tmp / scale);

			// BitmapFactory.Options options=new BitmapFactory.Options();
			//
			// options.inSampleSize=scale;
			// bitmap=BitmapFactory.decodeStream(new FileInputStream(file),
			// null, options);
			// Log.e("size",""+bitmap.getWidth()+bitmap.getHeight());

		} catch (Exception e) {
			bitmap = null;
		}
		return bitmap;
	}

	/**
	 * Scales the file as a bitmap with max(width,height)=120
	 * 
	 * @param imagePath
	 *            (absolute-path) of the image file
	 * @return a decoded Bitmap of size 120
	 */
	public Bitmap decodeImageForProfile(String imagePath) {
		Bitmap bitmap = null;

		try {

			File file = new File(imagePath);
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(new FileInputStream(file), null, o);
			final int REQUIRED_SIZE = 120;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;

			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp = width_tmp;
				scale *= 2;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inSampleSize = scale;
			bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
			Log.e("size", "" + bitmap.getWidth() + bitmap.getHeight());

		} catch (Exception e) {
			bitmap = null;
		}
		return bitmap;
	}

	/**
	 * @param bm
	 *            (Bitmap object) to resize
	 * @param newHeight
	 *            of the returned Bitmap
	 * @param newWidth
	 *            of the returned Bitmap
	 * @return the resized Bitmap object
	 */
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		Bitmap resizedBitmap = null;
		try {
			if (bm != null) {
				int width = bm.getWidth();
				int height = bm.getHeight();
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				// create a matrix for the manipulation
				Matrix matrix = new Matrix();
				// resize the bit map
				matrix.postScale(scaleWidth, scaleHeight);
				// recreate the new Bitmap
				resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

			}
		} catch (Exception e) {
			resizedBitmap = null;
		}

		return resizedBitmap;
	}

}