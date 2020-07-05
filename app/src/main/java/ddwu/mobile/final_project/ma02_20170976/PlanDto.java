package ddwu.mobile.final_project.ma02_20170976;

import java.io.Serializable;

public class PlanDto implements Serializable {

	private long id;
	private String content;
	private String date;
	private String place;
	private String attendance;
	private String photoPath;
	private String hour;
	private String minute;
	private String second;
	private String alarm;
	private String repeat;
	private String interval;

	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPhotoPath() {
		return photoPath;
	}
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() { return date; }
	public void setDate(String date) { this.date = date; }
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) { this.place = place; }
	public String getAttendance() { return attendance; }
	public void setAttendance(String attendance) { this.attendance = attendance; }



	@Override
	public String toString() {
		return id + ". " + date + " - " + place + "에서 " + content  + " (with)" + attendance;
	}
	
}
