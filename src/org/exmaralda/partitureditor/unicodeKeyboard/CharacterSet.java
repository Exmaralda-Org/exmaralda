/*
 * CharacterSet.java
 *
 * Created on 7. Februar 2002, 17:14
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

/**
 *
 * @author  Thomas
 * @version 
 */
public class CharacterSet {

    public static String[] HIAT_BASIC_CHAR_SET = {"m\u0301","m\u0300","m\u030c","m\u0302","m\u0304", // m + diacritics
                                                  "\u0301","\u0300","\u030c","\u0302","\u0304", // isolated diacritics
                                                  "\u2190","\u2191","\u2193","\u2192","\u2197","\u2198", // arrows
                                                  "\u2026",  "\u2013", // three dots, n-dash (Gedankenstrich)
                                                  "\u2022","\u00B7","((_s))","\u203F"}; // Pause dot and written out, Ligature

    public static String[] HIAT_PORTUGUESE_CHAR_SET = {"m\u0301","m\u0300","m\u030c","m\u0302","m\u0304", // m + diacritics
                                                     "\u0301","\u0300","\u030c","\u0302","\u0304", // isolated diacritics
                                                     "\u2190","\u2191","\u2193","\u2192","\u2197","\u2198", // arrows
                                                     "\u2026",  "\u2013", // three dots, n-dash (Gedankenstrich)
                                                     "\u2022","\u00B7","((_s))","\u203F", // Pause dot and written out, Ligature                                                     "\u00C0","\u00C1","\u00C2","\u00C3", // Capital As with diacritics
                                                     "\u00C7", //Capital c cedilla
                                                     "\u00C9","\u00CA", // Capital Es with diacritics
                                                     "\u00CD", // capital I with accent aigu
                                                     "\u00D2","\u00D3","\u00D4","\u00D5", // capital Os with diacritics
                                                     "\u00DA","\u00DC", // capital Us with diacritics
                                                     "\u00E0","\u00E1","\u00E2","\u00E3",  // small as with diacritics
                                                     "\u00E7", // small c cedilla
                                                     "\u00E9","\u00EA", // small es with diacritics
                                                     "\u00ED", // small i with accent aigu
                                                     "\u00F2","\u00F3","\u00F4","\u00F5", // small os with diacritics
                                                     "\u00FA","\u00FC"}; // small us with diacritics

                                                     
    public static String[] HIAT_SCANDINAVIAN_CHAR_SET = {"m\u0301","m\u0300","m\u030c","m\u0302","m\u0304", // m + diacritics
                                                         "\u0301","\u0300","\u030c","\u0302","\u0304", // isolated diacritics
                                                         "\u2190","\u2191","\u2193","\u2192","\u2197","\u2198", // arrows
                                                         "\u2026",  "\u2013", // three dots, n-dash (Gedankenstrich)
                                                         "\u2022","\u00B7","((_s))","\u203F", // Pause dot and written out, Ligature                                                     "\u00C0","\u00C1","\u00C2","\u00C3", // Capital As with diacritics
                                                         "\u00C4", "\u00C5", "\u00C6", // capital As with diacritics / ligature
                                                         "\u00D8", // capital O with circle
                                                         "\u00D0","\u00DE", // capital Icelandic chars
                                                         "\u00E4","\u00E5","\u00E6", // small as with diacritics / ligature
                                                         "\u00F8", // small o with circle
                                                         "\u00F0","\u00FE"}; // small Icelandic chars

    public static String[] HIAT_TURKISH_CHAR_SET = {"m\u0301","m\u0300","m\u030c","m\u0302","m\u0304", // m + diacritics
                                                    "\u0301","\u0300","\u030c","\u0302","\u0304", // isolated diacritics
                                                    "\u2190","\u2191","\u2193","\u2192","\u2197","\u2198", // arrows
                                                    "\u2026",  "\u2013", // three dots, n-dash (Gedankenstrich)
                                                    "\u2022","\u00B7","((_s))","\u203F", // Pause dot and written out, Ligature                                                     "\u00C0","\u00C1","\u00C2","\u00C3", // Capital As with diacritics
                                                    "\u00C2","\u00C7","\u011E","\u0130","\u00CE","\u00D6","\u015E","\u0160","\u00DB","\u00DC", // capital letters
                                                    "\u00E2","\u00E7","\u011F","\u0131","\u00EE","\u00F6","\u015F","\u0161","\u00FB","\u00FC"}; // small letters

    public static String[] HIAT_SPANISH_CHAR_SET = {"m\u0301","m\u0300","m\u030c","m\u0302","m\u0304", // m + diacritics
                                                    "\u0301","\u0300","\u030c","\u0302","\u0304", // isolated diacritics
                                                    "\u2190","\u2191","\u2193","\u2192","\u2197","\u2198", // arrows
                                                    "\u2026",  "\u2013", // three dots, n-dash (Gedankenstrich)
                                                    "\u2022","\u00B7","((_s))","\u203F", // Pause dot and written out, Ligature                                                     "\u00C0","\u00C1","\u00C2","\u00C3", // Capital As with diacritics
                                                    "\u00C1","\u00C9","\u00CD","\u00D1","\u00D3","\u00DA","\u00DC", // Capital letters + diacritics
                                                    "\u00E0","\u00E9","\u00ED","\u00F1","\u00F3","\u00FA","\u00FC", // small letters + diacritics
                                                    "\u00BF","\u00A1"}; // punctuation

    public static String[] HIAT_FRENCH_CHAR_SET = {"m\u0301","m\u0300","m\u030c","m\u0302","m\u0304", // m + diacritics
                                                   "\u0301","\u0300","\u030c","\u0302","\u0304", // isolated diacritics
                                                   "\u2190","\u2191","\u2193","\u2192","\u2197","\u2198", // arrows
                                                   "\u2026",  "\u2013", // three dots, n-dash (Gedankenstrich)
                                                   "\u2022","\u00B7","((_s))","\u203F", // Pause dot and written out, Ligature                                                     "\u00C0","\u00C1","\u00C2","\u00C3", // Capital As with diacritics
                                                   "\u00C0","\u00C2","\u00C7","\u00C9","\u00C8","\u00CA","\u00CB","\u00CE","\u00CF","\u00D4","\u0152",  // Capital letters + diacritics / ligature
                                                   "\u00E0","\u00E2","\u00E7","\u00E9","\u00E8","\u00EA","\u00EB","\u00EE","\u00EF","\u00F4","\u0153"};  // small letters + diacritics / ligature
                                                    
    public static String[] HIAT_GERMAN_CHAR_SET = {"m\u0301","m\u0300","m\u030c","m\u0302","m\u0304", // m + diacritics
                                                   "\u0301","\u0300","\u030c","\u0302","\u0304", // isolated diacritics
                                                   "\u2190","\u2191","\u2193","\u2192","\u2197","\u2198", // arrows
                                                   "\u2026",  "\u2013", // three dots, n-dash (Gedankenstrich)
                                                   "\u2022","\u00B7","((_s))","\u203F", // Pause dot and written out, Ligature                                                     "\u00C0","\u00C1","\u00C2","\u00C3", // Capital As with diacritics
                                                   "Ä","Ö","Ü","ä","ö","ü","ß"};
                                                   
    public static String[] LATIN_SUPPLEMENT_CHAR_SET = {"\u00C0","\u00C1","\u00C2","\u00C3","\u00C4","\u00C5","\u00C6","\u00C7",
                                                        "\u00C8","\u00C9","\u00CA","\u00CB","\u00CC","\u00CD","\u00CE","\u00CF",
                                                        "\u00D0","\u00D1","\u00D2","\u00D3","\u00D4","\u00D5","\u00D6","\u00D7",
                                                        "\u00D8","\u00D9","\u00DA","\u00DB","\u00DC","\u00DD","\u00DE","\u00DF",
                                                        "\u00E0","\u00E1","\u00E2","\u00E3","\u00E4","\u00E5","\u00E6","\u00E7",
                                                        "\u00E8","\u00E9","\u00EA","\u00EB","\u00EC","\u00ED","\u00EE","\u00EF",
                                                        "\u00F0","\u00F1","\u00F2","\u00F3","\u00F4","\u00F5","\u00F6","\u00F7",
                                                        "\u00F8","\u00F9","\u00FA","\u00FB","\u00FC","\u00FD","\u00FE","\u00FF"};

     public static String[] LATIN_EXTENDEDA1_CHAR_SET = {"\u0100","\u0101","\u0102","\u0103","\u0104","\u0105","\u0106","\u0107",
                                                        "\u0108","\u0109","\u010A","\u010B","\u010C","\u010D","\u010E","\u010F",
                                                        "\u0110","\u0111","\u0112","\u0113","\u0114","\u0115","\u0116","\u0117",
                                                        "\u0118","\u0119","\u011A","\u011B","\u011C","\u011D","\u011E","\u011F",
                                                        "\u0120","\u0121","\u0122","\u0123","\u0124","\u0125","\u0126","\u0127",
                                                        "\u0128","\u0129","\u012A","\u012B","\u012C","\u012D","\u012E","\u012F",
                                                        "\u0130","\u0131","\u0132","\u0133","\u0134","\u0135","\u0136","\u0137",
                                                        "\u0138","\u0139","\u013A","\u013B","\u013C","\u013D","\u013E","\u013F"};
    
    public static String[] LATIN_EXTENDEDA2_CHAR_SET = {"\u0140","\u0141","\u0142","\u0143","\u0144","\u0145","\u0146","\u0147",
                                                        "\u0148","\u0149","\u014A","\u014B","\u014C","\u014D","\u014E","\u014F",
                                                        "\u0150","\u0151","\u0152","\u0153","\u0154","\u0155","\u0156","\u0157",
                                                        "\u0158","\u0159","\u015A","\u015B","\u015C","\u015D","\u015E","\u015F",
                                                        "\u0160","\u0161","\u0162","\u0163","\u0164","\u0165","\u0166","\u0167",
                                                        "\u0168","\u0169","\u016A","\u016B","\u016C","\u016D","\u016E","\u016F",
                                                        "\u0170","\u0171","\u0172","\u0173","\u0174","\u0175","\u0176","\u0177",
                                                        "\u0178","\u0179","\u017A","\u017B","\u017C","\u017D","\u017E","\u017F"};
      

    public static String[] LATIN_EXTENDEDB1_CHAR_SET = {"\u0180","\u0181","\u0182","\u0183","\u0184","\u0185","\u0186","\u0187",
                                                        "\u0188","\u0189","\u018A","\u018B","\u018C","\u018D","\u018E","\u018F",
                                                        "\u0190","\u0191","\u0192","\u0193","\u0194","\u0195","\u0196","\u0197",
                                                        "\u0198","\u0199","\u019A","\u019B","\u019C","\u019D","\u019E","\u019F",
                                                        "\u01A0","\u01A1","\u01A2","\u01A3","\u01A4","\u01A5","\u01A6","\u01A7",
                                                        "\u01A8","\u01A9","\u01AA","\u01AB","\u01AC","\u01AD","\u01AE","\u01AF",
                                                        "\u01B0","\u01B1","\u01B2","\u01B3","\u01B4","\u01B5","\u01B6","\u01B7",
                                                        "\u01B8","\u01B9","\u01BA","\u01BB","\u01BC","\u01BD","\u01BE","\u01BF"};
    
    public static String[] LATIN_EXTENDEDB2_CHAR_SET = {"\u01C0","\u01C1","\u01C2","\u01C3","\u01C4","\u01C5","\u01C6","\u01C7",
                                                        "\u01C8","\u01C9","\u01CA","\u01CB","\u01CC","\u01CD","\u01CE","\u01CF",
                                                        "\u01D0","\u01D1","\u01D2","\u01D3","\u01D4","\u01D5","\u01D6","\u01D7",
                                                        "\u01D8","\u01D9","\u01DA","\u01DB","\u01DC","\u01DD","\u01DE","\u01DF",
                                                        "\u01E0","\u01E1","\u01E2","\u01E3","\u01E4","\u01E5","\u01E6","\u01E7",
                                                        "\u01E8","\u01E9","\u01EA","\u01EB","\u01EC","\u01ED","\u01EE","\u01EF",
                                                        "\u01F0","\u01F1","\u01F2","\u01F3","\u01F4","\u01F5","\u01F6","\u01F7",
                                                        "\u01F8","\u01F9","\u01FA","\u01FB","\u01FC","\u01FD","\u01FE","\u01FF"};
                                                        
    public static String[] IPA_CHAR_SET = {"\u0250","\u0251","\u0252","\u00E6", // a's 4
                                           "\u0253","\u03B2", //b's 6
                                           "\u00E7", //c's 7
                                           "\u0256","\u0257","\u00F0", "\u03B8",//d's 11
                                           "\u0258","\u0259","\u025B","\u025C", //e's 15
                                           "\u0261","\u0262", // g's 17
                                           "\u0268","\u0269","\u026A", // i's 20
                                           "\u026B","\u026C","\u026E", // l's 23
                                           "\u026F","\u0270","\u0271", // m's 26
                                           "\u014B","\u0272","\u0273","\u0274", // n's 30
                                           "\u0276","\u0153","\u00F8","\u0254", //o's 34
                                           "\u0279","\u027A","\u027B","\u027C","\u0280","\u0281", //r's 40
                                           "\u0283","\u0292","\u02A4", "\u02A7", //s's 44
                                           "\u0294","\u0295", // glottal stops 46
                                           "\u028A","\u028B","\u028C","\u028D","\u028F"}; // 51
    
   public static String[] GREEK_CHAR_SET = {"\u0391","\u0392","\u0393","\u0394","\u0395","\u0396","\u0397","\u0398","\u0399",
                                            "\u039A","\u039B","\u039C","\u039D","\u039E","\u039F","\u03A0","\u03A1",
                                            "\u03A3","\u03A4","\u03A5","\u03A6","\u03A7","\u03A8","\u03A9", // end of capital letters
                                            "\u03B1","\u03B2","\u03B3","\u03B4","\u03B5","\u03B6","\u03B7","\u03B8","\u03B9",
                                            "\u03BA","\u03BB","\u03BC","\u03BD","\u03BE","\u03BF","\u03C0","\u03C1","\u03C2",
                                            "\u03C3","\u03C4","\u03C5","\u03C6","\u03C7","\u03C8","\u03C9"}; // end of small letters
   
   
   public static String[] GOTHIC_CHAR_SET = {"\u1030","\u1031","\u1032","\u1033","\u1034","\u1035","\u1036","\u1037",
                                             "\u1038","\u1039","\u103A","\u103B","\u103C","\u103D","\u103E","\u103F",
                                             "\u1040","\u1041","\u1042","\u1043","\u1044","\u1045","\u1046","\u1047",
                                             "\u1048","\u1049","\u104A"};
                                             
   public static String[] CYRILLIC_CHAR_SET = {"\u0410","\u0430","\u0411","\u0431","\u0412","\u0432","\u0413","\u0433",
                                               "\u0414","\u0434","\u0415","\u0435","\u0416","\u0436","\u0417","\u0437",
                                               "\u0418","\u0438","\u0419","\u0439","\u041A","\u043A","\u041B","\u043B",
                                               "\u041C","\u043C","\u041D","\u043D","\u041E","\u043E","\u041F","\u043F",
                                               "\u0420","\u0440","\u0421","\u0441","\u0422","\u0442","\u0423","\u0443",
                                               "\u0424","\u0444","\u0425","\u0445","\u0426","\u0446","\u0427","\u0447",
                                               "\u0428","\u0448","\u0429","\u0449","\u042A","\u044A","\u042B","\u044B",
                                               "\u042C","\u044C","\u042D","\u044D","\u042E","\u044E","\u042F","\u044F"};

   public static String[] GEORGIAN_CHAR_SET = {"\u10A0","\u10A1","\u10A2","\u10A3","\u10A4","\u10A5","\u10A6","\u10A7",
                                               "\u10A8","\u10A9","\u10AA","\u10AB","\u10AC","\u10AD","\u10AE","\u10AF",
                                               "\u10B0","\u10B1","\u10B2","\u10B3","\u10B4","\u10B5","\u10B6","\u10B7",
                                               "\u10B8","\u10B9","\u10BA","\u10BB","\u10BC","\u10BD","\u10BE","\u10BF",
                                               "\u10C0","\u10C1","\u10C2","\u10C3","\u10C4","\u10C5","\u10D0","\u10D1",
                                               "\u10D2","\u10D3","\u10D4","\u10D5","\u10D6","\u10D7","\u10D8","\u10D9",
                                               "\u10DA","\u10DB","\u10DC","\u10DD","\u10DE","\u10DF","\u10E0","\u10E1",
                                               "\u10E2","\u10E3","\u10E4","\u10E5","\u10E6","\u10E7","\u10E8","\u10E9",
                                               "\u10EA","\u10EB","\u10EC","\u10ED","\u10EE","\u10EF","\u10F0","\u10F1",
                                               "\u10F2","\u10F3","\u10F4","\u10F5","\u10F6","\u10F7","\u10F8"};
                                               
   public static String[] ARMENIAN_CHAR_SET = {"\u0531","\u0532","\u0533","\u0534","\u0535","\u0536","\u0537","\u0538",
                                                "\u0539","\u053A","\u053B","\u053C","\u053D","\u053E","\u053F","\u0540",
                                                "\u0541","\u0542","\u0543","\u0544","\u0545","\u0546","\u0547","\u0548",
                                                "\u0549","\u054A","\u054B","\u054C","\u054D","\u054E","\u054F","\u0550",
                                                "\u0551","\u0552","\u0553","\u0554","\u0555","\u0556",
                                                "\u0561","\u0562","\u0563","\u0564","\u0565","\u0566","\u0567","\u0568",
                                                "\u0569","\u056A","\u056B","\u056C","\u056D","\u056E","\u056F","\u0570",
                                                "\u0571","\u0572","\u0573","\u0574","\u0575","\u0576","\u0577","\u0578",
                                                "\u0579","\u057A","\u057B","\u057C","\u057D","\u057E","\u057F","\u0580",
                                                "\u0581","\u0582","\u0583","\u0584","\u0585","\u0586","\u0587"};                                                          

                                                          

    public static String[] COMBINING_DIACRITICAL_MARKS_CHAR_SET = {"\u0300","\u0301","\u0302","\u0303","\u0304","\u0305","\u0306","\u0307","\u0308",
                                                                 "\u0309","\u030A","\u030B","\u030C","\u030D","\u030E","\u030F","\u0310","\u0311",
                                                                 "\u0312","\u0313","\u0314","\u0315","\u0316","\u0317","\u0318","\u0319","\u031A",
                                                                 "\u031B","\u031C","\u031D","\u031E","\u031F","\u0320","\u0321","\u0322","\u0323",
                                                                 "\u0324","\u0325","\u0326","\u0327","\u0328","\u0329","\u032A","\u032B","\u032C",
                                                                 "\u032D","\u032E","\u032F","\u0330","\u0331","\u0332","\u0333","\u0334","\u0335",
                                                                 "\u0336","\u0337","\u0338","\u0339","\u033A","\u033B","\u033C","\u033D","\u033E",
                                                                 "\u033F"}; 

    public static String[] RUNIC_CHAR_SET = {"\u16A0","\u16A1","\u16A2","\u16A3","\u16A4","\u16A5","\u16A6","\u16A7","\u16A8","\u16A9","\u16AA",
                                             "\u16AB","\u16AC","\u16AD","\u16AE","\u16AF","\u16B0","\u16B1","\u16B2","\u16B3","\u16B4","\u16B5",
                                             "\u16B6","\u16B7","\u16B8","\u16B9","\u16BA","\u16BB","\u16BC","\u16BD","\u16BE","\u16BF","\u16C0",
                                             "\u16C1","\u16C2","\u16C3","\u16C4","\u16C5","\u16C6","\u16C7","\u16C8","\u16C9","\u16CA","\u16CB",
                                             "\u16CC","\u16CD","\u16CE","\u16CF","\u16D0","\u16D1","\u16D2","\u16D3","\u16D4","\u16D5","\u16D6",
                                             "\u16D7","\u16D8","\u16D9","\u16DA","\u16DB","\u16DC","\u16DD","\u16DE","\u16DF","\u16E0","\u16E1",
                                             "\u16E2","\u16E3","\u16E4","\u16E5","\u16E6","\u16E7","\u16E8","\u16E9","\u16EA","\u16EB","\u16EC",
                                             "\u16ED","\u16EE","\u16EF","\u16F0"};
                                             
    public static String[] BOPOMOFO_CHAR_SET = {"?","?","?","?","?","?","?","?","?","?","?"};
    /** Creates new CharacterSet */

    public CharacterSet() {
    }

}
