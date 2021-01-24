package com.demo._5subscribe;

/**
 * 记录WordServer的基本信息
 * 
 * @author jerome
 */
public class ServerData {

	private Integer id;
	private String name;
	private String address;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	@Override
	public String toString() {
		return "ServerData [id=" + id + ", name=" + name + ", address=" + address + "]";
	}

}
