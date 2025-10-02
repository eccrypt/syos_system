package com.syos.application.dto;

import com.syos.domain.model.Bill;
import com.syos.domain.model.BillItem;
import java.util.List;

public class ReportDTOMapper {

	public static BillItemReportDTO toBillItemReportDTO(BillItem billItem) {
		String productName = (billItem.getProduct() != null) ? billItem.getProduct().getName() : "Unknown Product";
		String productCode = (billItem.getProduct() != null) ? billItem.getProduct().getCode() : "N/A";
		double unitPrice = (billItem.getProduct() != null) ? billItem.getProduct().getPrice() : 0.0;
		double calculatedSubtotal = unitPrice * billItem.getQuantity();

		return new BillItemReportDTO(productName, productCode, billItem.getQuantity(), unitPrice, calculatedSubtotal,
				billItem.getDiscountAmount(), billItem.getTotalPrice());
	}

	public static BillReportDTO toBillReportDTO(Bill bill, List<BillItemReportDTO> itemDTOs) {
		return new BillReportDTO(bill.getSerialNumber(), bill.getBillDate(), bill.getTotalAmount(),
				bill.getCashTendered(), bill.getChangeReturned(), bill.getTransactionType(), itemDTOs);
	}

	public static BillReportDTO toBillReportDTO(Bill bill) {
		return new BillReportDTO(bill.getSerialNumber(), bill.getBillDate(), bill.getTotalAmount(),
				bill.getCashTendered(), bill.getChangeReturned(), bill.getTransactionType(), null);
	}
}
