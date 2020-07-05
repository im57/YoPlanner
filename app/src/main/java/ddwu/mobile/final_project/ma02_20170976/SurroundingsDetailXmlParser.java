package ddwu.mobile.final_project.ma02_20170976;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

//상세 카페정보 파싱
public class SurroundingsDetailXmlParser {

    public enum TagType { NONE, NAME, VICINITY, OPEN_NOW, FORMATTED_PHONE_NUMBER, WEEKDAY_TEXT, AUTHOR_NAME, TIME, TEXT }

    public SurroundingsDetailXmlParser() { }

    public SurroundingsDto parse(String xml, SurroundingsDto dto) {
        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            ArrayList<SurroundingsDto.ReviewDTO> reviewList = new ArrayList();
            SurroundingsDto.ReviewDTO rDto = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("review")) {
                            rDto = new SurroundingsDto.ReviewDTO();
                        }else if (parser.getName().equals("name")) {
                            if (dto != null) tagType = TagType.NAME;
                        }else if (parser.getName().equals("vicinity")) {
                            if (dto != null) tagType = TagType.VICINITY;
                        }else if (parser.getName().equals("open_now")) {
                            if (dto != null) tagType = TagType.OPEN_NOW;
                        }else if (parser.getName().equals("formatted_phone_number")) {
                            if (dto != null) tagType = TagType.FORMATTED_PHONE_NUMBER;
                        }else if (parser.getName().equals("weekday_text")) {
                            if (dto != null) tagType = TagType.WEEKDAY_TEXT;
                        }else if (parser.getName().equals("author_name")) {
                            if (dto != null) tagType = TagType.AUTHOR_NAME;
                        }else if (parser.getName().equals("relative_time_description")) {
                            if (dto != null) tagType = TagType.TIME;
                        }else if (parser.getName().equals("text")) {
                            if (dto != null) tagType = TagType.TEXT;
                        } break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("result")) {
                            dto.setReviewDTO(reviewList);
                        } else if (parser.getName().equals("review")) {
                            reviewList.add(rDto);
                            Log.d("review", rDto.getText());
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
                            case FORMATTED_PHONE_NUMBER:
                                dto.setPhone(parser.getText());
                                break;
                            case WEEKDAY_TEXT:
                                dto.setWeekday_text(parser.getText());
                                break;
                            case AUTHOR_NAME:
                                rDto.setRName(parser.getText());
                                break;
                            case TIME:
                                rDto.setTime(parser.getText());
                                break;
                            case TEXT:
                                rDto.setText(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return dto;
    }
}
