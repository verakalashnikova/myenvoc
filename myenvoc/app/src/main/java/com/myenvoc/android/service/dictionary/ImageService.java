package com.myenvoc.android.service.dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.inject.Inject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.myenvoc.android.dao.dictionary.VocabularyDatabase;
import com.myenvoc.android.domain.Failure;
import com.myenvoc.android.service.network.GenericCallback;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;
import com.myenvoc.commons.ThreadPoolExecutor;

public class ImageService extends AbstractLoader<Bitmap> {
	private final String tag = ImageService.class.getName();

	@Inject
	VocabularyDatabase vocabularyDatabase;

	public void loadImage(final String url, final ImageView installTo) {
		load(url, new GenericCallback<Bitmap>() {

			@Override
			public void onSuccess(final Bitmap data) {
				installTo.setImageBitmap(data);
			}

			@Override
			public void onError(final Failure failure) {
				// TODO set no images found error. Note, now there is a bug - it
				// will be executed in background thread.
			}
		}, ThreadPoolExecutor.getCallbackInUIThreadParameters(MyenvocHomeActivity.getHandler()));
	}

	@Override
	protected Bitmap loadFromInternet(final String request) {
		Log.i(tag, "Loading image from Internet: " + request);
		URL newurl;
		InputStream inputStream = null;
		try {
			newurl = new URL(request);
			inputStream = newurl.openConnection().getInputStream();
			Bitmap decodeStream = BitmapFactory.decodeStream(inputStream);

			if (decodeStream == null) {
				throw new RuntimeException("Unable to decode image");
			}
			return decodeStream;
		} catch (Exception e) {
			Log.e(tag, "Unable to load image", e);
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	protected Bitmap loadFromDataBase(final String request) {
		return vocabularyDatabase.findImage(request);
	}

	@Override
	protected void saveToDatabase(final String request, final Bitmap result) {
		vocabularyDatabase.insertImage(request, result);
	}

	@Override
	protected String getCacheKey(final String request) {
		return request;
	}

}
