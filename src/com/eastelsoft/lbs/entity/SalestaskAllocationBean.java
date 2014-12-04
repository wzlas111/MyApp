package com.eastelsoft.lbs.entity;

import com.eastelsoft.lbs.R;

/**
 * 
 * 
 * @author admin
 * 
 */
public class SalestaskAllocationBean {
//	db.execSQL("create table if not exists l_salesallocation("
//	+ "id NTEXT," 
//	+ "clientid NTEXT,"
//	+ "clientName NTEXT,"
//	+ "date NTEXT," 
//	+ "goods_id NTEXT," 
//	+ "istijiao NTEXT)");
	private String id;
	private String clientid;
	private String clientName;
	private String date;

	private String goods_id;

	private String istijiao;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}



	public String getIstijiao() {
		return istijiao;
	}

	public void setIstijiao(String istijiao) {
		this.istijiao = istijiao;
	}

	

	@Override
	public String toString() {
		return "id=" + id + ", clientName=" + clientName+ 
				", date=" + date +
				 ", goods_id=" + goods_id +
				
				", istijiao=" + istijiao + "]";
	}

	

}
