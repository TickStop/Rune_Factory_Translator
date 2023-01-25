package com.tickstop.runefactorytranslator;

import java.util.ArrayList;
import java.util.List;

public class TranslationState {
    private final List<TranslationStateEntryChangeListener> listeners = new ArrayList<>();


    public final String OriginalFileName;
    public final TableData Original;
    private TableData mTranslation;

    public final int numberOfEntries;
    private int mSelectedEntryIndex;

    public TranslationState() {
        this (null, "Translation", null);
        mSelectedEntryIndex = 0;
    }

    public TranslationState(TableData original, String originalFileName) {
        this(original, originalFileName, null);
    }

    public TranslationState(TableData original, String originalFileName, TableData translation) {
        Original = original;
        mTranslation = translation;
        OriginalFileName = originalFileName;
        mSelectedEntryIndex = -1;

        if (Original != null) {
            numberOfEntries = Original.getEntries().length;
        }
        else {
            numberOfEntries = 0;
        }
    }

    /** @return the currently selected index */
    public int getSelectedEntryIndex() {
        return mSelectedEntryIndex;
    }

    public TableData getTranslation() {
        return mTranslation;
    }

    public void setTranslation(TableData translation) {
        if (Original == null)
            return;
        if (translation.getNumberOfEntries() != Original.getNumberOfEntries())
            return;
        mTranslation = translation;
    }


    public void addListener(TranslationStateEntryChangeListener toAdd) {
        listeners.add(toAdd);
    }

    public void removeListener(TranslationStateEntryChangeListener toRemove) {
        listeners.remove(toRemove);
    }

    public void invokeListeners() {
        for (TranslationStateEntryChangeListener l : listeners) {
            l.Execute(this);
        }
    }

    /** @return the original and translation in String format.
     * <br> [0] Original
     * <br> [1] Translation */
    public String[] getTableEntry() {
        String original =
                Original != null ?
                        Original.getEntry(mSelectedEntryIndex) :
                        "There was no original data imported";
        String translated =
                mTranslation != null ?
                        mTranslation.getEntry(mSelectedEntryIndex) :
                        "There was no translated data imported";
        return new String[] { original, translated };
    }

    /** Sets the index to the next occurrence of "value".
     * If no entry contains "value", the current index stays selected */
    public void selectEntryContaining(String value) {
        int selectedIndexWhenStarted = mSelectedEntryIndex;
        boolean jumpedToBeginning = false;
        for (int i = selectedIndexWhenStarted; i < numberOfEntries; i++) {
            if (mTranslation != null) {
                if (mTranslation.doesEntryContain(i, value) && selectedIndexWhenStarted < i) {
                    selectEntry(i);
                    return;
                }
            }
            if (Original != null) {
                if (Original.doesEntryContain(i, value)
                        && selectedIndexWhenStarted < i) {
                    selectEntry(i);
                    return;
                }
            }

            if (i == numberOfEntries - 1) {
                if (!jumpedToBeginning) {
                    jumpedToBeginning = true;
                    selectedIndexWhenStarted = 0;
                    i = 0;
                }
            }
        }
    }

    /** Sets the selected index */
    public void selectEntry(int index) {
        if (index == mSelectedEntryIndex)
            return;

        if (index < 0)
            index = numberOfEntries - 1;
        if (index >= numberOfEntries)
            index = 0;

        mSelectedEntryIndex = index;
        invokeListeners();
    }

    /** Sets the current entry of the translation to the passed String */
    public void setEntryAtCurrentIndex(String translation) {
        if (mTranslation != null) {
            mTranslation.setEntry(mSelectedEntryIndex, translation);
        }
    }
}
