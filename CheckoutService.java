public class CheckoutService {
    private int nextOrderId = 1;

    public Order checkout(Customer customer, int paymentChoice) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        if (paymentChoice < 0 || paymentChoice > 1) {
            throw new IllegalArgumentException("Invalid payment method.");
        }

        Cart cart = customer.getCart();
        if (cart.isEmpty()) {
            throw new IllegalStateException("Your cart is empty.");
        }

        for (CartItem item : cart.getItems()) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                throw new IllegalStateException(
                        "Insufficient stock for " + item.getProduct().getName() + ".");
            }
        }

        String id = String.format("O%03d", nextOrderId);
        Payment payment = Payment.create(paymentChoice, cart.getTotal());
        Order order = new Order(id, customer.getId(), cart, payment);

        for (CartItem item : cart.getItems()) {
            if (!item.getProduct().reduceStock(item.getQuantity())) {
                throw new IllegalStateException(
                        "Insufficient stock for " + item.getProduct().getName() + ".");
            }
        }

        customer.addOrder(order);
        cart.clear();
        nextOrderId++;
        return order;
    }
}
