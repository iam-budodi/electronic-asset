/**
 * 
 */
package com.assets.management.assets.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

/**
 * @author Japhet
 *
 */
public class PurchaseOrder extends PanacheEntity {
	public String poDate;
	public String poQty;
	public String poStatus;
	public Supplier supplier;
}
