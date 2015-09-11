package com.myenvoc.android.ui.vocabulary;

import java.util.Collection;

import javax.inject.Inject;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.myenvoc.R;
import com.myenvoc.android.domain.MyWord;
import com.myenvoc.android.service.dictionary.ImageService;
import com.myenvoc.android.service.vocabulary.Vocabulary;
import com.myenvoc.commons.StringUtils;

public class VocabularyViewStrategyFactory {

	@Inject
	ImageService imageService;

	public interface VocabularyViewStrategy {

		Vocabulary massageVocabulary(Vocabulary vocabulary);

		int getViewId();

		void renderVocabularyWord(Vocabulary vocabulary, View myWordView, ScrollView contentView, FragmentActivity context);

	}

	private final VocabularyViewStrategy defaultStrategy = new VocabularyViewStrategy() {

		@Override
		public Vocabulary massageVocabulary(final Vocabulary vocabulary) {
			return vocabulary;
		}

		@Override
		public int getViewId() {
			return R.layout.view_my_word;
		}

		@Override
		public void renderVocabularyWord(final Vocabulary vocabulary, final View myWordView, final ScrollView contentView,
				final FragmentActivity context) {
			TextView wordTitle = (TextView) myWordView.findViewById(R.id.wordTitle);
			View pronunciationInProgress = myWordView.findViewById(R.id.pronunciationInProgress);
			handleWordLemmaVisibility(myWordView, true);
			ViewMyWordWidget.renderMyWordWidgetFull(context, wordTitle, contentView, pronunciationInProgress, vocabulary, imageService);
		}

	};

	private final VocabularyViewStrategy englishStrategy = new VocabularyViewStrategy() {

		@Override
		public Vocabulary massageVocabulary(final Vocabulary vocabulary) {
			return vocabulary;
		}

		@Override
		public int getViewId() {
			return R.layout.view_my_word;
		}

		@Override
		public void renderVocabularyWord(final Vocabulary vocabulary, final View myWordView, final ScrollView contentView,
				final FragmentActivity context) {
			handleWordLemmaVisibility(myWordView, false);

			ViewMyWordWidget.renderMyWordWidgetLemma(context, contentView, vocabulary);
		}

	};

	private final VocabularyViewStrategy translationStrategy = new VocabularyViewStrategy() {
		@Override
		public int getViewId() {
			return R.layout.view_my_word;
		}

		@Override
		public Vocabulary massageVocabulary(final Vocabulary vocabulary) {
			Collection<MyWord> filtered = Collections2.filter(vocabulary.getMyWords(), new Predicate<MyWord>() {

				@Override
				public boolean apply(final MyWord word) {
					return StringUtils.isNotEmpty(word.getTranslation());
				}
			});
			if (filtered.size() != vocabulary.getTotalGivenFilter()) {
				return new Vocabulary(Lists.newArrayList(filtered), vocabulary.getTotalWords());
			}
			return vocabulary;
		};

		@Override
		public void renderVocabularyWord(final Vocabulary vocabulary, final View myWordView, final ScrollView contentView,
				final FragmentActivity context) {
			handleWordLemmaVisibility(myWordView, false);
			ViewMyWordWidget.renderMyWordWidgetTranslation(context, contentView, vocabulary, imageService);
		}
	};

	private final VocabularyViewStrategy translationDefinitionStrategy = new VocabularyViewStrategy() {
		@Override
		public int getViewId() {
			return R.layout.view_my_word;
		}

		@Override
		public Vocabulary massageVocabulary(final Vocabulary vocabulary) {
			Collection<MyWord> filtered = Collections2.filter(vocabulary.getMyWords(), new Predicate<MyWord>() {

				@Override
				public boolean apply(final MyWord word) {
					return StringUtils.isNotEmpty(word.getTranslation()) || !word.getContexts().isEmpty();
				}
			});
			if (filtered.size() != vocabulary.getTotalGivenFilter()) {
				return new Vocabulary(Lists.newArrayList(filtered), vocabulary.getTotalWords());
			}
			return vocabulary;
		};

		@Override
		public void renderVocabularyWord(final Vocabulary vocabulary, final View myWordView, final ScrollView contentView,
				final FragmentActivity context) {
			handleWordLemmaVisibility(myWordView, false);
			ViewMyWordWidget.renderMyWordWidgetTranslationAndDefinition(context, contentView, vocabulary, imageService);
		}
	};

	private final VocabularyViewStrategy pronunciationStrategy = new VocabularyViewStrategy() {
		@Override
		public int getViewId() {
			return R.layout.view_my_word;
		}

		@Override
		public Vocabulary massageVocabulary(final Vocabulary vocabulary) {
			return vocabulary;
		};

		@Override
		public void renderVocabularyWord(final Vocabulary vocabulary, final View myWordView, final ScrollView contentView,
				final FragmentActivity context) {
			handleWordLemmaVisibility(myWordView, false);
			ViewMyWordWidget.renderMyWordWidgetPronunciation(context, contentView, vocabulary, imageService);
		}
	};

	private VocabularyView vocabularyView;

	public VocabularyView getVocabularyView() {
		return vocabularyView == null ? VocabularyView.DEFAULT : vocabularyView;
	}

	public void setVocabularyView(final VocabularyView vocabularyView) {
		this.vocabularyView = vocabularyView;
	}

	public VocabularyViewStrategy getCurrentVocabularyViewStrategy() {
		return getVocabularyViewStrategy(getVocabularyView());
	}

	public VocabularyViewStrategy getVocabularyViewStrategy(final VocabularyView vocabularyView) {
		switch (vocabularyView) {
		case DEFAULT:
			return defaultStrategy;
		case LEMMA:
			return englishStrategy;
		case TRANSLATION:
			return translationStrategy;
		case TRANSLATION_DEFINITION:
			return translationDefinitionStrategy;
		case PRONUNCIATION:
			return pronunciationStrategy;
		default:
			return defaultStrategy;
		}
	}

	private static void handleWordLemmaVisibility(final View myWordView, final boolean show) {
		int lemmaVisibility = show ? View.VISIBLE : View.INVISIBLE;
		int openButtonVisibility = show ? View.GONE : View.VISIBLE;

		View wrapper = myWordView.findViewById(R.id.wordTitleWrapper);
		if (wrapper.getVisibility() != lemmaVisibility) {
			wrapper.setVisibility(lemmaVisibility);
		}

		Button openWordCard = (Button) myWordView.findViewById(R.id.openWordCard);
		if (openWordCard.getVisibility() != openButtonVisibility) {
			openWordCard.setVisibility(openButtonVisibility);
		}
	}

}
