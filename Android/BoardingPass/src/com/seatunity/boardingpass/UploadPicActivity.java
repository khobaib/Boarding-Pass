package com.seatunity.boardingpass;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.utilty.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class UploadPicActivity extends Activity {

	private Uri mImageCaptureUri;
	private File picFile;
	private static final int CAMERA_REQ_CODE = 901;
	private static final int CROP_REQ_CODE = 1001;
	private static final int GALLERY_REQ_CODE = 902;
	Bitmap scaledBmp;
	ImageView iv_profile_pic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.upload_pic);
		iv_profile_pic=(ImageView) findViewById(R.id.iv_profile_pic);

	}

	@Override
	protected void onStart() {
		super.onStart();

	}


	public void onClickTakePic(View v) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			File directory = Constants.APP_DIRECTORY;
			  if(directory.exists()){
				  directory.delete();
			  }
			  directory.mkdirs(); 
			String mainDir = directory.toString();
			String photoFileName = "profile_pic_"+System.currentTimeMillis() + ".png";
			picFile = new File(mainDir, photoFileName);
			if (isSDCardMounted()) {
				mImageCaptureUri = Uri.fromFile(picFile);
			} else {
				Toast.makeText(UploadPicActivity.this, "Media Not Mounted!",
						Toast.LENGTH_LONG).show();
				return;
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, CAMERA_REQ_CODE);
		} catch (ActivityNotFoundException e) {
			Log.d("Error", "Activity Not Found" + e.toString());
		}
	}

	public void onClickGoToGallery(View v) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, GALLERY_REQ_CODE);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CAMERA_REQ_CODE:

				try {
					Bitmap bmp = decodeFile(picFile, 500);
					;
					int w = bmp.getWidth();
					if (w < 200)
						w = 200;
					int h = bmp.getHeight();
					if (h < 200)
						h = 200;
					scaledBmp = Bitmap.createScaledBitmap(bmp, w, h, true);


					try {
						FileOutputStream out = new FileOutputStream(picFile);
						scaledBmp.compress(Bitmap.CompressFormat.PNG, 90, out);
						mImageCaptureUri = Uri.fromFile(picFile);
						out.close();
						iv_profile_pic.setImageBitmap(scaledBmp);
					} catch (Exception e) {
						
					}
					// code b4 crop: ivProfilePic.setImageBitmap(scaledBmp);
					// btnUpdate.setVisibility(View.VISIBLE);
				} catch (OutOfMemoryError oom) {
					Toast.makeText(UploadPicActivity.this,
							"OOM error - Failed to load", Toast.LENGTH_SHORT)
							.show();
					Log.e("Camera", oom.toString());
				}

				//
				// doCrop();
				// imageUriToString = mImageCaptureUri.toString();
				break;

			case GALLERY_REQ_CODE:
				try {
					mImageCaptureUri = data.getData();

					if (getPath(mImageCaptureUri) != null) {
						

						InputStream inputStream = getContentResolver()
								.openInputStream(mImageCaptureUri);
						BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
						Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
						int w = bmp.getWidth();
						//if (w < 200)
							w = 200;
						int h = bmp.getHeight();
						//if (h < 200)
							h = 200;
						scaledBmp = Bitmap.createScaledBitmap(bmp, w, h, true);
						iv_profile_pic.setImageBitmap(scaledBmp);
						Toast.makeText(UploadPicActivity.this, "inside "+scaledBmp.getHeight(), 2000).show();
						FileOutputStream fileOutputStream = new FileOutputStream(
								picFile);
						copyStream(inputStream, fileOutputStream);
						fileOutputStream.close();
						inputStream.close();
						


					} else {
						System.out.println("Picasa Image!");
						Toast.makeText(UploadPicActivity.this, "outside", 2000).show();

						loadPicasaImageFromGallery(mImageCaptureUri);

					}

				} catch (Exception e) {

				}
				// doCrop();
				// imageUriToString = mImageCaptureUri.toString();
				break;

			case CROP_REQ_CODE:

				break;

			}
		}
	}




	private void loadPicasaImageFromGallery(final Uri uri) {
		String[] projection = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
			if (columnIndex != -1) {
				new Thread(new Runnable() {
					public void run() {
						try {
							Bitmap bitmap = android.provider.MediaStore.Images.Media
									.getBitmap(getContentResolver(), uri);
							scaledBmp = Bitmap.createScaledBitmap(bitmap, 200,
									200, true);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										FileOutputStream out = new FileOutputStream(
												picFile);
										scaledBmp.compress(
												Bitmap.CompressFormat.PNG, 90,
												out);
										mImageCaptureUri = Uri
												.fromFile(picFile);
										out.close();
										
										 iv_profile_pic.setImageBitmap(scaledBmp);
										// btnUpdate.setVisibility(View.VISIBLE);
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							});

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}).start();
			}
		}
		cursor.close();

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		} else
			return uri.getPath();
	}

	public void onClickUpdate(View v) {
		if (scaledBmp == null) {
			alert("Please update image first.");
			return;
		}

		// finish();
		// overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
			finish();
			// BugSenseHandler.closeSession(UploadPicActivity.this);

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(
				UploadPicActivity.this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		bld.create().show();
	}

	private Bitmap decodeFile(File f, int imageQuality) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = imageQuality;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;

			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			Log.i("SCALE", "scale = " + scale);

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isSDCardMounted() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}

	}

}


