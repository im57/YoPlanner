package ddwu.mobile.final_project.ma02_20170976.plan;

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

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}


	@Override
	public String toString() {
		return id + ". " + date + " - " + place + "에서 " + content  + " (with)" + attendance;
	}
	
}
