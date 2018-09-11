package de.tr7zw.gw2racing.region;

import java.util.HashMap;

public class Region {

	public int                         id;
	public String                      name;
	public java.util.Map<Integer, Map> maps = new HashMap<>();

	@Override
	public String toString() {
		return "Region{" +
				"id=" + id +
				", name='" + name + '\'' +
				", maps=" + maps +
				'}';
	}
}
