package com.myenvoc.commons;

import android.content.Context;
import android.graphics.Typeface;

public class CustomFontsLoader {

	private static boolean fontsLoaded = false;

	public static final int GENTIUM = 0;
	private static String[] fontPath = { "fonts/Gentium-R.ttf" };
	private static Typeface[] fonts = new Typeface[fontPath.length];

	/**
	 * Returns a loaded custom font based on it's identifier.
	 * 
	 * @param context
	 *            - the current context
	 * @param fontIdentifier
	 *            = the identifier of the requested font
	 * 
	 * @return Typeface object of the requested font.
	 */
	public static Typeface getTypeface(final Context context, final int fontIdentifier) {
		if (!fontsLoaded) {
			loadFonts(context);
		}
		return fonts[fontIdentifier];
	}

	private static void loadFonts(final Context context) {
		for (int i = 0; i < fonts.length; i++) {
			fonts[i] = Typeface.createFromAsset(context.getAssets(), fontPath[i]);
		}
		fontsLoaded = true;

	}
}
