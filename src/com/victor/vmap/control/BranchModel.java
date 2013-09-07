package com.victor.vmap.control;

public class BranchModel {
	private String uid= "";
	private String addr= "";
	private String name = "";
	private String province= "";
	private String city = "";
	private String district = "";
	private String create_time = "";
	private String modify_time = "";

	private String distance= "";
	private String imageurl= "";
	private String webUrl= "";
	private String latitude= "";
	private String longitude= "";
    private String geotable_id= "";
    private int branch_type= 0;
	/**
	 * @return the branch_type
	 **/
	public int getBranch_type() {
		return branch_type;
	}

	/**
	 * @param branch_type the branch_type to set
	 **/
	public void setBranch_type(int branch_type) {
		this.branch_type = branch_type;
	}

	/**
	 * @return the modify_time
	 **/
	public String getModify_time() {
		return modify_time;
	}

	/**
	 * @param modify_time the modify_time to set
	 **/
	public void setModify_time(String modify_time) {
		this.modify_time = modify_time;
	}

	/**
	 * @return the uid
	 **/
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 **/
	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * @return the geotable_id
	 **/
	public String getGeotable_id() {
		return geotable_id;
	}

	/**
	 * @param geotable_id the geotable_id to set
	 **/
	public void setGeotable_id(String geotable_id) {
		this.geotable_id = geotable_id;
	}

	/**
	 * @return the province
	 **/
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 **/
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the city
	 **/
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 **/
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the district
	 **/
	public String getDistrict() {
		return district;
	}

	/**
	 * @param district the district to set
	 **/
	public void setDistrict(String district) {
		this.district = district;
	}

	/**
	 * @return the create_time
	 **/
	public String getCreate_time() {
		return create_time;
	}

	/**
	 * @param create_time the create_time to set
	 **/
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

}