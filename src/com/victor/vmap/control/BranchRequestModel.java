/** 
 * @Filename BranchRequestModel.java 
 * @Description TODO 
 * @Version 1.0
 * @Author xiaoyl
 * @Creation 2013-9-2 上午11:27:00   
 * @Copyright Copyright © 2009 - 2013 Victor.All Rights Reserved.
 **/
package com.victor.vmap.control;

/**
 * @ClassName BranchRequestModel
 * @Description TODO
 * @Version 1.0
 * @Creation 2013-9-2 上午11:27:00
 * @Mender xiaoyl
 * @Modification 2013-9-2 上午11:27:00
 **/
public class BranchRequestModel {
	private String request_url = "http://api.map.baidu.com/geosearch/v2/local?q=&region=长沙市&ak="
			+ "81f8a6039d6819798e583732a8004b79&geotable_id=31669";

	private String geotable_id = "";
	private String region = "长沙市";

	public static final String ak = "81f8a6039d6819798e583732a8004b79";


	public BranchRequestModel(String geotable_id) {
		request_url = "q=&region=长沙市&ak=81f8a6039d6819798e583732a8004b79&geotable_id="+geotable_id;
	}
	public BranchRequestModel() {

	}
	/**
	 * @return the request_url
	 **/
	public String getRequest_url() {
		return request_url;
	}

	/**
	 * @param request_url
	 *            the request_url to set
	 **/
	public void setRequest_url(String request_url) {
		this.request_url = request_url;
	}

	/**
	 * @return the geotable_id
	 **/
	public String getGeotable_id() {
		return geotable_id;
	}

	/**
	 * @param geotable_id
	 *            the geotable_id to set
	 **/
	public void setGeotable_id(String geotable_id) {
		this.geotable_id = geotable_id;
	}

	/**
	 * @return the region
	 **/
	public String getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 **/
	public void setRegion(String region) {
		this.region = region;
	}

}
