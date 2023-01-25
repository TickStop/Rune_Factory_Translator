package com.tickstop.runefactorytranslator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class GlobalData {
    /** The encoding used by the game */
    public static Charset Encoding_Default = StandardCharsets.UTF_8;

    /** An indicator for text that would be red in the game */
    public static final TextIndicator text_blue = new TextIndicator(new byte[] { (byte) 0xef, (byte) 0xbc, (byte) 0x8b }, "_b_");
    /** An indicator for text that would be blue in the game */
    public static final TextIndicator text_red = new TextIndicator(new byte[] { (byte) 0xef, (byte) 0xbc, (byte) 0x8f }, "_n_");
}
