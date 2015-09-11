package com.myenvoc.android.ui.vocabulary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;

import com.myenvoc.R;

public class VocabularyViewDialog {
	interface VocabularyViewListener {
		void onViewSelection(VocabularyView vocabularyView);
	}

	public static void create(final FragmentActivity activity, final VocabularyView vocabularyView, final VocabularyViewListener listener) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		alertDialogBuilder.setTitle(R.string.changeVocabularyView);

		Context context = activity.getBaseContext();

		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
		for (VocabularyView view : VocabularyView.values()) {
			arrayAdapter.add(view.getLocalized(context));
		}

		final int selection;
		if (vocabularyView != null) {
			selection = vocabularyView.ordinal();
		} else {
			selection = 0;
		}

		alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);
		alertDialogBuilder.setSingleChoiceItems(arrayAdapter, selection, new OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int index) {
				final VocabularyView view = VocabularyView.values()[index];
				dialog.dismiss();

				listener.onViewSelection(view);
			}
		});
		alertDialogBuilder.create().show();

	}

}
