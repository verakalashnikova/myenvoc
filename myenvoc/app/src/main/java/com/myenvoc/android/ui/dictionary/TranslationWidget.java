package com.myenvoc.android.ui.dictionary;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.myenvoc.R;
import com.myenvoc.android.domain.TranEntry;
import com.myenvoc.android.domain.TranOfThisPart;
import com.myenvoc.android.domain.TranslationMeaning;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.StringUtils;
import com.myenvoc.commons.UIUtils;

public class TranslationWidget {
	public interface TranslationSelectListener {
		void onSelect(String translation);
	}

	private final static ImmutableMap<Integer, String> ROMES = ImmutableMap.of(1, "I", 2, "II", 3, "III", 4, "IV", 5, "V");

	public static View create(final String word, final Collection<TranslationMeaning> value, final Context context,
			final TranslationSelectListener translationSelectListener) {

		if (value.size() > 1) {
			LinearLayout result = new LinearLayout(context);

			result.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			result.setOrientation(LinearLayout.VERTICAL);
			int number = 0;
			for (TranslationMeaning meaning : value) {
				result.addView(createTranslationMeaningWrapper(meaning.getEntry(), context, meaning.getDescr(), ++number,
						translationSelectListener));
			}
			return result;
		} else {
			/** Exactly 1. */
			TranslationMeaning meaning = value.iterator().next();
			if (meaning.getDescr() != null) {
				return createTranslationMeaningWrapper(meaning.getEntry(), context, meaning.getDescr(), -1, translationSelectListener);
			}
			return createTranslationEntries(meaning.getEntry(), context, translationSelectListener);
		}
	}

	private static View createTranslationMeaningWrapper(final Collection<TranEntry> entries, final Context context,
			final String description, final int number, final TranslationSelectListener translationSelectListener) {
		RelativeLayout layout = new RelativeLayout(context);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		layout.setLayoutParams(rlp);

		TextView translationMeaningSummary = UIUtils.createTextView(context);
		int id = CommonUtils.IDS_GENERATOR.getAndIncrement();
		boolean valueSet = false;
		if (number > 0) {
			String text = ROMES.get(number);
			String meaning = text == null ? " " : text + " ";
			translationMeaningSummary.append(CommonUtils.spanOfStyle(meaning, context, R.style.boldInDictionary));
			valueSet = true;
		}
		if (StringUtils.isNotEmpty(description)) {
			translationMeaningSummary.append(description);
			valueSet = true;
		}

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		if (valueSet) {
			translationMeaningSummary.setId(id);
			layout.addView(translationMeaningSummary);
			layoutParams.addRule(RelativeLayout.BELOW, id);
		}

		View translationEntries = createTranslationEntries(entries, context, translationSelectListener);
		translationEntries.setPadding(5, 0, 0, 0);

		layout.addView(translationEntries, layoutParams);
		return layout;
	}

	private static View createTranslationEntries(final Collection<TranEntry> entry, final Context context,
			final TranslationSelectListener translationSelectListener) {
		if (entry.size() == 1) {
			return createTranslationEntry(entry.iterator().next(), context, -1, translationSelectListener);
		}

		LinearLayout result = new LinearLayout(context);

		result.setOrientation(LinearLayout.VERTICAL);

		result.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		int number = 0;
		for (TranEntry tranEntry : entry) {
			result.addView(createTranslationEntry(tranEntry, context, ++number, translationSelectListener));
		}
		return result;
	}

	private static View createTranslationEntry(final TranEntry entry, final Context context, final int numberWithinTranslationMeaning,
			final TranslationSelectListener translationSelectListener) {
		if (numberWithinTranslationMeaning <= 0 && CommonUtils.isEmpty(entry.getPart())) {
			return createTranslationOfThisPart(entry.getData(), context, translationSelectListener);
		}

		RelativeLayout layout = new RelativeLayout(context);

		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		layout.setLayoutParams(rlp);

		TextView numberAndPartOfSpeech = UIUtils.createTextView(context);
		int id = CommonUtils.IDS_GENERATOR.getAndIncrement();
		numberAndPartOfSpeech.setId(id);

		if (numberWithinTranslationMeaning > 0) {
			numberAndPartOfSpeech.append(CommonUtils.spanOfStyle(numberWithinTranslationMeaning + ". ", context, R.style.boldInDictionary));
		}

		if (entry.getPart() == null) {
			numberAndPartOfSpeech.append("");
		} else {
			numberAndPartOfSpeech.append(CommonUtils.textOfColor(entry.getPart(), context.getResources().getColor(R.color.red)));

		}

		layout.addView(numberAndPartOfSpeech);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		// below is error don't set below always
		layoutParams.addRule(RelativeLayout.BELOW, id);

		View translationOfPart = createTranslationOfThisPart(entry.getData(), context, translationSelectListener);
		translationOfPart.setPadding(15, 0, 0, 0);
		layoutParams.setMargins(0, 0, 0, 15);
		layout.addView(translationOfPart, layoutParams);
		return layout;
	}

	private static View createTranslationOfThisPart(final Collection<TranOfThisPart> data, final Context context,
			final TranslationSelectListener translationSelectListener) {
		if (data.size() == 1) {
			return createTranslationOfThisPart(data.iterator().next(), context, -1, translationSelectListener);
		}
		LinearLayout result = new LinearLayout(context);
		result.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		if (translationSelectListener != null) {
			params.setMargins(0, 0, 0, 10);
		}
		result.setLayoutParams(params);
		int number = 0;
		for (TranOfThisPart tranOfThisPart : data) {
			if (!tranOfThisPart.getT().isEmpty() || !tranOfThisPart.getE().isEmpty()) {
				result.addView(createTranslationOfThisPart(tranOfThisPart, context, ++number, translationSelectListener), params);
			}
		}
		return result;
	}

	private static View createTranslationOfThisPart(final TranOfThisPart tranOfThisPart, final Context context, final int number,
			final TranslationSelectListener translationSelectListener) {

		RelativeLayout result = new RelativeLayout(context);

		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		result.setLayoutParams(rlp);

		int rightOfId = -1;
		int belowOfId = -1;
		if (number > 0) {
			TextView tranNumber = UIUtils.createTextView(context);
			tranNumber.setText(number + ") ");
			tranNumber.setTextColor(context.getResources().getColor(R.color.dictionaryColor));
			rightOfId = CommonUtils.IDS_GENERATOR.getAndIncrement() + 1000;
			tranNumber.setId(rightOfId);
			result.addView(tranNumber);
		}

		if (!tranOfThisPart.getT().isEmpty()) {

			TextView view = createTranOfThisPart(tranOfThisPart.getT(), context);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			if (rightOfId > 0) {
				layoutParams.addRule(RelativeLayout.RIGHT_OF, rightOfId);
			}

			belowOfId = CommonUtils.IDS_GENERATOR.getAndIncrement();
			view.setId(belowOfId);

			result.addView(view, layoutParams);
		}
		if (!tranOfThisPart.getE().isEmpty()) {

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			if (rightOfId > 0) {
				layoutParams.addRule(RelativeLayout.RIGHT_OF, rightOfId);
			}

			if (belowOfId > 0) {
				layoutParams.addRule(RelativeLayout.BELOW, belowOfId);
			}

			TextView view = createExampleOfThisPart(tranOfThisPart.getE(), context);
			result.addView(view, layoutParams);
		}

		if (translationSelectListener != null) {
			result.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View arg0) {
					String translation = Joiner.on(", ").join(
							Iterators.concat(tranOfThisPart.getT().iterator(), tranOfThisPart.getE().iterator()));
					translationSelectListener.onSelect(translation);
				}
			});
			result.setBackgroundResource(R.drawable.border);
		}
		return result;
	}

	private static TextView createTranOfThisPart(final Collection<String> t, final Context context) {
		TextView translations = UIUtils.createTextView(context);
		translations.setText(glue(t));
		translations.setTextColor(context.getResources().getColor(R.color.dictionaryColor));
		return translations;
	}

	private static TextView createExampleOfThisPart(final Collection<String> t, final Context context) {
		TextView translations = UIUtils.createTextView(context);
		translations.setText(glue(t));
		translations.setTextColor(context.getResources().getColor(R.color.exampleColor));
		return translations;
	}

	private static String glue(final Collection<String> vals) {
		return Joiner.on(", ").join(vals);
	}

}
