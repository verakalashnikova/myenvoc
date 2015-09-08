package com.myenvoc.android.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableEntityAdapter implements Parcelable {
	private final Entity entity;

	public ParcelableEntityAdapter(final Entity entity) {
		super();
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeStringArray(new String[] { JSONConverter.convertToString(entity), entity.getClass().getName() });
	}

	public static final Parcelable.Creator<ParcelableEntityAdapter> CREATOR = new Parcelable.Creator<ParcelableEntityAdapter>() {
		@SuppressWarnings("unchecked")
		@Override
		public ParcelableEntityAdapter createFromParcel(final Parcel in) {
			String[] val = new String[2];
			in.readStringArray(val);
			try {
				return new ParcelableEntityAdapter(JSONConverter.loadSingleObject(val[0], (Class<Entity>) Class.forName(val[1])));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public ParcelableEntityAdapter[] newArray(final int size) {
			return new ParcelableEntityAdapter[size];
		}
	};
}
