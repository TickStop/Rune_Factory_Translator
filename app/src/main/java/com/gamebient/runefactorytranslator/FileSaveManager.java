package com.gamebient.runefactorytranslator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class FileSaveManager {

    public static byte[] ImportBinaryTextFile(InputStream stream) {
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

            // int bytesRead = stream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String header = new String(Arrays.copyOfRange(allBytes, 0, 4), GlobalData.Encoding_Default);
        if (!header.equals("TEXT")) {
            allBytes = new byte[0];
        }


        return allBytes;
    }


    public static void ExportBinaryTextFile(OutputStream stream, TranslationState state) {
        AppendTableToFile(stream, state.Translation);
    }

    public static void AppendTableToFile(OutputStream stream, TableData data) {
        try {
            stream.write("TEXT".getBytes(GlobalData.Encoding_Default));
            stream.write(ByteConverter.intToByteArray(data.GetNumberOfEntries()));

            int totalPosition = 8 + data.GetEntries().length * 8;
            for (int i = 0; i < data.GetEntries().length; i++) {
                stream.write(ByteConverter.intToByteArray(data.GetEntries()[i].length));
                stream.write(ByteConverter.intToByteArray(totalPosition));
                totalPosition += data.GetEntries()[i].length + 2;
            }
            for (int i = 0; i < data.GetEntries().length; i++) {
                stream.write(data.GetEntries()[i]);
                stream.write(0);
                stream.write(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
