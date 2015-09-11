package com.myenvoc.android.ui.vocabulary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;

import com.myenvoc.R;

public class ConfimWordDeleteDialog {
	public static void showWordDeleteConfirmDialog(final FragmentActivity activity, final DialogInterface.OnClickListener onConfirm) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		alertDialogBuilder.setTitle(R.string.confirmDeleteTitle);

		alertDialogBuilder.setMessage(R.string.confirmWordDelete).setCancelable(true).setPositiveButton(R.string.delete, onConfirm)
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});

		alertDialogBuilder.create().show();

	}
}
