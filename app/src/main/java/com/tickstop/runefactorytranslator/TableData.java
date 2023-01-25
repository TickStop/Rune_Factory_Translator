package com.tickstop.runefactorytranslator;

import java.util.Arrays;
import java.util.Locale;

public class TableData {
    /** The file starts with the identifier (TEXT) followed by a 32bit integer.
     *  The start position of the table is therefore 8. */
    private static final int TABLE_STARTPOS = 8;

    /** The number of entries this table has */
    private final int numberOfEntries;
    private final byte[][] entries;

    public TableData(byte[] table) {
        byte[] tableLengthData = Arrays.copyOfRange(table, 4, 8);
        numberOfEntries = ByteConverter.byteArrayToInt(tableLengthData);
        entries = new byte[numberOfEntries][];

        readEntries(table);
    }

    /** @return the number of entries this table has */
    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    /** @return the entries' byte array */
    public byte[][] getEntries() {
        return  entries;
    }

    /** @return the range as an array
     * <br> [0] start
     * <br> [1] end */
    private int[] getRange(int index, int startOffset) {
        int range_start = TABLE_STARTPOS + startOffset + 8 * index;
        int range_end = range_start + 4;
        return new int[] { range_start, range_end };
    }

    /** @return the positions of the entries in the file */
    private int[] getEntryPositions(byte[] dataTable) {
        int[] entryPositions = new int[numberOfEntries];
        for (int entryIndex = 0; entryIndex < numberOfEntries; entryIndex++)
        {
            int[] range = getRange(entryIndex, 4);
            int entryPos = ByteConverter.byteArrayToInt(Arrays.copyOfRange(dataTable, range[0], range[1]));
            entryPositions[entryIndex] = entryPos;
        }
        return entryPositions;
    }

    /** @return the lengths of the entries in the file */
    private int[] getEntryLengths(byte[] dataTable) {
        int[] entryLengths = new int[numberOfEntries];
        for (int entryIndex = 0; entryIndex < numberOfEntries; entryIndex++)
        {
            int[] range = getRange(entryIndex, 0);
            int entryPos = ByteConverter.byteArrayToInt(Arrays.copyOfRange(dataTable, range[0], range[1]));
            entryLengths[entryIndex] = entryPos;
        }
        return entryLengths;
    }

    /** Reads all entries and stores them in {@link TableData#entries} */
    private void readEntries(byte[] dataTable) {
        int[] entryPositions = getEntryPositions(dataTable);
        int[] entryLengths = getEntryLengths(dataTable);
        for (int i = 0; i < numberOfEntries; i++)
        {
            int start = entryPositions[i];
            int end = start + entryLengths[i];
            entries[i] = Arrays.copyOfRange(dataTable, start, end);
        }
    }

    /** Searches for a string in the entry specified by the index
     *  @return True if the entry contains the string, false otherwise */
    public boolean doesEntryContain(int index, String match) {
        if (index >= entries.length)
            return false;

        String entry = new String(entries[index], GlobalData.Encoding_Default);
        return entry.toLowerCase(Locale.ROOT).contains(match.toLowerCase(Locale.ROOT));
    }

    /** Converts the entry from bytes to a String
     * @param index Index of the entry
     * @return The String representation of the entry */
    public String getEntry(int index) {
        if (index >= entries.length)
            return "";

        byte[] currentEntry = new byte[entries[index].length];
        System.arraycopy(entries[index], 0, currentEntry, 0, currentEntry.length);

        ByteConverter.replace(currentEntry, GlobalData.text_red.original, GlobalData.text_red.display);
        ByteConverter.replace(currentEntry, GlobalData.text_blue.original, GlobalData.text_blue.display);

        return new String(currentEntry, GlobalData.Encoding_Default);
    }

    /** Sets the entry to the specified String in bytes
     * @param index Index of the entry
     * @param newText New text of the entry */
    public void setEntry(int index, String newText) {
        byte[] entry = newText.getBytes(GlobalData.Encoding_Default);

        ByteConverter.replace(entry, GlobalData.text_red.display, GlobalData.text_red.original);
        ByteConverter.replace(entry, GlobalData.text_blue.display, GlobalData.text_blue.original);

        entries[index] = entry;
    }
}
