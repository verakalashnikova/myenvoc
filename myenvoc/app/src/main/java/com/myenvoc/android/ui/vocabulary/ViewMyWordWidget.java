package com.myenvoc.android.ui.vocabulary;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.myenvoc.R;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.domain.MyWordContext;
import com.myenvoc.android.service.dictionary.ImageService;
import com.myenvoc.android.service.vocabulary.Vocabulary;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.CustomFontsLoader;
import com.myenvoc.commons.StringUtils;
import com.myenvoc.commons.UIUtils;
import com.myenvoc.commons.VocabularyUtils;

public class ViewMyWordWidget {

	private static String MASK = "***********";

	public static void renderMyWordWidgetFull(final FragmentActivity context, final TextView wordTitle, final ScrollView contentView,
			final View pronunciationInProgress, final Vocabulary vocabulary, final ImageService imageService) {

		MyWord myWord = vocabulary.getCurrent();
		setLemma(myWord, context, wordTitle, pronunciationInProgress);

		LinearLayout layout = verticalLinearLayout(context);

		setImage(context, imageService, myWord, layout);

		int number = 1;
		number = setTranslation(myWord, layout, context, number);

		setDefinition(myWord, layout, context, number, false);

		contentView.removeAllViews();
		contentView.addView(layout);
	}

	public static void renderMyWordWidgetLemma(final FragmentActivity context, final ScrollView contentView, final Vocabulary vocabulary) {
		MyWord myWord = vocabulary.getCurrent();

		View view = View.inflate(context, R.layout.lemma_center_big, null);

		TextView wordTitle = (TextView) view.findViewById(R.id.wordTitle);
		ViewMyWordWidget.initializeLemmaTextView(context, wordTitle);
		setLemma(myWord, context, wordTitle, view.findViewById(R.id.pronunciationInProgress));
		contentView.removeAllViews();
		contentView.addView(view);
	}

	public static void renderMyWordWidgetPronunciation(final FragmentActivity context, final ScrollView contentView,
			final Vocabulary vocabulary, final ImageService imageService) {

		MyWord myWord = vocabulary.getCurrent();

		View view = View.inflate(context, R.layout.lemma_center_big, null);

		TextView wordTitle = (TextView) view.findViewById(R.id.wordTitle);

		View pronunciationInProgress = view.findViewById(R.id.pronunciationInProgress);

		setPronunciationCallback(myWord, context, wordTitle, pronunciationInProgress);

		VocabularyUtils.playTranscription(myWord.getLemma(), context, pronunciationInProgress);

		contentView.removeAllViews();
		contentView.addView(view);
	}

	public static void renderMyWordWidgetTranslation(final Context context, final ScrollView contentView, final Vocabulary vocabulary,
			final ImageService imageService) {
		MyWord myWord = vocabulary.getCurrent();

		LinearLayout layout = verticalLinearLayout(context);

		setImage(context, imageService, myWord, layout);

		int number = 1;
		number = setTranslation(myWord, layout, context, number);

		// openCardListener, layout);

		contentView.removeAllViews();
		contentView.addView(layout);

	}

	public static void renderMyWordWidgetTranslationAndDefinition(final Context context, final ScrollView contentView,
			final Vocabulary vocabulary, final ImageService imageService) {
		MyWord myWord = vocabulary.getCurrent();

		LinearLayout layout = verticalLinearLayout(context);

		setImage(context, imageService, myWord, layout);

		int number = 1;
		number = setTranslation(myWord, layout, context, number);

		setDefinition(myWord, layout, context, number, true);

		// RelativeLayout relativeLayout = attachOpenCardButton(context,
		// openCardListener, layout);

		contentView.removeAllViews();
		contentView.addView(layout);

	}

	// private static RelativeLayout attachOpenCardButton(final Context context,
	// final View content, final OnClickListener openCardListener) {
	// RelativeLayout relativeLayout = new RelativeLayout(context);
	// relativeLayout.setLayoutParams(new
	// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
	// RelativeLayout.LayoutParams.WRAP_CONTENT));
	// relativeLayout.addView(content);
	//
	// RelativeLayout.LayoutParams openCardButtonLayoutParams = new
	// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
	// RelativeLayout.LayoutParams.WRAP_CONTENT);
	// openCardButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	// openCardButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	//
	// Button openCardButton = new Button(context);
	//
	// relativeLayout.addView(openCardButton, openCardButtonLayoutParams);
	//
	// openCardButton.setOnClickListener(openCardListener);
	// return relativeLayout;
	// }

	private static void setDefinition(final MyWord myWord, final LinearLayout layout, final Context context, int number,
			final boolean needMask) {
		List<MyWordContext> contexts = myWord.getContexts();
		if (CommonUtils.isEmpty(contexts)) {
			return;
		}

		LinearLayout definitionLayout = new LinearLayout(context);
		definitionLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		definitionLayout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(definitionLayout);

		final String mask = needMask ? getMask(myWord.getLemma()) : null;

		for (MyWordContext myWordContext : contexts) {
			TextView textView = renderMyWordContext(context, myWordContext, number++, needMask, myWord.getLemma(), mask);
			definitionLayout.addView(textView);
		}

	}

	public static TextView renderMyWordContext(final Context context, final MyWordContext myWordContext, final int finalIndex,
			final boolean needMask, final String lemma, final String mask) {
		TextView textView = UIUtils.createTextView(context);
		textView.setTextColor(context.getResources().getColor(R.color.dictionaryColor));
		if (finalIndex > 0) {
			textView.append(String.valueOf(finalIndex) + ". ");
		}

		if (myWordContext.getSynsetType() != null) {
			textView.append(CommonUtils.spanOfStyle(myWordContext.getSynsetType().getLocalized(context) + " - ", context,
					R.style.synsetType));
		}
		if (StringUtils.isNotEmpty(myWordContext.getSynonyms())) {
			textView.append(CommonUtils.textOfColor(maskIfNeeded(myWordContext.getSynonyms(), needMask, lemma, mask) + " ", context
					.getResources().getColor(R.color.blue)));
		}
		if (StringUtils.isNotEmpty(myWordContext.getDefinition())) {
			textView.append(maskIfNeeded(myWordContext.getDefinition(), needMask, lemma, mask)
					+ (StringUtils.isEmpty(myWordContext.getExample()) ? "" : "\n"));
		}
		if (StringUtils.isNotEmpty(myWordContext.getExample())) {
			textView.append(CommonUtils.textOfColor(maskIfNeeded(myWordContext.getExample(), needMask, lemma, mask) + " ", context
					.getResources().getColor(R.color.exampleColor)));
		}
		return textView;
	}

	private static String maskIfNeeded(final String string, final boolean needMask, final String lemma, final String mask) {
		if (!needMask) {
			return string;
		}

		return string.replaceAll(lemma, mask);
	}

	private static String getMask(final String lemma) {
		while (true) {
			if (lemma.length() > MASK.length()) {
				MASK += MASK;
				continue;
			}
			return MASK.substring(0, lemma.length());
		}
	}

	private static int setTranslation(final MyWord myWord, final LinearLayout layout, final Context context, int number) {
		String translation = myWord.getTranslation();
		if (StringUtils.isEmpty(translation)) {
			return number;
		}

		LinearLayout translationLayout = new LinearLayout(context);
		translationLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		translationLayout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(translationLayout);

		for (String part : VocabularyUtils.getTranslations(myWord)) {
			TextView textView = UIUtils.createTextView(context);
			textView.setTextColor(context.getResources().getColor(R.color.dictionaryColor));
			textView.setText(String.valueOf(number++) + ". " + part);
			translationLayout.addView(textView);

		}
		return number;
	}

	private static void setImage(final Context context, final ImageService imageService, final MyWord myWord, final LinearLayout layout) {
		if (CommonUtils.isNotEmpty(myWord.getImageUrl())) {
			ImageView image = new ImageView(context);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtils.convertToPixels(context, CommonUtils.IMAGE_WIDTH),
					UIUtils.convertToPixels(context, CommonUtils.IMAGE_HEIGHT));

			params.gravity = Gravity.CENTER_HORIZONTAL;
			params.setMargins(0, 0, 0, 5);
			image.setLayoutParams(params);
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);

			imageService.loadImage(myWord.getImageUrl(), image);

			// layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
			// LayoutParams.FILL_PARENT));
			android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			layout.addView(image);
		}
	}

	private static void setLemma(final MyWord myWord, final FragmentActivity context, final TextView wordTitle,
			final View pronunciationInProgress) {
		wordTitle.setText(myWord.getLemma());

		if (StringUtils.isNotEmpty(myWord.getTranscription())) {
			VocabularyUtils.appendTranscription(myWord.getTranscription(), wordTitle, context);
		}
		setPronunciationCallback(myWord, context, wordTitle, pronunciationInProgress);
	}

	private static void setPronunciationCallback(final MyWord myWord, final FragmentActivity context, final TextView wordTitle,
			final View pronunciationInProgress) {
		wordTitle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				VocabularyUtils.playTranscription(myWord.getLemma(), context, pronunciationInProgress);
			}
		});
	}

	private static LinearLayout verticalLinearLayout(final Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		return layout;
	}

	public static void initializeLemmaTextView(final Context context, final TextView wordTitle) {
		try {
			wordTitle.setTypeface(CustomFontsLoader.getTypeface(context, CustomFontsLoader.GENTIUM), Typeface.BOLD);
		} catch (Exception e) {
		}
	}

}