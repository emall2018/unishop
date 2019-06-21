package com.google.cloud.android.speech;

import com.ibm.watson.developer_cloud.language_translator.v3.util.Language;

import java.util.Locale;

public enum SupportedLanguages {
    ENGLISH_US("eng-US", "English", Language.ENGLISH, Locale.ENGLISH),
  //  ENGLISH_UK("eng-GBR", "English", "UK", Language.ENGLISH),
 //   ENGLISH_AUS("eng-USA", "English", "Australia", Language.ENGLISH),
  //  ARABIC_EGYPT("ara-EGY", "Arabic", "Egypt", Language.ARABIC),
//    ARABIC_SAUDI("ara-SAU", "Arabic", "Saudi", Language.ARABIC),
    ARABIC_UAE("ara-XWW", "Arabic", Language.ARABIC, Locale.getDefault() ),


  //  CZECH("ces-CZE", "Czech", Language.CZECH),
   // DANISH("dan-DNK", "Danish", Language.DANISH),
    DUTCH("de-DE", "Dutch", Language.DUTCH, Locale.GERMANY),
 //   FINNISH("fin-FIN", "Finnish", Language.FINNISH),
    FRENCH_EU("fr-FR", "French", Language.FRENCH, Locale.FRENCH),
    GERMAN("de-DE", "German", Language.GERMAN, Locale.GERMANY),
  //  GREEK("ell-GRC", "Greek", Language.GREEK),
 //   HEBREW("heb-ISR", "Hebrew", Language.HEBREW),
  //  HUNGARIAN("hun-HUN", "Hungarian", Language.HUNGARIAN),
  //  INDONESIAN("ind-IDN", "Indonesian", Language.INDONESIAN),
    ITALIAN("it-IT", "Italian", Language.ITALIAN, Locale.ITALIAN),
   // JAPANESE("jpn-JPN", "Japanese", Language.JAPANESE),
   // KOREAN("kor-KOR", "Korean", Language.KOREAN),


  //  POLISH("pol-POL", "Polish", Language.POLISH),
//    PORTUGUESE_BRA("por-BRA", "Portuguese", "Brazil", Language.PORTUGUESE),
  //  PORTUGUESE_EU("por-PRT", "Portuguese", "Europe", Language.PORTUGUESE),
   // ROMANIAN("ron-ROU", "Romanian", Language.ROMANIAN),
    RUSSIAN("ru-RU", "Russian", Language.RUSSIAN , Locale.getDefault()),

    SPANISH_EU("es-ES", "Spanish", Language.SPANISH, Locale.getDefault() ),

   // SWEDISH("swe-SWE", "Swedish", Language.SWEDISH)

    TURKISH("tr-TR", "Turkish", Language.TURKISH, Locale.getDefault()),

    VIETNAMESE("vi-VN", "Vietnamese", Language.VIETNAMESE, Locale.getDefault() );

    private final String mIsoCode;
    private final String mLabel;
    private final Locale mTranslateLang;
    private Locale tts;
private String mm;



 SupportedLanguages(String isoCode, String label, String mm, Locale translateLanguage) {
        this.mIsoCode = isoCode;
        this.mLabel = label;

        this.mm=mm;
        this.mTranslateLang = translateLanguage;
    }

    public String getIsoCode() {
        return mIsoCode;
    }

    public String mmm() {
        return mm;
    }

    public String getLabel() {
        return mLabel;
    }


    public Locale getTranslationLanguage() {
        return mTranslateLang;
    }
    public Locale fortts() {
        return mTranslateLang;
    }


    public static String fromLabel(String llabel) {
        SupportedLanguages[] values = SupportedLanguages.values();
        for (SupportedLanguages value : values) {
            if (value.getLabel().equals(llabel)) {
                return value.mmm();
            }
        }

        throw new IllegalArgumentException("IsoCode: " + llabel + " is not invalid!");
    }
    public static Locale totts(String Label) {
        SupportedLanguages[] values = SupportedLanguages.values();
        for (SupportedLanguages value : values) {
            if (value.getLabel().equals(Label)) {
                return value.fortts();
            }
        }

        throw new IllegalArgumentException("IsoCode: " + Label + " is not invalid!");
    }

    public static String tostt(String Label) {
        SupportedLanguages[] values = SupportedLanguages.values();
        for (SupportedLanguages value : values) {
            if (value.getLabel().equals(Label)) {
                return value.getIsoCode();
            }
        }

        throw new IllegalArgumentException("IsoCode: " + Label + " is not invalid!");
    }
}
