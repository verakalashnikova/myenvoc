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
import android.widget.Spinner;
import android.widget.TextView;

import com.myenvoc.R;
import com.myenvoc.android.domain.MyWordContext;
import com.myenvoc.android.domain.ParcelableEntityAdapter;
import com.myenvoc.android.domain.SynsetType;
import com.myenvoc.commons.CommonUtils;

public class EditMyContextDialog extends DialogFragment {
	private static final String INDEX = "index";
	private static final String MY_WORD_CONTEXT = "myWordContext";

	public interface EditMyContextDialogListener {
		public void onAddOrUpdateMyContext(DialogFragment dialog, MyWordContext myWordContext, int index);
	}

	private EditMyContextDialogListener listener;
	private int index = -1;

	public static void create(final FragmentManager supportFragmentManager) {
		DialogFragment dialog = new EditMyContextDialog();
		dialog.show(supportFragmentManager, "EditMyContext");
	}

	public static void edit(final MyWordContext myWordContext, final int index, final FragmentManager supportFragmentManager) {
		DialogFragment dialog = new EditMyContextDialog();
		Bundle args = new Bundle();
		args.putParcelable(MY_WORD_CONTEXT, new ParcelableEntityAdapter(myWordContext));
		args.putInt(INDEX, index);
		dialog.setArguments(args);
		dialog.show(supportFragmentManager, "EditMyContext");
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		try {
			listener = (EditMyContextDialogListener) activity;
		} catch (ClassCastException e) {
			throw e;
		}
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.edit_my_context, null);

		if (getArguments() != null && getArguments().containsKey(MY_WORD_CONTEXT)) {
			ParcelableEntityAdapter adapter = getArguments().getParcelable(MY_WORD_CONTEXT);
			this.index = getArguments().getInt(INDEX);
			MyWordContext myWordContext = (MyWordContext) adapter.getEntity();

			Spinner synsetType = (Spinner) dialogView.findViewById(R.id.synsetType);
			TextView definition = (TextView) dialogView.findViewById(R.id.definition);
			TextView example = (TextView) dialogView.findViewById(R.id.example);
			TextView synonym = (TextView) dialogView.findViewById(R.id.synonym);

			if (myWordContext.getSynsetType() != null) {
				synsetType.setSelection(myWordContext.getSynsetType().getIndex() + 1);
			}
			if (CommonUtils.isNotEmpty(myWordContext.getDefinition())) {
				definition.setText(myWordContext.getDefinition());
			}
			if (CommonUtils.isNotEmpty(myWordContext.getExample())) {
				example.setText(myWordContext.getExample());
			}
			if (CommonUtils.isNotEmpty(myWordContext.getSynonyms())) {
				synonym.setText(myWordContext.getSynonyms());
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final int titleId;
		if (index < 0) {
			titleId = R.string.addDefinition;
		} else {
			titleId = R.string.editDefinition;
		}
		builder.setTitle(titleId).setView(dialogView).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				onAction();
			}

		}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				EditMyContextDialog.this.getDialog().cancel();
			}
		});
		return builder.create();
	}

	private void onAction() {
		Spinner synsetType = (Spinner) getDialog().findViewById(R.id.synsetType);
		TextView definition = (TextView) getDialog().findViewById(R.id.definition);
		TextView example = (TextView) getDialog().findViewById(R.id.example);
		TextView synonym = (TextView) getDialog().findViewById(R.id.synonym);

		MyWordContext myWordContext = new MyWordContext();

		int itemPosition = synsetType.getSelectedItemPosition();
		boolean hasData = false;
		if (itemPosition > 0) {
			SynsetType synsetTypeValue = SynsetType.values()[itemPosition - 1];
			myWordContext.setSynsetType(synsetTypeValue);
			hasData = true;
		}

		if (!"".equals(definition.getText().toString())) {
			myWordContext.setDefinition(definition.getText().toString());
			hasData = true;
		}

		if (!"".equals(example.getText().toString())) {
			myWordContext.setExample(example.getText().toString());
			hasData = true;
		}

		if (!"".equals(synonym.getText().toString())) {
			myWordContext.setSynonyms(synonym.getText().toString());
			hasData = true;
		}
		if (hasData) {
			listener.onAddOrUpdateMyContext(this, myWordContext, index);
		}
	}
}
