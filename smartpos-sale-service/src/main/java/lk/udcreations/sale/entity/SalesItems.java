package lk.udcreations.sale.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "salesItems")
public class SalesItems {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sales_item_id")
	private Integer salesItemId;

	@Column(name = "sale_id")
	private Integer saleId;

	@Column(name = "product_id")
	private Integer productId;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "returned_quantity", nullable = false)
	private int returnedQuantity;

	@Column(name = "price_per_unit", nullable = false)
	private BigDecimal pricePerUnit;

	@Column(name = "item_discount_val", nullable = true)
	private BigDecimal itemDiscountVal;

	@Column(name = "item_discount_per", nullable = true)
	private int itemDiscountPer;

	@Column(name = "total_price")
	private BigDecimal totalPrice;


	public Integer getSalesItemId() {
		return salesItemId;
	}

	public void setSalesItemId(Integer salesItemId) {
		this.salesItemId = salesItemId;
	}

	public Integer getSaleId() {
		return saleId;
	}

	public void setSaleId(Integer saleId) {
		this.saleId = saleId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public BigDecimal getItemDiscountVal() {
		return itemDiscountVal;
	}

	public void setItemDiscountVal(BigDecimal itemDiscountVal) {
		this.itemDiscountVal = itemDiscountVal;
	}

	public int getItemDiscountPer() {
		return itemDiscountPer;
	}

	public void setItemDiscountPer(int itemDiscountPer) {
		this.itemDiscountPer = itemDiscountPer;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getReturnedQuantity() {
		return returnedQuantity;
	}

	public void setReturnedQuantity(int returnedQuantity) {
		this.returnedQuantity = returnedQuantity;
	}

}
