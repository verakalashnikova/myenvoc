package com.myenvoc.android.domain;

import android.content.Context;

import com.myenvoc.R;

public enum FailureValue {
	INTERNAL_ERROR_CODE {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.internal_error);
		}
	},

	USER_WITH_THIS_EMAIL_NOT_FOUND {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.FailureValue_USER_WITH_THIS_EMAIL_NOT_FOUND);
		}
	},

	USER_WITH_EMAIL_EXISTS {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.FailureValue_USER_WITH_EMAIL_EXISTS);
		}
	},
	GENERAL_ERROR {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.internal_error);
		}
	},
	NO_INTERNET {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.no_internet);
		}
	},
	WRONG_EMAIL_PWD {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.wrong_email_pwd);
		}
	},
	VALIDATION_ERROR {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.FailureValue_VALIDATION_ERROR);
		}
	},
	ANONYMOUS_USER_ACCESS_TRANSLATION {
		@Override
		public String getLocalized(final Context context) {
			return "";
		}
	},
	NATIVE_LANG_NOT_SET {
		@Override
		public String getLocalized(final Context context) {
			return "";
		}
	},
	CONNECTION_TIMEOUT {
		@Override
		public String getLocalized(final Context context) {
			return context.getString(R.string.connection_timeout);
		}
	};

	public abstract String getLocalized(Context context);

	/**
	 * <string name="FailureValue_INTERNAL_ERROR_CODE"></string> <string
	 * name="FailureValue_REQUEST_TIMEOUT"></string> <string
	 * name="FailureValue_CONNECTION_PROBLEM"></string> <string
	 * name="FailureValue_WRONG_TEXT_TYPE"></string> <string
	 * name="FailureValue_TEXT_NOT_FOUND"></string> <string
	 * name="FailureValue_FOREIGN_TEXT"></string> <string
	 * name="FailureValue_CONFIRMATION_NOT_FOUND"></string> <string
	 * name="FailureValue_CONFIRMATION_WRONG_GUID"></string> <string
	 * name="FailureValue_CONFIRMATION_NEW_PASSWORD_NOT_SET"></string> <string
	 * name="FailureValue_USER_WITH_THIS_EMAIL_NOT_FOUND"></string> <string
	 * name="FailureValue_WORD_NOT_FOUND"></string> <string
	 * name="FailureValue_MY_WORD_EXIST"></string> <string
	 * name="FailureValue_MY_WORD_NOT_FOUND"></string> <string
	 * name="FailureValue_TEXT_EXTRACT_ERROR"></string> <string
	 * name="FailureValue_STUDY_SESSION_EXPIRED"></string> <string
	 * name="FailureValue_USER_WITH_EMAIL_EXISTS"></string>
	 */
}