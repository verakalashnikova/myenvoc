/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myenvoc.android.ui.dictionary;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

/**
 * An ad listener that toasts all ad events.
 */
public class ToastAdListener extends AdListener {
	private final Context mContext;
	private final AdView adView;

	// private final int size;

	public ToastAdListener(final Context context, final AdView adView) {
		this.mContext = context;
		this.adView = adView;
		// LayoutParams layoutParams = adView.getLayoutParams();
		// this.size = layoutParams.height;
		// layoutParams.height = 0;
		// adView.setLayoutParams(layoutParams);
	}

	@Override
	public void onAdLoaded() {
		// Toast.makeText(mContext, "onAdLoaded()", Toast.LENGTH_SHORT).show();
		//
		// LayoutParams layoutParams = adView.getLayoutParams();
		// layoutParams.height = xxx;
		// adView.setLayoutParams(layoutParams);
	}

	@Override
	public void onAdFailedToLoad(final int errorCode) {
		LayoutParams layoutParams = adView.getLayoutParams();
		layoutParams.height = 0;
		adView.setLayoutParams(layoutParams);
		// String errorReason = "";
		// switch (errorCode) {
		// case AdRequest.ERROR_CODE_INTERNAL_ERROR:
		// errorReason = "Internal error";
		// break;
		// case AdRequest.ERROR_CODE_INVALID_REQUEST:
		// errorReason = "Invalid request";
		// break;
		// case AdRequest.ERROR_CODE_NETWORK_ERROR:
		// errorReason = "Network Error";
		// break;
		// case AdRequest.ERROR_CODE_NO_FILL:
		// errorReason = "No fill";
		// break;
		// }
		// Toast.makeText(mContext, String.format("onAdFailedToLoad(%s)",
		// errorReason), Toast.LENGTH_SHORT).show();
	}

}
