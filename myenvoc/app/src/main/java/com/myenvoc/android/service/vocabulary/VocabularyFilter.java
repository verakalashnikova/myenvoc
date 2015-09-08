package com.myenvoc.android.service.vocabulary;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.myenvoc.android.dao.dictionary.VocabularyOrder;
import com.myenvoc.android.domain.JSONConverter;
import com.myenvoc.android.domain.MyWordStatus;
import com.myenvoc.commons.CommonUtils;

public class VocabularyFilter implements Parcelable {
	private static final String VOCABULARY_FILTER = "VOCABULARY_FILTER";

	private static VocabularyFilter SESSION_FILTER;

	public static VocabularyFilter getCurrentVocabularyFilter(final Context context) {
		if (SESSION_FILTER == null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			VocabularyFilterDto vocabularyFilterDto = null;
			String dtoString = preferences.getString(VOCABULARY_FILTER, null);
			if (dtoString != null) {
				try {
					vocabularyFilterDto = JSONConverter.loadSingleObject(dtoString, VocabularyFilterDto.class);
				} catch (Exception e) {
					// may fail due to future version incompatibilities.
				}
			}
			final VocabularyFilter vocabularyFilter;
			if (vocabularyFilterDto == null) {
				/** default filter. */
				vocabularyFilter = new VocabularyFilter("", AddedAt.EVER, EnumSet.allOf(MyWordStatus.class), VocabularyOrder.ADDED_DATE);
				setCurrentVocabularyFilter(vocabularyFilter, context);
			} else {
				vocabularyFilter = VocabularyFilter.fromDto(vocabularyFilterDto);
			}
			SESSION_FILTER = vocabularyFilter;
		}
		return SESSION_FILTER;
	}

	public static void setCurrentVocabularyFilter(final VocabularyFilter vocabularyFilter, final Context context) {
		SESSION_FILTER = vocabularyFilter;

		/**
		 * NOTE!!! Due to weird lack of ability to store Parcel into shared
		 * preferences, introduce new JSON dto.
		 */
		VocabularyFilterDto dto = vocabularyFilter.toDto();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = preferences.edit();
		edit.putString(VOCABULARY_FILTER, JSONConverter.convertToString(dto));
		edit.commit();

	}

	public static boolean filterSet(final Context context) {
		VocabularyFilter vocabularyFilter = getCurrentVocabularyFilter(context);
		return vocabularyFilter != null && vocabularyFilter.filterIsSet();
	}

	public static VocabularyOrder getCurrentVocabularyOrder(final Context context) {
		VocabularyFilter vocabularyFilter = getCurrentVocabularyFilter(context);
		return vocabularyFilter == null ? VocabularyOrder.ADDED_DATE : vocabularyFilter.getVocabularyOrder();
	}

	public enum AddedAt {
		EVER, TODAY {
			@Override
			public Date getFilterDate() {
				return CommonUtils.truncateDate(new Date());
			}
		},
		WEEK_AGO {
			@Override
			public Date getFilterDate() {
				return CommonUtils.subtractAndTruncateDate(new Date(), 7);
			}
		},
		MONTH_AGO {
			@Override
			public Date getFilterDate() {
				return CommonUtils.subtractAndTruncateDate(new Date(), 31);
			}
		};

		public Date getFilterDate() {
			return null;
		}

	}

	private final String startsWith;
	private final AddedAt addedAt;
	private final EnumSet<MyWordStatus> wordStatuses;
	private final VocabularyOrder vocabularyOrder;

	public VocabularyFilter(final String startsWith, final AddedAt addedAt, final EnumSet<MyWordStatus> wordStatuses,
			final VocabularyOrder vocabularyOrder) {
		super();
		this.startsWith = startsWith;
		this.addedAt = addedAt;
		this.wordStatuses = wordStatuses;
		this.vocabularyOrder = vocabularyOrder;
	}

	public String getStartsWith() {
		return startsWith;
	}

	public AddedAt getAddedAt() {
		return addedAt;
	}

	public VocabularyOrder getVocabularyOrder() {
		return vocabularyOrder;
	}

	public EnumSet<MyWordStatus> getWordStatuses() {
		return wordStatuses;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int arg) {
		parcel.writeString(startsWith);
		parcel.writeString(addedAt.name());
		parcel.writeString(vocabularyOrder.name());
		List<String> wordStatusesList = new ArrayList<String>();

		for (MyWordStatus status : wordStatuses) {
			wordStatusesList.add(status.name());
		}

		parcel.writeStringList(wordStatusesList);
	}

	public VocabularyFilterDto toDto() {
		VocabularyFilterDto dto = new VocabularyFilterDto();
		dto.setWordStart(startsWith);
		dto.setAddedAt(addedAt.name());
		dto.setOrder(vocabularyOrder.name());
		List<String> wordStatusesList = new ArrayList<String>();

		for (MyWordStatus status : wordStatuses) {
			wordStatusesList.add(status.name());
		}

		dto.setStatuses(wordStatusesList);
		return dto;
	}

	public static VocabularyFilter fromDto(final VocabularyFilterDto dto) {
		String startsWith = dto.getWordStart();
		AddedAt addedAt = AddedAt.valueOf(dto.getAddedAt());
		VocabularyOrder vocabularyOrder = VocabularyOrder.valueOf(dto.getOrder());
		List<String> wordStatusesList = dto.getStatuses();

		final EnumSet<MyWordStatus> wordStatuses = convertToStatuses(wordStatusesList);
		return new VocabularyFilter(startsWith, addedAt, wordStatuses, vocabularyOrder);
	}

	public static final Parcelable.Creator<VocabularyFilter> CREATOR = new Parcelable.Creator<VocabularyFilter>() {

		@Override
		public VocabularyFilter createFromParcel(final Parcel in) {
			String startsWith = in.readString();
			AddedAt addedAt = AddedAt.valueOf(in.readString());
			VocabularyOrder vocabularyOrder = VocabularyOrder.valueOf(in.readString());
			List<String> wordStatusesList = new ArrayList<String>();
			in.readStringList(wordStatusesList);

			final EnumSet<MyWordStatus> wordStatuses = convertToStatuses(wordStatusesList);

			return new VocabularyFilter(startsWith, addedAt, wordStatuses, vocabularyOrder);

		}

		@Override
		public VocabularyFilter[] newArray(final int size) {
			return new VocabularyFilter[size];
		}

	};

	public boolean filterIsSet() {
		return CommonUtils.isNotEmpty(startsWith) || addedAt != AddedAt.EVER
				|| !wordStatuses.containsAll(EnumSet.allOf(MyWordStatus.class));
	}

	private static EnumSet<MyWordStatus> convertToStatuses(final List<String> wordStatusesList) {
		final EnumSet<MyWordStatus> wordStatuses;
		if (wordStatusesList.isEmpty()) {
			wordStatuses = EnumSet.noneOf(MyWordStatus.class);
		} else {
			wordStatuses = EnumSet.copyOf(Collections2.transform(wordStatusesList, new Function<String, MyWordStatus>() {

				@Override
				public MyWordStatus apply(final String string) {
					return MyWordStatus.valueOf(string);
				}
			}));
		}
		return wordStatuses;
	}
}
