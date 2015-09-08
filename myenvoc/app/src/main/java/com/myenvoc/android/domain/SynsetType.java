package com.myenvoc.android.domain;

import android.content.Context;

import com.myenvoc.R;

public enum SynsetType {
	NOUN(0) {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.SynsetType_NOUN);
		}
	},
	VERB(1) {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.SynsetType_VERB);
		}
	},
	ADJECTIVE(2) {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.SynsetType_ADJECTIVE);
		}
	},
	ADVERB(3) {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.SynsetType_ADVERB);
		}
	},
	ADJECTIVE_SATELLITES(3) {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.SynsetType_ADJECTIVE_SATELLITES);
		}
	};

	private int index;

	private SynsetType(final int index) {
		this.index = index;
	}

	public abstract String getLocalized(Context context);

	public int getIndex() {
		return index;
	}

	/**
	 * <string name="SynsetType_NOUN"></string> <string
	 * name="SynsetType_VERB"></string> <string
	 * name="SynsetType_ADJECTIVE"></string> <string
	 * name="SynsetType_ADVERB"></string> <string
	 * name="SynsetType_ADJECTIVE_SATELLITES"></string>
	 */
}