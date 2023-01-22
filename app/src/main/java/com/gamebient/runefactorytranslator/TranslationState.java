package com.gamebient.runefactorytranslator;

import java.util.ArrayList;
import java.util.List;

public class TranslationState {
    private final List<TranslationStateEntryChangeListener> listeners = new ArrayList<>();


    public final String OriginalFileName;
    public final TableData Original;
    public final TableData Translation;

    public final int numberOfEntries;
    private int selectedEntryIndex;

    public TranslationState() {
        this (null, null, "Translation");
        selectedEntryIndex = 0;
    }

    public TranslationState(TableData original, TableData translation, String originalFileName) {
        Original = original;
        Translation = translation;
        OriginalFileName = originalFileName;
        selectedEntryIndex = -1;

        if (Original != null) {
            numberOfEntries = Original.GetEntries().length;
        }
        else {
            numberOfEntries = 0;
        }
    }

    public int GetSelectedEntryIndex() {
        return selectedEntryIndex;
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

    public String[] GetTableEntry() {
        String original =
                Original != null ?
                        Original.GetEntry(selectedEntryIndex) :
                        "There was no original data imported";
        String translated =
                Translation != null ?
                        Translation.GetEntry(selectedEntryIndex) :
                        "There was no translated data imported";
        return new String[] { original, translated };
    }

    public void SelectEntryContaining(String value) {
        int selectedIndexWhenStarted = selectedEntryIndex;
        boolean jumpedToBeginning = false;
        for (int i = selectedIndexWhenStarted; i < numberOfEntries; i++) {
            if (Translation != null) {
                if (Translation.DoesEntryContain(i, value) && selectedIndexWhenStarted < i) {
                    SelectEntry(i);
                    return;
                }
            }
            if (Original != null) {
                if (Original.DoesEntryContain(i, value)
                        && selectedIndexWhenStarted < i) {
                    SelectEntry(i);
                    return;
                }
            }

            if (i == numberOfEntries - 1) {
                if (!jumpedToBeginning) {
                    // TODO: Add debug call
                    jumpedToBeginning = true;
                    selectedIndexWhenStarted = 0;
                    i = 0;
                }
            }
        }
    }

    public void SelectEntry(int index) {
        if (index == selectedEntryIndex)
            return;

        if (index < 0)
            index = numberOfEntries - 1;
        if (index >= numberOfEntries)
            index = 0;

        selectedEntryIndex = index;
        invokeListeners();
    }

    public void SetEntryAtCurrentIndex(String translation) {
        if (Translation != null) {
            Translation.SetEntry(selectedEntryIndex, translation);
        }
    }
}
