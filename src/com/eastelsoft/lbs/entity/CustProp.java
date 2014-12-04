package com.eastelsoft.lbs.entity;

import com.google.gson.annotations.Expose;

public class CustProp {
	@Expose
	private String id;
	@Expose
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
	public boolean equals(Object obj){
		CustProp p = (CustProp)obj;
        if (this.id == (p.getId())) {
            return true;
        } else {
            return false;
        }
    }
}
