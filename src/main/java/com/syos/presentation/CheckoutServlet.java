package com.syos.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.syos.application.factory.BillItemFactory;
import com.syos.domain.model.Bill;
import com.syos.domain.model.BillItem;
import com.syos.domain.model.Customer;
import com.syos.domain.model.Product;
import com.syos.infrastructure.repository.BillingRepository;
import com.syos.infrastructure.repository.BillingRepositoryImpl;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.repository.ProductRepositoryImpl;
import com.syos.infrastructure.repository.ShelfStockRepository;
import com.syos.infrastructure.repository.ShelfStockRepositoryImpl;
import com.syos.infrastructure.singleton.InventoryManager;
import com.syos.application.strategy.DiscountPricingStrategy;
import com.syos.application.strategy.NoDiscountStrategy;

public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BillingRepository billingRepository = new BillingRepositoryImpl();
    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final ShelfStockRepository shelfStockRepository = new ShelfStockRepositoryImpl(productRepository);
    private final BillItemFactory billItemFactory = new BillItemFactory(
            new DiscountPricingStrategy(new NoDiscountStrategy()));
    private final InventoryManager inventoryManager = InventoryManager.getInstance(null);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Customer customer = (Customer) session.getAttribute("user");

        if (customer == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login to checkout");
            return;
        }

        try {
            // Parse cart items from form
            List<BillItem> billItems = parseCartItems(req);
            if (billItems.isEmpty()) {
                req.setAttribute("error", "Your cart is empty");
                req.getRequestDispatcher("/customer/cart.jsp").forward(req, resp);
                return;
            }

            // Validate stock availability
            for (BillItem item : billItems) {
                int availableStock = inventoryManager.getAvailableStock(item.getProduct().getCode());
                if (availableStock < item.getQuantity()) {
                    req.setAttribute("error", "Insufficient stock for " + item.getProduct().getName());
                    req.getRequestDispatcher("/customer/cart.jsp").forward(req, resp);
                    return;
                }
            }

            // Calculate totals
            double totalDue = billItems.stream().mapToDouble(BillItem::getTotalPrice).sum();
            String paymentMethod = req.getParameter("paymentMethod");

            if ("cash".equals(paymentMethod)) {
                String cashTenderedStr = req.getParameter("cashTendered");
                if (cashTenderedStr == null || cashTenderedStr.trim().isEmpty()) {
                    req.setAttribute("error", "Cash amount is required for cash payment");
                    req.getRequestDispatcher("/customer/cart.jsp").forward(req, resp);
                    return;
                }

                double cashTendered = Double.parseDouble(cashTenderedStr);
                if (cashTendered < totalDue) {
                    req.setAttribute("error", "Cash tendered is less than total amount");
                    req.getRequestDispatcher("/customer/cart.jsp").forward(req, resp);
                    return;
                }
            }

            // Create bill
            int serialNumber = billingRepository.nextSerial();
            double cashTendered = "cash".equals(paymentMethod) ?
                Double.parseDouble(req.getParameter("cashTendered")) : totalDue;

            Bill bill = new Bill.BillBuilder(serialNumber, billItems)
                    .withCashTendered(cashTendered)
                    .build();

            // Save bill and update inventory
            billingRepository.save(bill);
            deductStock(billItems);

            // Clear session cart (if using session-based cart)
            session.removeAttribute("cart");

            // Redirect to success page with bill details
            req.setAttribute("bill", bill);
            req.setAttribute("success", "Order completed successfully!");
            req.getRequestDispatcher("/customer/order-success.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Checkout failed: " + e.getMessage());
            req.getRequestDispatcher("/customer/cart.jsp").forward(req, resp);
        }
    }

    private List<BillItem> parseCartItems(HttpServletRequest req) throws Exception {
        List<BillItem> billItems = new ArrayList<>();

        // Parse items from form parameters (items[0][productCode], items[0][quantity], etc.)
        int index = 0;
        while (req.getParameter("items[" + index + "][productCode]") != null) {
            String productCode = req.getParameter("items[" + index + "][productCode]");
            int quantity = Integer.parseInt(req.getParameter("items[" + index + "][quantity]"));

            Product product = productRepository.findByCode(productCode);
            if (product == null) {
                throw new Exception("Product not found: " + productCode);
            }

            BillItem billItem = billItemFactory.create(product, quantity);
            billItems.add(billItem);
            index++;
        }

        return billItems;
    }

    private void deductStock(List<BillItem> billItems) {
        for (BillItem item : billItems) {
            inventoryManager.deductFromShelf(item.getProduct().getCode(), item.getQuantity());
        }
    }
}