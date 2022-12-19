package me.asu.startdict;

public enum SameType {

    m("m", "Word's pure text meaning. The data should be a utf-8 string ending with '\\0'."),
    l("l", "Word's pure text meaning. The data is NOT a utf-8 string, but is instead a string in locale encoding, ending with '\\0'."),
    g("g", "A utf-8 string which is marked up with the Pango text markup language."),
    t("t", "English phonetic string. The data should be a utf-8 string ending with '\\0'."),
    x("x", "A utf-8 string which is marked up with the xdxf language."),
    y("y", "Chinese YinBiao or Japanese KANA."),
    k("k", "KingSoft PowerWord's data. The data is a utf-8 string ending with '\\0'.It is in XML format."),
    w("w", "MediaWiki markup language."),
    h("h", "Html codes."),
    n("n", "WordNet data"),
    r("r", "Resource file list."),

    ;
    String code;
    String desc;

    private SameType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SameType getByCode(String code) {
        for (SameType value : values()) {
            if(value.code.equals(code)) return value;
        }
        return null;
    }
}