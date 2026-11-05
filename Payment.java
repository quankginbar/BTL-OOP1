public class Payment implements Payable {
    private String method;
    private double amount;
    private boolean paid;

    public Payment(String method, double amount) {
        this.method = method;
        this.amount = amount;
        this.paid = false;
    }

    // Factory Method Pattern (Milestone 5): Tạo đối tượng Payment tự động theo lựa chọn
    public static Payment create(int choice, double amount) {
        if (choice == 1) {
            return new Payment("Cash on delivery", amount);
        }
        Payment payment = new Payment("Bank transfer (demo)", amount);
        payment.processPayment();
        return payment;
    }

    @Override
    public void processPayment() {
        paid = true;
    }

    public String getMethod() { return method; }
    public double getAmount() { return amount; }

    @Override
    public boolean isPaid() { return paid; }
}
