package com.myenvoc.android.ui.vocabulary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;

import com.myenvoc.R;
import com.myenvoc.android.domain.MyWordStatus;

public class ChangeWordStatusDialog {
	interface MyWordStatusListener {
		void onStatusSelection(MyWordStatus myWordStatus);
	}

	public static void showDontShowAgainDialog(final FragmentActivity activity, final MyWordStatusListener positiveListene,
			final MyWordStatus status) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		alertDialogBuilder.setTitle(R.string.changeStatus);

		Context context = activity.getBaseContext();

		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
		for (MyWordStatus myWordStatus : MyWordStatus.values()) {

			arrayAdapter.add(myWordStatus.getLocalized(context));
		}

		final int selection;
		if (status != null) {
			selection = status.ordinal();
		} else {
			selection = 0;
		}

		alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);
		alertDialogBuilder.setSingleChoiceItems(arrayAdapter, selection, new OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int index) {
				MyWordStatus[] values = MyWordStatus.values();

				if (index < values.length) {
					final MyWordStatus myWordStatus = values[index];

					if (dialog != null) {
						dialog.dismiss();
					}

					positiveListene.onStatusSelection(myWordStatus);

				}
			}
		});
		alertDialogBuilder.create().show();

	}

}
