package com.zzh.dao.impl;

import java.util.Map;

import com.zzh.dao.entity.annotation.*;

@Table("mo_host")
public class Host {

	static Host make(String name) {
		Host host = new Host();
		host.name = name;
		return host;
	}

	@Column
	@Id
	public int id;

	@Column
	@Name
	public String name;

	@Many(target = Other.class, field = "hostId")
	public Other[] others;

	@Column("tid")
	public int typeId;

	@One(target = HostType.class, field = "typeId")
	public HostType type;

	@Many(target = Other.class, field = "hostId")
	public Other oneOther;

	@ManyMany(target = Address.class, relation = "mo_host_address", from = "host_name", to = "address_ip")
	public Address[] adrs;

	@ManyMany(target = Port.class, relation = "mo_host_port", from = "host_id", to = "port_id")
	public Port[] ports;

	@ManyMany(target = Address.class, relation = "mo_host_main_address", from = "host_name", to = "address_ip")
	public Address mainAddress;

	@ManyMany(target = Port.class, relation = "mo_host_main_port", from = "host_id", to = "port_id")
	public Port mainPort;

	@Many(target = Other.class, field = "hostId", mapKeyField = "text")
	public Map<String, Other> mapOther;

	@ManyMany(target = Address.class, relation = "mo_host_address", from = "host_name", to = "address_ip", mapKeyField = "ip")
	public Map<String, Address> mapAddress;

}
