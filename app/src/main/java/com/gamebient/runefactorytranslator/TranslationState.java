package com.gamebient.runefactorytranslator;

import java.util.ArrayList;
import java.util.List;

public class TranslationState {
    private final List<TranslationStateEntryChangeListener> listeners = new ArrayList<>();


    public final String OriginalFileName;
    public final TableData Original;
    public final TableData Translation;

    public final int numberOfEntries;
    private int mSelectedEntryIndex;

    public TranslationState() {
        this (null, null, "Translation");
        mSelectedEntryIndex = 0;
    }

    public TranslationState(TableData original, TableData translation, String originalFileName) {
        Original = original;
        Translation = translation;
        OriginalFileName = originalFileName;
        mSelectedEntryIndex = -1;

        if (Original != null) {
            numberOfEntries = Original.GetEntries().length;
        }
        else {
            numberOfEntries = 0;
        }
    }

    /** @return the currently selected index */
    public int getSelectedEntryIndex() {
        return mSelectedEntryIndex;
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
                        Original.GetEntry(mSelectedEntryIndex) :
                        "There was no original data imported";
        String translated =
                Translation != null ?
                        Translation.GetEntry(mSelectedEntryIndex) :
                        "There was no translated data imported";
        return new String[] { original, translated };
    }

    /** Sets the index to the next occurrence of "value".
     * If no entry contains "value", the current index stays selected */
    public void selectEntryContaining(String value) {
        int selectedIndexWhenStarted = mSelectedEntryIndex;
        boolean jumpedToBeginning = false;
        for (int i = selectedIndexWhenStarted; i < numberOfEntries; i++) {
            if (Translation != null) {
                if (Translation.DoesEntryContain(i, value) && selectedIndexWhenStarted < i) {
                    selectEntry(i);
                    return;
                }
            }
            if (Original != null) {
                if (Original.DoesEntryContain(i, value)
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
        if (Translation != null) {
            Translation.SetEntry(mSelectedEntryIndex, translation);
        }
    }
}
