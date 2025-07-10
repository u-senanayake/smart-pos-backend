package lk.udcreations.sale.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
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

}
