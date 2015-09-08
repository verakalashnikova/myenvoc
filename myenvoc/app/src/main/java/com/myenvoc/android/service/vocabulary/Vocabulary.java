package com.myenvoc.android.service.vocabulary;

import java.util.List;

import com.myenvoc.android.domain.MyWord;

public class Vocabulary {
	private static int currentWordIndex = 0;
	private int current;
	private final List<MyWord> myWords;
	private int totalWords;

	public Vocabulary(final List<MyWord> word) {
		this(word, -1);
	}

	public Vocabulary(final List<MyWord> words, final int totalWords) {

		this.myWords = words;
		if (words.isEmpty()) {
			current = -1;
		}
		if (totalWords < 0) {
			this.totalWords = this.myWords.size();
		} else {
			this.totalWords = totalWords;
		}
	}

	public MyWord getCurrent() {
		if (hasCurrent()) {
			return myWords.get(current);
		}
		return null;
	}

	public boolean hasCurrent() {
		return current >= 0;
	}

	public boolean hasNext() {
		return current < myWords.size() - 1;
	}

	public boolean hasPrev() {
		return current > 0;
	}

	public void next() {
		if (hasNext()) {
			current++;
			currentWordIndex = current;
		}
	}

	public void prev() {
		if (hasPrev()) {
			current--;
			currentWordIndex = current;
		}
	}

	public int getCurrentIndex() {
		return current;
	}

	public int getTotalGivenFilter() {
		return myWords.size();
	}

	public int getTotalWords() {
		return totalWords;
	}

	public boolean isEmpty() {
		return getTotalGivenFilter() == 0;
	}

	public void tryToMoveTo(final int wordNumber) {
		if (wordNumber < 0 || myWords.isEmpty()) {
			current = -1;
			return;
		}

		if (wordNumber < getTotalGivenFilter()) {
			current = wordNumber;
		} else if (wordNumber == getTotalGivenFilter()) {
			current = wordNumber - 1;
		}

	}

	public void removeCurrent() {
		int current = getCurrentIndex();
		myWords.remove(current);
		totalWords--; // as we remove a word, total number is also decremented
		tryToMoveTo(current);
	}

	public void updateCurrent(final MyWord updatedWord) {
		if (hasCurrent()) {
			myWords.set(getCurrentIndex(), updatedWord);
		}
	}

	public boolean hasFilter() {
		return getTotalGivenFilter() != getTotalWords();
	}

	public static void flushCurrentWord() {
		currentWordIndex = 0;
	}

	public void tryToMoveToSavedIndex() {
		tryToMoveTo(currentWordIndex);
	}

	public List<MyWord> getMyWords() {
		return myWords;
	}

}
