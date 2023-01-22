package com.gamebient.runefactorytranslator;

import java.util.Arrays;
import java.util.Locale;

public class TableData {
    private static final int TABLE_STARTPOS = 8;

    private final byte[] dataTable;
    private final int numberOfEntries;
    private final byte[][] entries;

    private final boolean isReadOnly;

    public TableData(byte[] table, boolean isReadOnly) {
        this.dataTable = table;
        this.isReadOnly = isReadOnly;
        byte[] tableLengthData = Arrays.copyOfRange(table, 4, 8);
        numberOfEntries = ByteConverter.byteArrayToInt(tableLengthData);
        // numberOfEntries = ByteBuffer.wrap(tableLengthData).order(ByteOrder.LITTLE_ENDIAN).getInt();
        entries = new byte[numberOfEntries][];

        ReadEntries();
    }


    public int GetNumberOfEntries() {
        return  numberOfEntries;
    }

    public byte[][] GetEntries() {
        return  entries;
    }

    private int[] GetRange(int index, int startOffset) {
        int range_start = TABLE_STARTPOS + startOffset + 8 * index;
        int range_end = range_start + 4;
        return new int[] { range_start, range_end };
    }

    private int[] GetEntryPositions() {
        int[] entryPositions = new int[numberOfEntries];
        for (int entryIndex = 0; entryIndex < numberOfEntries; entryIndex++)
        {
            int[] range = GetRange(entryIndex, 4);
            int entryPos = ByteConverter.byteArrayToInt(Arrays.copyOfRange(dataTable, range[0], range[1]));
            entryPositions[entryIndex] = entryPos;
        }
        return entryPositions;
    }

    private int[] GetEntryLengths() {
        int[] entryLengths = new int[numberOfEntries];
        for (int entryIndex = 0; entryIndex < numberOfEntries; entryIndex++)
        {
            int[] range = GetRange(entryIndex, 0);
            int entryPos = ByteConverter.byteArrayToInt(Arrays.copyOfRange(dataTable, range[0], range[1]));
            entryLengths[entryIndex] = entryPos;
        }
        return entryLengths;
    }

    private void ReadEntries() {
        int[] entryPositions = GetEntryPositions();
        int[] entryLengths = GetEntryLengths();
        for (int i = 0; i < numberOfEntries; i++)
        {
            int start = entryPositions[i];
            int end = start + entryLengths[i];
            entries[i] = Arrays.copyOfRange(dataTable, start, end);
        }
    }


    public boolean DoesEntryContain(int index, String match) {
        if (index >= entries.length)
            return false;

        String entry = new String(entries[index], GlobalData.Encoding_Default);
        return entry.toLowerCase(Locale.ROOT).contains(match.toLowerCase(Locale.ROOT));
    }

    public String GetEntry(int index) {
        if (index >= entries.length)
            return "";

        byte[] currentEntry = new byte[entries[index].length];
        System.arraycopy(entries[index], 0, currentEntry, 0, currentEntry.length);

        ByteConverter.Replace(currentEntry, GlobalData.text_blue.original, GlobalData.text_blue.display);
        ByteConverter.Replace(currentEntry, GlobalData.text_red.original, GlobalData.text_red.display);

        return new String(currentEntry, GlobalData.Encoding_Default);
    }

    public void SetEntry(int index, String newText) {
        if (isReadOnly)
            return;

        byte[] entry = newText.getBytes(GlobalData.Encoding_Default);

        ByteConverter.Replace(entry, GlobalData.text_blue.display, GlobalData.text_blue.original);
        ByteConverter.Replace(entry, GlobalData.text_red.display, GlobalData.text_red.original);

        entries[index] = entry;
    }
}
