package ddwu.mobile.final_project.ma02_20170976.bookmark;

import java.io.Serializable;

public class BookmarkDto implements Serializable {

	private long id;
	private String name;
	private String address;

	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
