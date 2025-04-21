package lk.udcreations.sale.util.relationcheck;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import lk.udcreations.common.dto.payment.PaymentDTO;
import lk.udcreations.sale.entity.SalesItems;
import lk.udcreations.sale.util.calculate.Calculate;
import lk.udcreations.sale.util.calculate.CalculateUtil;

@Component
public class SalesCheck {

	// Payment check
	public boolean verifyPayment(BigDecimal totalAmount, PaymentDTO payment) {
		BigDecimal calcAmount = Calculate.calcTotalPayments(payment.getCashAmount(), payment.getcCardAmount(),
				payment.getQrAmount(), payment.getChequeAmount(), payment.getDueAmount());
		return CalculateUtil.isEquals(totalAmount, calcAmount);
	}

	// total item count check
	public boolean verifiTotalQuantity(int totalQuantity, List<SalesItems> items) {
		int calcQuantity = Calculate.calcTotalQuantity(items);
		return CalculateUtil.isEquals(totalQuantity, calcQuantity);
	}

	// total amount check
	public boolean verifiTotalAmount(BigDecimal totalAmount, List<SalesItems> items) {
		BigDecimal calcAmount = Calculate.calcTotalAmount(items);
		return CalculateUtil.isEquals(totalAmount, calcAmount);
	}

	// Total discount check
	// TODO
}
