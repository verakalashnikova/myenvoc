package com.myenvoc.android.service.network;

import com.myenvoc.android.domain.Failure;

/***
 * Simple interface which returns queried data.
 * 
 * @param <D>
 */
public interface GenericCallback<D> {

	void onSuccess(D data);

	void onError(Failure failure);

}
