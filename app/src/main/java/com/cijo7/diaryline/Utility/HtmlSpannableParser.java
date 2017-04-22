package com.cijo7.diaryline.Utility;

import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;


import com.cijo7.diaryline.data.AppConstants;
import com.cijo7.diaryline.ui.HeaderSpan;
import com.cijo7.diaryline.ui.QuoteSpanModern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import timber.log.Timber;

/**
 * Created by cijo-saju on 23/1/16.
 *
 */
public class HtmlSpannableParser {


    /**
     * Returns an HTML representation of the provided Spanned text. A best effort is
     * made to add HTML tags corresponding to spans. Also note that HTML meta characters
     * (such as "&lt;" and "&amp;") within the input text are escaped.
     *
     * @param text input text to convert
     * @return string containing input converted to HTML
     */
    public static String toHtml(Spanned text) {
        StringBuilder out = new StringBuilder();
        withinHtml(out, text);
        return out.toString();
    }

    public static Spanned toSpannable(String string){
        SpannableStringBuilder builder=new SpannableStringBuilder();
        string = string.replace("\n","");
        int start=-1,end;
        int color= AppConstants.quoteColor;
        try {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser parser=factory.newPullParser();
            parser.setInput(new StringReader(string));
            int event=parser.getEventType();
            boolean skip = false, pre=false;
            while (event!=XmlPullParser.END_DOCUMENT){
                switch (event){
                    case XmlPullParser.START_TAG:
                        if (parser.isEmptyElementTag()) {
                            builder.append(preTag(parser.getName()));
                            skip = true;
                            break;
                        }
                        start=builder.length();
                        if (pre)
                            builder.append(preTag(parser.getName()));
                        pre = false;

                        try{
                            if(parser.getName().equals("blockquote")){
                                color = Integer.parseInt(parser.getAttributeValue(0));
                            }
                        } catch (IndexOutOfBoundsException | NumberFormatException e){
                            Timber.d(e,"Unable to retrieve attribute.");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        builder.append(parser.getText());
                        break;
                    case XmlPullParser.END_TAG:
                        if (skip) {
                            skip = false;
                            break;
                        }
                        end=builder.length();
                        if(start!=end && spansAvailable(parser.getName())){
                            builder.setSpan(getType(parser.getName(),color),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        pre = parser.getName().equals("p");
                        break;
                }
                try {
                    event=parser.next();
                } catch (IOException e) {
                    Timber.d(e,"Unable to get next event");
                }
            }
        } catch (XmlPullParserException e) {
            Timber.d(e, "Failed to parse");
        }
        return builder;
    }

    private static String preTag(String name) {
        switch (name) {
            case "p":
            case "blockquote":
                return "\n\n";
            case "br":
                return "\n";
            default:
                return "";
        }
    }

    private static boolean spansAvailable(String name) {
        return name.equals("b") ||
                name.equals("i") ||
                name.equals("h1") ||
                name.equals("blockquote");
    }

    private static Object getType(String tag,int attr){
        switch (tag){
            case "b":
                return new StyleSpan(Typeface.BOLD);
            case "i":
                return new StyleSpan(Typeface.ITALIC);
            case "h1":
                return new HeaderSpan();
            case "blockquote":
                return new QuoteSpanModern(attr);
            default:
                return null;
        }
    }



    private static void withinHtml(StringBuilder out, Spanned text) {
        int len = text.length();

        int next;
        for (int i = 0; i < text.length(); i = next) {
            next = text.nextSpanTransition(i, len, ParagraphStyle.class);
            ParagraphStyle[] style = text.getSpans(i, next, ParagraphStyle.class);
            String elements = " ";
            boolean needDiv = false;

            for (ParagraphStyle aStyle : style) {
                if (aStyle instanceof AlignmentSpan) {
                    Layout.Alignment align =
                            ((AlignmentSpan) aStyle).getAlignment();
                    needDiv = true;
                    if (align == Layout.Alignment.ALIGN_CENTER) {
                        elements = "align=\"center\" " + elements;
                    } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                        elements = "align=\"right\" " + elements;
                    } else {
                        elements = "align=\"left\" " + elements;
                    }
                }
            }
            if (needDiv) {
                out.append("<div ").append(elements).append(">");
            }

            withinDiv(out, text, i, next);

            if (needDiv) {
                out.append("</div>");
            }
        }
    }

    private static void withinDiv(StringBuilder out, Spanned text,
                                  int start, int end) {
        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, QuoteSpanModern.class);
            QuoteSpanModern[] quotes = text.getSpans(i, next, QuoteSpanModern.class);

            for (QuoteSpanModern quote : quotes) {
                out.append("<blockquote ");
                out.append("color='").append(quote.getColor()).append("'>");
            }

            withinBlockQuote(out, text, i, next);

            for (@SuppressWarnings("unused")
                    QuoteSpanModern quote : quotes) {
                out.append("</blockquote>");
            }
        }
    }

    @SuppressWarnings("unused")
    private static String getOpenParaTagWithDirection(Spanned text, int start, int end) {

     /*   final int len = end - start;
        final byte[] levels = ArrayUtils.newUnpaddedByteArray(len);
        final char[] buffer = TextUtils.obtain(len);
        TextUtils.getChars(text, start, end, buffer, 0);

        final int DIR_REQUEST_DEFAULT_LTR = 2; // Layout.DIR_REQUEST_DEFAULT_LTR;
        final int DIR_REQUEST_DEFAULT_RTL = -2; // Layout.DIR_REQUEST_DEFAULT_RTL;
        final int DIR_REQUEST_LTR = 1; // Layout.DIR_REQUEST_LTR;
        final int DIR_REQUEST_RTL = -1; // Layout.DIR_REQUEST_RTL;

        int paraDir = AndroidBidi.bidi(DIR_REQUEST_DEFAULT_LTR, buffer, levels, len,false);*/

        // FIXME: 23/1/16 Unidirectional text is only supported.
        switch(Layout.DIR_LEFT_TO_RIGHT) {
            case Layout.DIR_RIGHT_TO_LEFT:
                return "<p dir=\"rtl\">";
            case Layout.DIR_LEFT_TO_RIGHT:
            default:
                return "<p dir=\"ltr\">";
        }
    }

    private static void withinBlockQuote(StringBuilder out, Spanned text,
                                         int start, int end) {
        out.append(getOpenParaTagWithDirection(text, start, end));

        int next;
        for (int i = start; i < end; i = next) {
            next = TextUtils.indexOf(text, '\n', i, end);
            if (next < 0) {
                next = end;
            }

            int nl = 0;

            while (next < end && text.charAt(next) == '\n') {
                nl++;
                next++;
            }

            if (withinParagraph(out, text, i, next - nl, nl, next == end)) {
                /* Paragraph should be closed */
                out.append("</p>");
                out.append(getOpenParaTagWithDirection(text, next, end));
            }
        }

        out.append("</p>");
    }

    /* Returns true if the caller should close and reopen the paragraph. */
    private static boolean withinParagraph(StringBuilder out, Spanned text,
                                           int start, int end, int nl,
                                           boolean last) {
        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, CharacterStyle.class);
            CharacterStyle[] style = text.getSpans(i, next,
                    CharacterStyle.class);

            for (CharacterStyle aStyle : style) {
                if(aStyle instanceof HeaderSpan){
                    out.append("<h").append(((HeaderSpan) aStyle).getLevel())
                            .append(">");
                }
                if (aStyle instanceof StyleSpan) {
                    int s = ((StyleSpan) aStyle).getStyle();

                    if ((s & Typeface.BOLD) != 0) {
                        out.append("<b>");
                    }
                    if ((s & Typeface.ITALIC) != 0) {
                        out.append("<i>");
                    }
                }
                if (aStyle instanceof TypefaceSpan) {
                    String s = ((TypefaceSpan) aStyle).getFamily();

                    if ("monospace".equals(s)) {
                        out.append("<tt>");
                    }
                }
                if (aStyle instanceof SuperscriptSpan) {
                    out.append("<sup>");
                }
                if (aStyle instanceof SubscriptSpan) {
                    out.append("<sub>");
                }
                if (aStyle instanceof UnderlineSpan) {
                    out.append("<u>");
                }
                if (aStyle instanceof StrikethroughSpan) {
                    out.append("<strike>");
                }
                if (aStyle instanceof URLSpan) {
                    out.append("<a href=\"");
                    out.append(((URLSpan) aStyle).getURL());
                    out.append("\">");
                }
                if (aStyle instanceof ImageSpan) {
                    out.append("<img src=\"");
                    out.append(((ImageSpan) aStyle).getSource());
                    out.append("\">");

                    // Don't output the dummy character underlying the image.
                    i = next;
                }
                if (aStyle instanceof AbsoluteSizeSpan) {
                    out.append("<font size =\"");
                    out.append(((AbsoluteSizeSpan) aStyle).getSize() / 6);
                    out.append("\">");
                }
                if (aStyle instanceof ForegroundColorSpan) {
                    out.append("<font color =\"#");
                    String color = Integer.toHexString(((ForegroundColorSpan)
                            aStyle).getForegroundColor() + 0x01000000);
                    while (color.length() < 6) {
                        color = "0" + color;
                    }
                    out.append(color);
                    out.append("\">");
                }
            }

            withinStyle(out, text, i, next);

            for (int j = style.length - 1; j >= 0; j--) {

                if (style[j] instanceof ForegroundColorSpan) {
                    out.append("</font>");
                }
                if (style[j] instanceof AbsoluteSizeSpan) {
                    out.append("</font>");
                }
                if (style[j] instanceof URLSpan) {
                    out.append("</a>");
                }
                if (style[j] instanceof StrikethroughSpan) {
                    out.append("</strike>");
                }
                if (style[j] instanceof UnderlineSpan) {
                    out.append("</u>");
                }
                if (style[j] instanceof SubscriptSpan) {
                    out.append("</sub>");
                }
                if (style[j] instanceof SuperscriptSpan) {
                    out.append("</sup>");
                }
                if (style[j] instanceof TypefaceSpan) {
                    String s = ((TypefaceSpan) style[j]).getFamily();

                    if (s.equals("monospace")) {
                        out.append("</tt>");
                    }
                }
                if (style[j] instanceof StyleSpan) {
                    int s = ((StyleSpan) style[j]).getStyle();

                    if ((s & Typeface.BOLD) != 0) {
                        out.append("</b>");
                    }
                    if ((s & Typeface.ITALIC) != 0) {
                        out.append("</i>");
                    }
                }
                if(style[j] instanceof HeaderSpan){
                    out.append("</h").append(((HeaderSpan) style[j]).getLevel())
                            .append(">");
                }
            }
        }

        if (nl == 1) {
            out.append("<br/>");
            return false;
        } else {
            for (int i = 2; i < nl; i++) {
                out.append("<br/>");
            }
            return !last;
        }
    }

    private static void withinStyle(StringBuilder out, CharSequence text,
                                    int start, int end) {
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);

            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c >= 0xD800 && c <= 0xDFFF) {
                if (c < 0xDC00 && i + 1 < end) {
                    char d = text.charAt(i + 1);
                    if (d >= 0xDC00 && d <= 0xDFFF) {
                        i++;
                        int codePoint = 0x010000 | (int) c - 0xD800 << 10 | (int) d - 0xDC00;
                        out.append("&#").append(codePoint).append(";");
                    }
                }
            } else if (c > 0x7E || c < ' ') {
                out.append("&#").append((int) c).append(";");
            } else if (c == ' ') {
                while (i + 1 < end && text.charAt(i + 1) == ' ') {
                    out.append("&nbsp;");
                    i++;
                }

                out.append(' ');
            } else {
                out.append(c);
            }
        }
    }
   /* private class AndroidBidi {

        public static int bidi(int dir, char[] chs, byte[] chInfo, int n, boolean haveInfo) {
            if (chs == null || chInfo == null) {
                throw new NullPointerException();
            }

            if (n < 0 || chs.length < n || chInfo.length < n) {
                throw new IndexOutOfBoundsException();
            }

            switch(dir) {
                case Layout.DIR_REQUEST_LTR: dir = 0; break;
                case Layout.DIR_REQUEST_RTL: dir = 1; break;
                case Layout.DIR_REQUEST_DEFAULT_LTR: dir = -2; break;
                case Layout.DIR_REQUEST_DEFAULT_RTL: dir = -1; break;
                default: dir = 0; break;
            }

            int result = runBidi(dir, chs, chInfo, n, haveInfo);
            result = (result & 0x1) == 0 ? Layout.DIR_LEFT_TO_RIGHT : Layout.DIR_RIGHT_TO_LEFT;
            return result;
        }

        private native static int runBidi(int dir, char[] chs, byte[] chInfo, int n, boolean haveInfo);
    }*/
}

