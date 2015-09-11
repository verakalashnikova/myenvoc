package com.myenvoc.android.ui.user;

import javax.inject.Inject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.myenvoc.R;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.android.ui.dictionary.MyenvocHomeActivity;

public class SignOutDialog extends DialogFragment {
	@Inject
	UserService userService;

	public static void create(final FragmentManager supportFragmentManager) {
		DialogFragment dialog = new SignOutDialog();
		dialog.show(supportFragmentManager, "SignOutDialog");
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.signOutQuestion).setMessage(R.string.doYouWantToSignOut)
				.setPositiveButton(R.string.signOut, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						onAction();
					}

				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						SignOutDialog.this.getDialog().cancel();
					}
				});
		return builder.create();
	}

	private void onAction() {
		userService.signOut(getActivity());
		Intent intent = new Intent(getActivity(), MyenvocHomeActivity.class);
		getActivity().finish();
		getActivity().startActivity(intent);
	}
}
