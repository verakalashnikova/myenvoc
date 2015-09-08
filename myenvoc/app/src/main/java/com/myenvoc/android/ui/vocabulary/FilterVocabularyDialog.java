package com.myenvoc.android.ui.vocabulary;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.myenvoc.R;
import com.myenvoc.android.dao.dictionary.VocabularyOrder;
import com.myenvoc.android.domain.MyWordStatus;
import com.myenvoc.android.service.vocabulary.VocabularyFilter;
import com.myenvoc.android.service.vocabulary.VocabularyFilter.AddedAt;
import com.myenvoc.commons.CommonUtils;

public class FilterVocabularyDialog extends DialogFragment {

	private static final String FILTER = "filter";

	public interface FilterVocabularyDialogListener {
		public void onFilterSelected(DialogFragment dialog, VocabularyFilter vocabularyFilter);
	}

	private Spinner addedAt;

	private Spinner sortingOrder;

	private TextView startsWith;

	private CheckBox wordStatusNew;

	private CheckBox wordStatusFamiliar;

	private CheckBox wordStatusGood;

	private CheckBox wordStatusKnown;

	private CheckBox wordStatusNever;

	private FilterVocabularyDialogListener listener;

	public static void create(final VocabularyFilter vocabularyFilter, final FragmentManager supportFragmentManager) {
		DialogFragment dialog = new FilterVocabularyDialog();
		if (vocabularyFilter != null) {
			Bundle args = new Bundle();
			args.putParcelable(FILTER, vocabularyFilter);
			dialog.setArguments(args);
		}
		dialog.show(supportFragmentManager, "FilterVocabulary");
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		try {
			listener = (FilterVocabularyDialogListener) activity;
		} catch (ClassCastException e) {
			throw e;
		}
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.filter_vocabulary, null);

		addedAt = (Spinner) dialogView.findViewById(R.id.addedAtPeriod);
		sortingOrder = (Spinner) dialogView.findViewById(R.id.sortingOrder);
		startsWith = (TextView) dialogView.findViewById(R.id.startsWith);
		startsWith.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					onAction();
					FilterVocabularyDialog.this.dismiss();
					return true;
				}
				return false;
			}
		});

		wordStatusNew = (CheckBox) dialogView.findViewById(R.id.wordStatusNew);

		wordStatusFamiliar = (CheckBox) dialogView.findViewById(R.id.wordStatusFamiliar);

		wordStatusGood = (CheckBox) dialogView.findViewById(R.id.wordStatusGood);

		wordStatusKnown = (CheckBox) dialogView.findViewById(R.id.wordStatusKnown);

		wordStatusNever = (CheckBox) dialogView.findViewById(R.id.wordStatusNever);
		if (getArguments() != null && getArguments().containsKey(FILTER)) {
			VocabularyFilter vocabularyFilter = getArguments().getParcelable(FILTER);

			if (CommonUtils.isNotEmpty(vocabularyFilter.getStartsWith())) {
				startsWith.setText(vocabularyFilter.getStartsWith());
			}
			addedAt.setSelection(vocabularyFilter.getAddedAt().ordinal());
			sortingOrder.setSelection(vocabularyFilter.getVocabularyOrder().ordinal());

			EnumSet<MyWordStatus> wordStatuses = vocabularyFilter.getWordStatuses();

			wordStatusNew.setChecked(wordStatuses.contains(MyWordStatus.NEW));
			wordStatusFamiliar.setChecked(wordStatuses.contains(MyWordStatus.FAMILIAR));
			wordStatusGood.setChecked(wordStatuses.contains(MyWordStatus.GOOD));
			wordStatusKnown.setChecked(wordStatuses.contains(MyWordStatus.KNOWN));
			wordStatusNever.setChecked(wordStatuses.contains(MyWordStatus.NEVER));
		} else {
			resetFilter();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.filterVocabulary).setView(dialogView)
				.setPositiveButton(R.string.filter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						onAction();
					}

				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						FilterVocabularyDialog.this.getDialog().cancel();
					}
				}).setNeutralButton(R.string.reset, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						resetFilter();
					}

				});
		return builder.create();
	}

	private void resetFilter() {
		addedAt.setSelection(0);
		sortingOrder.setSelection(0);
		startsWith.setText("");
		wordStatusNew.setChecked(true);
		wordStatusFamiliar.setChecked(true);
		wordStatusGood.setChecked(true);
		wordStatusKnown.setChecked(true);
		wordStatusNever.setChecked(true);

		onAction();
	}

	private void onAction() {

		String startsWithValue = startsWith.getText().toString();
		AddedAt addedAtValue = AddedAt.values()[addedAt.getSelectedItemPosition()];
		VocabularyOrder vocabularyOrder = VocabularyOrder.values()[sortingOrder.getSelectedItemPosition()];

		List<MyWordStatus> statuses = new ArrayList<MyWordStatus>(MyWordStatus.values().length);
		if (wordStatusNew.isChecked()) {
			statuses.add(MyWordStatus.NEW);
		}
		if (wordStatusFamiliar.isChecked()) {
			statuses.add(MyWordStatus.FAMILIAR);
		}
		if (wordStatusGood.isChecked()) {
			statuses.add(MyWordStatus.GOOD);
		}
		if (wordStatusKnown.isChecked()) {
			statuses.add(MyWordStatus.KNOWN);
		}
		if (wordStatusNever.isChecked()) {
			statuses.add(MyWordStatus.NEVER);
		}
		final EnumSet<MyWordStatus> statusesEnum;
		if (statuses.isEmpty()) {
			statusesEnum = EnumSet.noneOf(MyWordStatus.class);
		} else {
			statusesEnum = EnumSet.copyOf(statuses);
		}
		listener.onFilterSelected(this, new VocabularyFilter(startsWithValue, addedAtValue, statusesEnum, vocabularyOrder));
	}
}
