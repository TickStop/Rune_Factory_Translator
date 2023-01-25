package com.tickstop.runefactorytranslator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class FileSaveManager {
    /** Reads the contents of the InputStream
     * @return Bytes read from the stream */
    public static byte[] importBinaryTextFile(InputStream stream) {
        byte[] allBytes = new byte[0];
        int bufferSize = 1024 * 16;

        try {
            int nRead;
            byte[] data = new byte[bufferSize];

            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                while ((nRead = stream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                allBytes = buffer.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String header = new String(Arrays.copyOfRange(allBytes, 0, 4), GlobalData.Encoding_Default);
        if (!header.equals("TEXT")) {
            allBytes = new byte[0];
        }

        return allBytes;
    }

    /** Writes the TranslationState to an OutputStream */
    public static void exportBinaryTextFile(OutputStream stream, TranslationState state) {
        appendTableToFile(stream, state.getTranslation());
    }

    /** Writes the TableData to an OutputStream */
    public static void appendTableToFile(OutputStream stream, TableData data) {
        try {
            stream.write("TEXT".getBytes(GlobalData.Encoding_Default));
            stream.write(ByteConverter.intToByteArray(data.getNumberOfEntries()));

            int totalPosition = 8 + data.getEntries().length * 8;
            for (int i = 0; i < data.getEntries().length; i++) {
                stream.write(ByteConverter.intToByteArray(data.getEntries()[i].length));
                stream.write(ByteConverter.intToByteArray(totalPosition));
                totalPosition += data.getEntries()[i].length + 2;
            }
            for (int i = 0; i < data.getEntries().length; i++) {
                stream.write(data.getEntries()[i]);
                stream.write(0);
                stream.write(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
