package com.myenvoc.android.domain;

import android.content.Context;

import com.myenvoc.R;

public enum MyWordStatus {
	NEW {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.MyWordStatus_NEW);
		}
	},
	FAMILIAR {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.MyWordStatus_FAMILIAR);
		}
	},
	GOOD {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.MyWordStatus_GOOD);
		}
	},
	KNOWN {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.MyWordStatus_KNOWN);
		}
	},
	NEVER {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.MyWordStatus_NEVER);
		}
	};

	public abstract String getLocalized(Context context);

	/**
	 * <string name="MyWordStatus_NEW"></string> <string
	 * name="MyWordStatus_FAMILIAR"></string> <string
	 * name="MyWordStatus_GOOD"></string> <string
	 * name="MyWordStatus_KNOWN"></string>
	 */
}