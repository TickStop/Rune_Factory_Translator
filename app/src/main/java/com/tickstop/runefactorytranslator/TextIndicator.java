package com.tickstop.runefactorytranslator;


public final class TextIndicator {
    public final byte[] original;
    public final byte[] display;

    public TextIndicator(byte[] original, byte[] display) {
        this.original = original;
        this.display = display;
    }

    public TextIndicator(byte[] original, String display) {
        this (original, display.getBytes(GlobalData.Encoding_Default));
    }
}
