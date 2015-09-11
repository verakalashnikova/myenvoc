package com.myenvoc.android.ui.vocabulary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.myenvoc.R;
import com.myenvoc.commons.CommonUtils;

public class EditTranslationDialog extends DialogFragment {
	private static final String INDEX = "index";
	private static final String TRANSLATION = "translation";

	public interface EditTranslationDialogListener {
		public void onAddOrUpdateTranslation(DialogFragment dialog, String translation, int index);
	}

	private EditTranslationDialogListener listener;
	private int index = -1;

	public static void create(final FragmentManager supportFragmentManager) {
		DialogFragment dialog = new EditTranslationDialog();
		dialog.show(supportFragmentManager, "EditMyContext");
	}

	public static void edit(final String translation, final int index, final FragmentManager supportFragmentManager) {
		DialogFragment dialog = new EditTranslationDialog();
		Bundle args = new Bundle();
		args.putString(TRANSLATION, translation);
		args.putInt(INDEX, index);
		dialog.setArguments(args);
		dialog.show(supportFragmentManager, "EditTranslation");
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		try {
			listener = (EditTranslationDialogListener) activity;
		} catch (ClassCastException e) {
			throw e;
		}
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.edit_translation, null);

		if (getArguments() != null && getArguments().containsKey(TRANSLATION)) {
			String translationValue = getArguments().getString(TRANSLATION);
			this.index = getArguments().getInt(INDEX);
			TextView translation = (TextView) dialogView.findViewById(R.id.translation);

			if (CommonUtils.isNotEmpty(translationValue)) {
				translation.setText(translationValue);
			}

		}
		final int titleId;
		if (index < 0) {
			titleId = R.string.addTranslation;
		} else {
			titleId = R.string.editTranslation;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(titleId).setView(dialogView).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				onAction();
			}

		}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				EditTranslationDialog.this.getDialog().cancel();
			}
		});
		return builder.create();
	}

	private void onAction() {
		TextView translation = (TextView) getDialog().findViewById(R.id.translation);

		listener.onAddOrUpdateTranslation(this, translation.getText().toString(), index);
	}
}
