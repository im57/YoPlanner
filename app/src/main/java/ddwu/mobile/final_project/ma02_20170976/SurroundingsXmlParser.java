package ddwu.mobile.final_project.ma02_20170976;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

//검색한 위치 주변의 카페 파싱
public class SurroundingsXmlParser {
    public enum TagType { NONE, NAME, VICINITY, OPEN_NOW, PLACE_ID }

    public SurroundingsXmlParser() { }

    public ArrayList<SurroundingsDto> parse(String xml) {
        ArrayList<SurroundingsDto> resultList = new ArrayList();
        SurroundingsDto dto = null;
        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("result")) {
                            Log.d("a", "result");
                            dto = new SurroundingsDto();
                        } else if (parser.getName().equals("name")) {
                            if (dto != null) tagType = TagType.NAME;
                        } else if (parser.getName().equals("vicinity")) {
                            if (dto != null) tagType = TagType.VICINITY;
                        } else if (parser.getName().equals("open_now")) {
                            if (dto != null) tagType = TagType.OPEN_NOW;
                        } else if (parser.getName().equals("place_id")) {
                            if (dto != null) tagType = TagType.PLACE_ID;
                        } break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("result")) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case NAME:
                                dto.setName(parser.getText());
                                break;
                            case VICINITY:
                                dto.setVicinity(parser.getText());
                                break;
                            case OPEN_NOW:
                                if(parser.getText().equals("true"))
                                    dto.setOpen_now("open");
                                else
                                    dto.setOpen_now("close");
                                break;
                            case PLACE_ID:
                                dto.setPlaceID(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return resultList;
    }


}
