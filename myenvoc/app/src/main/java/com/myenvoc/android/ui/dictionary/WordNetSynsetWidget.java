package com.myenvoc.android.ui.dictionary;

import java.util.Collection;
import java.util.Map.Entry;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import com.myenvoc.R;
import com.myenvoc.android.domain.MyWordContext;
import com.myenvoc.android.domain.SynsetType;
import com.myenvoc.android.domain.WordNetSynset;
import com.myenvoc.android.domain.WordRef;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.CommonUtils.TextClickableSpan.SpanOnClickListener;
import com.myenvoc.commons.UIUtils;
import com.myenvoc.commons.VocabularyUtils;

public class WordNetSynsetWidget {
	private final static Function<WordRef, String> LEMMA_EXTRACT = new Function<WordRef, String>() {

		@Override
		public String apply(final WordRef arg) {
			return arg.getLemma();
		}
	};

	public interface DefinitionSelectListener {
		void onSelect(MyWordContext context);
	}

	public static View createWordNetDefinitionWidget(final FragmentActivity context, final Collection<WordNetSynset> wordNetSynsets,
			final DefinitionSelectListener definitionSelectListener) {
		ImmutableListMultimap<SynsetType, WordNetSynset> index = Multimaps.index(wordNetSynsets, new Function<WordNetSynset, SynsetType>() {

			@Override
			public SynsetType apply(final WordNetSynset w) {
				return w.getSynsetType();
			}
		});

		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.VERTICAL);

		for (Entry<SynsetType, Collection<WordNetSynset>> entry : index.asMap().entrySet()) {
			SynsetType synsetType = entry.getKey();

			layout.addView(WordNetSynsetWidget.create(synsetType, entry.getValue(), context, definitionSelectListener));
		}
		return layout;
	}

	public static View create(final SynsetType synsetType, final Collection<WordNetSynset> value, final FragmentActivity context,
			final DefinitionSelectListener definitionSelectListener) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout synsets = new LinearLayout(context);
		synsets.setLayoutParams(params);

		synsets.setOrientation(LinearLayout.VERTICAL);

		addSynsetType(synsetType, context, synsets);
		int number = value.size() > 1 ? 1 : -Integer.MAX_VALUE;
		for (WordNetSynset wordNetSynset : value) {
			params.setMargins(15, 0, 5, 10);
			synsets.addView(createSingleSynset(context, wordNetSynset, number++, definitionSelectListener), params);
		}
		return synsets;
	}

	private static void addSynsetType(final SynsetType synsetType, final FragmentActivity context, final ViewGroup synsets) {
		TextView synsetLabel = UIUtils.createTextView(context);
		synsetLabel.setTextColor(context.getResources().getColor(R.color.red));
		synsetLabel.setText(synsetType.getLocalized(context));
		synsets.addView(synsetLabel);
	}

	private static View createSingleSynset(final FragmentActivity context, final WordNetSynset wordNetSynset, final int number,
			final DefinitionSelectListener definitionSelectListener) {

		if (definitionSelectListener == null) {
			return createSingleSynsetAllowFurtherNavigation(context, wordNetSynset, number);
		}
		return createSingleSelectableSynset(context, wordNetSynset, number, definitionSelectListener);
	}

	private static View createSingleSelectableSynset(final FragmentActivity context, final WordNetSynset wordNetSynset, final int number,
			final DefinitionSelectListener definitionSelectListener) {
		Preconditions.checkNotNull(definitionSelectListener);

		final String synsetNumber = getSynsetNumber(number);

		final String synonymsString = getSynonyms(extractSynonyms(wordNetSynset));

		final String example = CommonUtils.isNotEmpty(wordNetSynset.getExample()) ? wordNetSynset.getExample() : "";

		final String definition = getDefinition(wordNetSynset, example);

		SpannableString spannable = new SpannableString(synsetNumber + synonymsString + definition + example);

		CommonUtils.addSpan(context, spannable, 0, synsetNumber, R.color.dictionaryColor);

		CommonUtils.addSpan(context, spannable, synsetNumber.length(), synonymsString, R.color.blue);

		CommonUtils.addSpan(context, spannable, synsetNumber.length() + synonymsString.length(), definition, R.color.dictionaryColor);

		CommonUtils.addSpan(context, spannable, synsetNumber.length() + definition.length() + synonymsString.length(), example,
				R.color.exampleColor);

		TextView synsetView = UIUtils.createTextView(context);
		synsetView.setTextColor(context.getResources().getColor(R.color.dictionaryColor));
		synsetView.setText(spannable);

		synsetView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				definitionSelectListener.onSelect(VocabularyUtils.convert(wordNetSynset));
			}
		});
		synsetView.setBackgroundResource(R.drawable.border);

		return synsetView;
	}

	private static View createSingleSynsetAllowFurtherNavigation(final FragmentActivity context, final WordNetSynset wordNetSynset,
			final int number) {

		SpanOnClickListener spanOnClickListener = new SpanOnClickListener() {
			@Override
			public void onClick(final String word) {
				Intent wordIntent = new Intent(context, MyenvocDictionaryActivity.class);
				wordIntent.putExtra(CommonUtils.WORD, word);
				context.startActivity(wordIntent);
			}
		};

		final Collection<String> synonyms = extractSynonyms(wordNetSynset);

		final String synsetNumber = getSynsetNumber(number);

		final String synonymsString = getSynonyms(synonyms);

		final String example = CommonUtils.isNotEmpty(wordNetSynset.getExample()) ? wordNetSynset.getExample() : "";

		final String definition = getDefinition(wordNetSynset, example);

		String synsetText = synsetNumber + synonymsString + definition + example;
		SpannableString spannable = new SpannableString(synsetText);

		CommonUtils.addSpan(context, spannable, 0, synsetNumber, R.color.dictionaryColor);

		CommonUtils.makeClickableSpannable(synonyms, CommonUtils.JOIN_COMMA, spannable, synsetNumber.length(), context.getResources()
				.getColor(R.color.blue), spanOnClickListener, synsetText);
		CommonUtils.makeClickableSpannable(synsetText, definition, spannable, synsetNumber.length() + synonymsString.length(), context
				.getResources().getColor(R.color.dictionaryColor), spanOnClickListener);
		CommonUtils.makeClickableSpannable(synsetText, example, spannable,
				synsetNumber.length() + synonymsString.length() + definition.length(), context.getResources()
						.getColor(R.color.exampleColor), spanOnClickListener);

		TextView synsetView = UIUtils.createTextView(context);
		synsetView.setTextColor(context.getResources().getColor(R.color.dictionaryColor));
		synsetView.setText(spannable);
		synsetView.setMovementMethod(LinkMovementMethod.getInstance());
		return synsetView;
	}

	private static String getDefinition(final WordNetSynset wordNetSynset, final String example) {
		return CommonUtils.isNotEmpty(wordNetSynset.getDefinition()) ? wordNetSynset.getDefinition() + ("".equals(example) ? "" : "\n")
				: "";
	}

	private static String getSynonyms(final Collection<String> synonyms) {
		String synonymsJoin = Joiner.on(CommonUtils.JOIN_COMMA).join(synonyms);
		return "".equals(synonymsJoin) ? "" : synonymsJoin + " ";

	}

	private static String getSynsetNumber(final int number) {
		if (number > 0) {
			return number + ") ";
		}
		return "";
	}

	// TODO: actually server may send only lemmas. No need in wordIds
	private static Collection<String> extractSynonyms(final WordNetSynset wordNetSynset) {
		return Collections2.transform(wordNetSynset.getSynonyms(), LEMMA_EXTRACT);
	}

}
