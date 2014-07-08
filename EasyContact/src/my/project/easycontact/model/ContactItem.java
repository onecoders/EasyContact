package my.project.easycontact.model;

public class ContactItem {

	private long _id;
	private String name;
	private String number;
	private String alpha;
	private long photoid;

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}

	public long getPhotoid() {
		return photoid;
	}

	public void setPhotoid(long photoid) {
		this.photoid = photoid;
	}

}
