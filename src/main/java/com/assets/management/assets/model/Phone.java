package com.assets.management.assets.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

//@Entity 
public class Phone extends Asset {
 
	@Column(name = "topup_amount")
	public BigDecimal topupAmout;
 
}
