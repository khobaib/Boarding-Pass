package com.seatunity.boardingpass.utilty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.widget.Toast;

/**
 * @author Touhid
 * 
 */
public class DialogViewer {

	public static void toastConnectionTimeOut(Activity activity) {
		final Context ctx = activity;
		if (Looper.myLooper() != Looper.getMainLooper())
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							ctx,
							"Connection to server is lost!\nPlease try again later.",
							Toast.LENGTH_LONG).show();
				}
			});
		else
			Toast.makeText(ctx,
					"Connection to server is lost!\nPlease try again later.",
					Toast.LENGTH_LONG).show();
	}

	public static void toastSimple(Activity activity, final String msg) {
		final Context ctx = activity;
		if (Looper.myLooper() != Looper.getMainLooper())
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
				}
			});
		else
			Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}

	public static void alertSimple(Activity activity, String message) {
		final AlertDialog.Builder bld = new AlertDialog.Builder(activity);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		if (Looper.myLooper() == Looper.getMainLooper())
			bld.create().show();
		else
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					bld.create().show();
				}
			});
	}
}

// public static void editStatusDialog(final Activity activity) {
// final Dialog verifDialog = new Dialog((Context) activity,
// android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
// verifDialog.setContentView(R.layout.user_verif_dialog);
// verifDialog.findViewById(R.id.btn_cancel_verif_dialog)
// .setOnClickListener(new OnClickListener() {
// @Override
// public void onClick(View v) {
// // if (isVerifDialogShowing) {
// verifDialog.cancel();
// // isVerifDialogShowing = false;
// // }
// }
// });
// verifDialog.findViewById(R.id.btn_submit_verif_dialog)
// .setOnClickListener(new OnClickListener() {
// @Override
// public void onClick(View v) {
//
// new VerificationTask(activity, code).execute();
// // if (isVerifDialogShowing) {
// // isVerifDialogShowing = false;
// // }
// }
// });
// // Center-focus the dialog
// Window window = verifDialog.getWindow();
// window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
// window.setGravity(Gravity.CENTER);
//
// // The below code is EXTRA - to dim the parent view by 70% :D
// WindowManager.LayoutParams lp = window.getAttributes();
// lp.dimAmount = 0.7f;
// lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
// window.setAttributes(lp);
// verifDialog.show();
// }