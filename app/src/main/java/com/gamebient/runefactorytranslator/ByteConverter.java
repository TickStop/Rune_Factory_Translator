package com.gamebient.runefactorytranslator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ByteConverter {
    public static int Find(byte[] src, byte[] find) {
        int index = -1;
        for (int i = 0, matchIndex = 0; i < src.length; i++)
        {
            if (src[i] == find[matchIndex])
            {
                if (matchIndex == (find.length - 1))
                {
                    index = i - matchIndex;
                    break;
                }
                matchIndex++;
            }
            else if (src[i] == find[0])
            {
                matchIndex = 1;
            }
            else
            {
                matchIndex = 0;
            }
        }

        return index;
    }

    public static void Replace(byte[] src, byte[] search, byte[] replacement) {
        int index = Find(src, search);

        while (index >= 0)
        {
            int i_repl = 0;
            for (int i = index; i < index + search.length; i++)
            {
                src[i] = replacement[i_repl];
                i_repl++;
            }

            index = Find(src, search);
        }
    }

    public static byte[] intToByteArray (int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    public static int byteArrayToInt(byte[] value) {
        return ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(GlobalData.Encoding_Default);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[j * 3 + 2] = 0x20;
        }
        return new String(hexChars, GlobalData.Encoding_Default);
    }

    public static String bytesToHex(byte[] bytes, boolean wrapAfterEight) {
        if (!wrapAfterEight)
            return  bytesToHex(bytes);
        byte[] hexChars = new byte[bytes.length * 3 + bytes.length / 8];
        for (int j = 0; j < bytes.length; j++) {
            int lines = j / 8;
            int v = bytes[j] & 0xFF;
            hexChars[j * 3 + lines] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + lines + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[j * 3 + lines + 2] = 0x20;
            if (j % 8 == 0)
                hexChars[j * 3 + lines + 3] = 0x0A;
        }
        return new String(hexChars, GlobalData.Encoding_Default);
    }
}
