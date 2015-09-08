package com.myenvoc.android.ui.dictionary;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.myenvoc.R;
import com.myenvoc.android.domain.GoogleImageSearchResponseResult;
import com.myenvoc.android.service.dictionary.ImageService;
import com.myenvoc.commons.CommonUtils;
import com.myenvoc.commons.UIUtils;
import com.myenvoc.commons.WrapContentGridView;

public class GoogleImagesWidget {
	public interface ImageSelectListener {
		void onSelect(String url);
	}

	public static View createGoogleImages(final ImageService imageService, final Context context, final RelativeLayout dictionaryTabLayout,
			final List<GoogleImageSearchResponseResult> imagesData, final ImageSelectListener imageOnClickListener) {
		GridView imagesGrid = new WrapContentGridView(context);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		imagesGrid.setLayoutParams(params);
		imagesGrid.setNumColumns(2);
		imagesGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		imagesGrid.setVerticalSpacing(20);
		imagesGrid.setGravity(Gravity.CENTER_HORIZONTAL);
		imagesGrid.setAdapter(new GoogleImagesAdapter(imageService, context, imagesData, imageOnClickListener));

		addBranding(dictionaryTabLayout, context);
		return imagesGrid;
	}

	private static void addBranding(final RelativeLayout layout, final Context context) {

		ImageView googleBranding = new ImageView(context);
		googleBranding.setImageResource(R.drawable.powered_by_google);

		View addWordLayout = layout.findViewById(R.id.addWord);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		if (addWordLayout == null) {
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			layout.addView(googleBranding, layoutParams);
		} else {
			layoutParams.addRule(RelativeLayout.ABOVE, R.id.addWord);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layout.addView(googleBranding, layoutParams);
		}

	}

	private static class GoogleImagesAdapter extends BaseAdapter {
		private final Context context;
		private final List<GoogleImageSearchResponseResult> imagesData;
		private final ImageService imageService;
		private final ImageSelectListener imageOnClickListener;

		public GoogleImagesAdapter(final ImageService imageService, final Context context,
				final List<GoogleImageSearchResponseResult> imagesData, final ImageSelectListener imageOnClickListener) {
			this.context = context;
			this.imagesData = imagesData;
			this.imageService = imageService;
			this.imageOnClickListener = imageOnClickListener;
		}

		@Override
		public int getCount() {
			return imagesData.size();
		}

		@Override
		public Object getItem(final int position) {
			return null;
		}

		@Override
		public long getItemId(final int position) {
			return 0;
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			if (convertView != null) {
				return convertView;
			}

			GoogleImageSearchResponseResult imageData = imagesData.get(position);
			final String url = imageData.getTbUrl();
			ImageView imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(UIUtils.convertToPixels(context, CommonUtils.IMAGE_WIDTH), UIUtils
					.convertToPixels(context, CommonUtils.IMAGE_HEIGHT)));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			if (imageOnClickListener != null) {
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(final View arg0) {
						imageOnClickListener.onSelect(url);
					}
				});
			}

			imageService.loadImage(url, imageView);

			return imageView;
		}

	}
}
