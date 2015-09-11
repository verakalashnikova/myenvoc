package com.myenvoc.android.ui.dictionary;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.myenvoc.R;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.domain.GoogleImageSearchResponseResult;
import com.myenvoc.android.domain.GoolgeImageSearchResult;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.commons.ThreadPoolExecutor;
import com.myenvoc.commons.VocabularyUtils;

/**
 * Displays a word and its definition.
 */
public class ImagesFragment extends DictionaryTab {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		setupActivity();

		final String word = getWordFromIntent();
		final View tab = inflater.inflate(R.layout.dictionary_tab, null);

		installWordTitle(tab, getActivitySafe());
		final View inProgress = onStartQuerying(tab, word);

		dictionaryService.queryGoogleImages(word, new GenericCallback<GoolgeImageSearchResult>() {

			@Override
			public void onSuccess(final GoolgeImageSearchResult data) {
				if (isActivityDead()) {
					return;
				}
				hideProgress(inProgress);
				renderImages(tab, data, word);
			}

			@Override
			public void onError(final Failure failure) {
				if (isActivityDead()) {
					return;
				}
				hideProgress(inProgress);
				handleError(tab, failure, ImagesFragment.this.getActivitySafe());
			}
		}, ThreadPoolExecutor.getCallbackInUIThreadParameters(MyenvocHomeActivity.getHandler()));
		return tab;

	}

	private void renderImages(final View tab, final GoolgeImageSearchResult data, final String word) {
		ViewGroup parent = (ViewGroup) tab.findViewById(R.id.tabContent);

		List<GoogleImageSearchResponseResult> googleImages = VocabularyUtils.getGoogleImagesFromResponse(data);
		if (googleImages.isEmpty()) {
			parent.addView(noDataFound(R.string.no_images_found, word));
		} else {

			RelativeLayout dictionaryTabLayout = (RelativeLayout) tab.findViewById(R.id.dictionaryTabLayout);

			View createGoogleImages = GoogleImagesWidget.createGoogleImages(imageService, getActivitySafe(), dictionaryTabLayout,
					googleImages, new GoogleImagesWidget.ImageSelectListener() {

						@Override
						public void onSelect(final String url) {
							handleAddFromImages(url);
						}
					});

			parent.addView(createGoogleImages);
		}
	}

}
