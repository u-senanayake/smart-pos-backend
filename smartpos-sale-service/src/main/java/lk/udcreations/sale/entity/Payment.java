package lk.udcreations.sale.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Integer paymentId;

	@Column(name = "sale_id")
	private Integer saleId;

	@Column(name = "cash_amount")
	private BigDecimal cashAmount;

	@Column(name = "ccard_amount")
	private BigDecimal creditCardAmount;

	@Column(name = "ccard_ref", length = 100)
	private String creditCardReference;

	@Column(name = "qr_amount")
	private BigDecimal qrAmount;

	@Column(name = "qr_ref", length = 100)
	private String qrReference;

	@Column(name = "cheque_amount")
	private BigDecimal chequeAmount;

	@Column(name = "cheque_ref", length = 100)
	private String chequeReference;

	@Column(name = "due_amount")
	private BigDecimal dueAmount;

	public BigDecimal getCashAmount() {
		return cashAmount;
	}

	public Integer getSaleId() {
		return saleId;
	}

	public void setSaleId(Integer saleId) {
		this.saleId = saleId;
	}

	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}

	public BigDecimal getCreditCardAmount() {
		return creditCardAmount;
	}

	public void setCreditCardAmount(BigDecimal creditCardAmount) {
		this.creditCardAmount = creditCardAmount;
	}

	public String getCreditCardReference() {
		return creditCardReference;
	}

	public void setCreditCardReference(String creditCardReference) {
		this.creditCardReference = creditCardReference;
	}

	public BigDecimal getQrAmount() {
		return qrAmount;
	}

	public void setQrAmount(BigDecimal qrAmount) {
		this.qrAmount = qrAmount;
	}

	public String getQrReference() {
		return qrReference;
	}

	public void setQrReference(String qrReference) {
		this.qrReference = qrReference;
	}

	public BigDecimal getChequeAmount() {
		return chequeAmount;
	}

	public void setChequeAmount(BigDecimal chequeAmount) {
		this.chequeAmount = chequeAmount;
	}

	public String getChequeReference() {
		return chequeReference;
	}

	public void setChequeReference(String chequeReference) {
		this.chequeReference = chequeReference;
	}

	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

}
