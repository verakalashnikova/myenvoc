package com.myenvoc.android.domain;

import com.myenvoc.android.service.user.User;

public class AnonymousUser implements User {
	private final int id;

	public AnonymousUser(final int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean isAnonymous() {
		return true;
	}

}
