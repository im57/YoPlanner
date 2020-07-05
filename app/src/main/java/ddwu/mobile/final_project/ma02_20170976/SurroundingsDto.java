package ddwu.mobile.final_project.ma02_20170976;

import java.io.Serializable;
import java.util.ArrayList;

public class SurroundingsDto implements Serializable {

    private int _id;
    private String placeID; //장소id
    private String name; //장소이름
    private String vicinity; //주소
    private String phone; //번호
    private String open_now; //현재 영업 여부
    private String weekday_text = null;//요일별 영업시간
    private ArrayList<ReviewDTO> reviewDTO; //리뷰


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpen_now() {
        return open_now;
    }

    public void setOpen_now(String open_now) {
        this.open_now = open_now;
    }

    public String getWeekday_text() {
        return weekday_text;
    }

    public void setWeekday_text(String weekday_text) {
        if(getWeekday_text() == null)
            this.weekday_text = "  " + weekday_text;
        else
            this.weekday_text = getWeekday_text() + "\r\n  " + weekday_text;
    }

    public ArrayList<ReviewDTO> getReviewDTO() {
        return reviewDTO;
    }

    public void setReviewDTO(ArrayList<ReviewDTO> reviewDTO) {
        this.reviewDTO = reviewDTO;
    }






    public static class ReviewDTO{
        private int _id;
        private  String rname; //작성자
        private String time; //작성시간
        private String text; //작성내용

        public int get_id() {
            return _id;
        }

        public void set_id(int _id) {
            this._id = _id;
        }

        public String getRName() {
            return rname;
        }

        public void setRName(String rname) {
            this.rname = rname;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
