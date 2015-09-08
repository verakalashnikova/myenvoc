package com.myenvoc.commons;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import com.google.common.collect.Sets;
import com.myenvoc.android.domain.Status;
import com.myenvoc.commons.CommonUtils.TextClickableSpan.SpanOnClickListener;

/***
 * http://www.hrupin.com/2011/09/how-to-make-custom-indeterminate-progressbar-in
 * -android-or-how-to-change-progressbar-style-or-color
 * 
 * 
 * progressBar = new ProgressDialog(v.getContext()); progressBar.show();
 * progressBar.dismiss();
 * 
 * Character[] a = new
 * Character[]{'a','b','c','d','e','f','g','h','i','j','k','l'
 * ,'m','n','o','p','q','r','s','t','u','v','w','x','y','z',
 * 'A','B','C','D','E',
 * 'F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T'
 * ,'U','V','W','X','Y','Z'};
 */
public class CommonUtils {

	private static final String UTF_8 = "utf-8";
	public static final int IMAGE_WIDTH = 140;
	public static final int IMAGE_HEIGHT = 120;
	public static final String EMAIL_REGEXP = "\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b";
	public static AtomicInteger IDS_GENERATOR = new AtomicInteger(1);
	public static final String MY_WORD = "myWord";
	public static final String MY_WORD_LEMMA = "myWordLemma";
	public static final String WORD = "word";
	public static final String TRANSCRIPTION = "transcription";
	public static final String MY_WORD_ID = "myWordId";
	public static final String IS_BLANK = "blank";
	public static final String IS_NEW = "isNew";
	public static Set<String> STOP_WORDS = Sets.newHashSet("a", "the", "and"); // TODO:
																				// add
																				// more
																				// and
																				// put
																				// to
																				// DB?
	public static String JOIN_COMMA = ", ";

	public static String encodeQueryString(final String name) {
		try {
			return URLEncoder.encode(name, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to encode string", e);
		}
	}

	public static String removeTrailingSlash(final String url) {
		if (url.length() == 0) {
			return "";
		}
		int urlLenght = url.length();
		if (url.charAt(urlLenght - 1) == '/') {
			return url.substring(0, urlLenght - 1);
		}

		return url;
	}

	public static String getLastSegment(final Uri uri) {
		List<String> segments = uri.getPathSegments();
		if (segments.size() > 1) {
			return segments.get(segments.size() - 1);
		}
		return null;
	}

	public static Spannable textOfColor(final String text, final int color) {
		Spannable span = new SpannableString(text);
		span.setSpan(new ForegroundColorSpan(color), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return span;
	}

	public static CharSequence spanOfStyle(final String text, final Context context, final int style) {
		Spannable span = new SpannableString(text);
		span.setSpan(new TextAppearanceSpan(context, style), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return span;
	}

	public static CharSequence spanOfStyle(final String text, final Context context, final int style, final String family) {
		Spannable span = new SpannableString(text);
		span.setSpan(new TextAppearanceSpan(context, style), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return span;
	}

	public static void addSpan(final Context context, final SpannableString spannable, final int offset, final String part, final int color) {
		if (!"".equals(part)) {
			spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(color)), offset, offset + part.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public static boolean isEmpty(final String s) {
		return s == null || s.trim().equals("");
	}

	public static boolean isNotEmpty(final String s) {
		return !isEmpty(s);
	}

	public static boolean isNotEmpty(final Collection<?> collection) {
		return !isEmpty(collection);
	}

	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static String formatDate(final Date added, final Context context) {

		if (added == null) {
			return "";
		}

		java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);

		return dateFormat.format(added);
	}

	public static Date truncateDate(final Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date subtractAndTruncateDate(final Date date, final int numbderOfDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_MONTH, -numbderOfDays);
		return cal.getTime();
	}

	public static boolean isSuccess(final Status status) {
		return status != null && "success".equals(status.getCode());
	}

	public static class TextClickableSpan extends ClickableSpan {
		public interface SpanOnClickListener {
			void onClick(String text);
		}

		private final int color;
		private final int from;
		private final int to;
		private final String target;
		private final SpanOnClickListener listener;

		public TextClickableSpan(final int from, final int to, final String target, final SpanOnClickListener listener, final int color) {
			super();
			this.from = from;
			this.to = to;
			this.target = target;
			this.listener = listener;
			this.color = color;
		}

		@Override
		public void onClick(final View textView) {
			listener.onClick(target.substring(from, to));
		}

		@Override
		public void updateDrawState(final TextPaint ds) {
			ds.setColor(color);
		}
	}

	public static void makeClickableSpannable(final Collection<String> collection, final String joinString,
			final SpannableString spannable, final int offset, final int color, final SpanOnClickListener spanOnClickListener,
			final String synsetText) {
		int index = 0;
		int end = 0;
		int joinLength = joinString.length();

		for (String string : collection) {
			int shift = index == 0 ? 0 : joinLength;
			int start = end + shift;
			end = start + string.length();
			int endIndex = end < synsetText.length() ? end + 1 : end;

			spannable.setSpan(new TextClickableSpan(0, string.length(), string, spanOnClickListener, color), offset + start, offset
					+ endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			index++;
		}
	}

	public static void makeClickableSpannable(final String targetSpannable, final String string, final SpannableString spannable,
			final int offset, final int color, final SpanOnClickListener spanOnClickListener) {
		int index = 0;
		int startIndex = -1;
		int endIndex = 0;
		for (int i = 0; i < string.length(); i++) {
			if (Character.isLetter(string.charAt(i))) {
				if (startIndex == -1) {
					startIndex = index;
					endIndex = index;
				}
				endIndex++;
			} else {
				if (startIndex != -1) {
					appendClickableSpan(targetSpannable, string.substring(startIndex, endIndex), offset + startIndex, offset + endIndex,
							spannable, color, spanOnClickListener);
					startIndex = -1;
				}
			}
			index++;
		}

		if (startIndex != -1) {
			appendClickableSpan(targetSpannable, string.substring(startIndex, endIndex), offset + startIndex, offset + endIndex, spannable,
					color, spanOnClickListener);
		}
	}

	private static void appendClickableSpan(final String targetSpannable, final String span, final int startIndex, final int endIndex,
			final SpannableString spannable, final int color, final SpanOnClickListener spanOnClickListener) {
		int spanEndIndex = endIndex < targetSpannable.length() ? endIndex + 1 : endIndex;
		if (stopWord(span)) {
			spannable.setSpan(new ForegroundColorSpan(color), startIndex, spanEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			spannable.setSpan(new TextClickableSpan(startIndex, endIndex, targetSpannable, spanOnClickListener, color), startIndex,
					spanEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	private static boolean stopWord(final String span) {
		return CommonUtils.STOP_WORDS.contains(span.toLowerCase());
	}
}
