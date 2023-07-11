package mobile.exam.network.sample.openapi_with_parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class NaverBookXmlParser {

    //xml에서 읽어들일 태그를 구분한 enum -> 정수값 등으로 구분하지 않고 가독성 높은 방식을 사용
    private enum TagType{ NONE, TITLE, AUTHOR, LINK, IMAGE};

    //parsing 대상인 tag를 상수로 선언
 //   private final static String FAULT_RESULT = "failtResult";
    private final static String ITEM_TAG = "item";
    private final static String TITLE_TAG = "title";
    private final static String AUTHOR_TAG = "author";
    private final static String LINK_TAG = "link";
    private final static String IMAGE_TAG = "image";

    private XmlPullParser parser;

    public NaverBookXmlParser() {
        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<NaverBookDto> parse(String xml) {
        ArrayList<NaverBookDto> resultList = new ArrayList<>();
        NaverBookDto dbo = null;
        TagType tagType = TagType.NONE;

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();


                        if (tag.equals(ITEM_TAG)) {
                            dbo = new NaverBookDto();
                        }
                        else if (tag.equals(TITLE_TAG)) {
                            if(dbo!=null){
                                tagType = TagType.TITLE;
                            }
                        }
                        else if (tag.equals(AUTHOR_TAG)) {
                            tagType = TagType.AUTHOR;
                        }
                        else if (tag.equals(LINK_TAG)) {
                            if(dbo!=null){
                                tagType = TagType.LINK;
                            }
                        }
                        else if (tag.equals(IMAGE_TAG)) {
                            tagType = TagType.IMAGE;
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(ITEM_TAG)) {
                            resultList.add(dbo);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case TITLE:
                                dbo.setTitle(parser.getText());
                                break;
                            case AUTHOR:
                                dbo.setAuthor(parser.getText());
                                break;
                            case LINK:
                                dbo.setLink(parser.getText());
                                break;
                            case IMAGE:
                                dbo.setImageLink(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;

                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultList;
    }
}