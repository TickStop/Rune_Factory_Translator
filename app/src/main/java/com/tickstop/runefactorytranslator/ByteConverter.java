package com.tickstop.runefactorytranslator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ByteConverter {
    /** Searches for the index at which a match starts
     * @return index at which the match starts */
    public static int find(byte[] src, byte[] match) {
        int index = -1;
        for (int i = 0, matchIndex = 0; i < src.length; i++) {
            if (src[i] == match[matchIndex]) {
                if (matchIndex == (match.length - 1)) {
                    index = i - matchIndex;
                    break;
                }
                matchIndex++;
            }
            else if (src[i] == match[0]) {
                matchIndex = 1;
            }
            else {
                matchIndex = 0;
            }
        }

        return index;
    }

    /** Checks if byte array arr contains byte array match
     * @return true if the byte array b is contained in arr, false otherwise */
    public static boolean doesArrayContain(byte[] arr, byte[] match) {
        return arr.length == match.length && find(arr, match) != -1;
    }

    /** Replaces matches containing "search" of the array "src" with "replacement" */
    public static void replace(byte[] src, byte[] search, byte[] replacement) {
        int index = find(src, search);

        while (index >= 0) {
            int i_repl = 0;
            for (int i = index; i < index + search.length; i++) {
                src[i] = replacement[i_repl];
                i_repl++;
            }

            index = find(src, search);
        }
    }

    /** Converts an integer to its byte representation
     * @return the integer as a byte array */
    public static byte[] intToByteArray (int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    /** Converts a byte array to an integer
     * @return the bytes as an integer */
    public static int byteArrayToInt(byte[] value) {
        return ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(GlobalData.Encoding_Default);
    /** Converts a byte array to a readable hexadecimal String */
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
