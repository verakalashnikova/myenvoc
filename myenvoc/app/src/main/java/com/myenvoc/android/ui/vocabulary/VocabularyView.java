package com.myenvoc.android.ui.vocabulary;

import com.myenvoc.R;

import android.content.Context;

public enum VocabularyView {

	DEFAULT {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.VocabularyView_DEFAULT);
		}
	}, LEMMA {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.VocabularyView_LEMMA);
		}
	}, TRANSLATION {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.VocabularyView_TRANSLATION);
		}
	}, TRANSLATION_DEFINITION {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.VocabularyView_TRANSLATION_DEFINITION);
		}
	}, PRONUNCIATION {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.VocabularyView_PRONUNCIATION);
		}
	};

	public abstract String getLocalized(Context context);


}
