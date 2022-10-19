package com.assets.management.assets.model;

import javax.persistence.Column;

public class Computer extends Asset {
	
	@Column(name = "computer_name", length = 100)
	public String computerName;
	
	@Column(name = "asset_tag", length = 100)
	public String assetTag;
	
}
