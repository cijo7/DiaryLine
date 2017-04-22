package com.cijo7.diaryline.Utility;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cijo-saju on 29/1/16.
 * The data parser class responsible for extracting and packing data.
 */
public class DataParser {
    private StringBuilder stringBuilder;
    private List<String> stringList;

    /**
     * <p>
     *     Packs a list of items into corresponding XML text. This function is exclusively
     *     intended to be used by a <i>List</i> as it's used for pacing individual tasks without
     *     retaining any formatting.
     * </p>
     * @param strings List of items to be packed.
     * @return Encoded XML string
     */
    public String listToText(List<String> strings){
        stringBuilder=new StringBuilder();
        stringList=strings;
        stringBuilder.append("<?xml version=\"1.0\"?>")
                .append("<list>");
        insertItem();
        stringBuilder.append("</list>");
        return stringBuilder.toString();
    }

    private void insertItem(){
        for (String str:stringList ) {
            stringBuilder.append("<item checked=\"true\">")
                    .append(str)
                    .append("</item>");
        }


    }

    /**
     * A simple XML parser that will accept the item xml and return the contents as an array
     * @param string The XML String
     * @return List containing individual items. Null on failure.
     */
    public List<String> textToList(String string){
        stringList=new ArrayList<>();
        boolean item=false;

        try {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser parser=factory.newPullParser();
            parser.setInput(new StringReader(string));
            int event=parser.getEventType();
            while (event!=XmlPullParser.END_DOCUMENT){
                switch (event){
                    case XmlPullParser.START_TAG:

                        if(parser.getName().equals("item"))
                            item=true;
                        break;
                    case XmlPullParser.TEXT:
                        if(item)
                            stringList.add(parser.getText());
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("item"))
                            item=false;
                        break;
                }
                event=parser.next();
            }
            return stringList;
        } catch (XmlPullParserException e) {
            Timber.d(e,"Failed to parse string:"+string);
        } catch (IOException e) {
            Timber.d(e,"Failed to loop through contents.");
        }
        return null;
    }

}
