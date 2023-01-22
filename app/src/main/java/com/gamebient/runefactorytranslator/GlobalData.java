package com.gamebient.runefactorytranslator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class GlobalData {
    public static Charset Encoding_Default = StandardCharsets.UTF_8;

    public static final TextIndicator text_red   = new TextIndicator(new byte[] { (byte) 0xef, (byte) 0xbc, (byte) 0x8b }, "_b_");
    public static final TextIndicator text_blue  = new TextIndicator(new byte[] { (byte) 0xef, (byte) 0xbc, (byte) 0x8f }, "_n_");
}
