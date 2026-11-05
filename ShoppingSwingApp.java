import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Giao diện Java Swing tối giản, dễ đọc và dễ học nhất.
 * Chỉ sử dụng các thành phần cơ bản: JFrame, JPanel, JTable, JTextArea, JButton, JOptionPane.
 */
public class ShoppingSwingApp extends JFrame {
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();
    private int nextCustomerId = 2;
    private int nextProductId = 4;
    private final CheckoutService checkoutService = new CheckoutService();
    private User currentUser;

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // Các bảng và vùng hiển thị
    private DefaultTableModel productModel = new DefaultTableModel(new Object[]{"ID", "Name", "Price", "Stock"}, 0);
    private DefaultTableModel adminProductModel = new DefaultTableModel(new Object[]{"ID", "Name", "Price", "Stock"}, 0);
    private JTextArea cartArea = new JTextArea(10, 30);
    private JTextArea orderHistoryArea = new JTextArea(10, 30);

    public ShoppingSwingApp() {
        // 1. Tạo dữ liệu mẫu
        users.add(new Admin("A001", "admin", "admin123"));
        users.add(new Customer("C001", "quan", "123456"));
        products.add(new Product("P001", "Keyboard", 350000, 10));
        products.add(new Product("P002", "Mouse", 150000, 15));
        products.add(new Product("P003", "Headphones", 450000, 8));

        // 2. Cài đặt cửa sổ chính
        setTitle("Shopping Online System");
        setSize(700, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createCustomerPanel(), "CUSTOMER");
        mainPanel.add(createAdminPanel(), "ADMIN");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // ==================== 1. MÀN HÌNH ĐĂNG NHẬP & ĐĂNG KÝ ====================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 150, 80, 150));

        JTextField userField = new JTextField("quan");
        JPasswordField passField = new JPasswordField("123456");
        JButton loginBtn = new JButton("1. Login");
        JButton registerBtn = new JButton("2. Register Customer");

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(loginBtn);
        panel.add(registerBtn);
        panel.add(new JLabel("Demo Admin: admin / admin123"));
        panel.add(new JLabel("Demo Customer: quan / 123456"));

        // Sự kiện Đăng nhập
        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            for (User user : users) {
                if (user.login(u, p)) {
                    currentUser = user;
                    refreshData();
                    if (user instanceof Admin) cardLayout.show(mainPanel, "ADMIN");
                    else cardLayout.show(mainPanel, "CUSTOMER");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Incorrect username or password.");
        });

        // Sự kiện Đăng ký
        registerBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (u.isEmpty() || p.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.");
                return;
            }
            for (User user : users) {
                if (user.getUsername().equalsIgnoreCase(u)) {
                    JOptionPane.showMessageDialog(this, "Username already exists.");
                    return;
                }
            }
            String id = String.format("C%03d", nextCustomerId++);
            users.add(new Customer(id, u, p));
            JOptionPane.showMessageDialog(this, "Registered! Customer ID: " + id);
        });

        return panel;
    }

    // ==================== 2. MÀN HÌNH KHÁCH HÀNG ====================
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(productModel);

        // Thanh nút bấm bên dưới cho Khách hàng
        JPanel bottom = new JPanel(new FlowLayout());
        JTextField qtyField = new JTextField("1", 4);
        JButton addCartBtn = new JButton("Add to Cart");
        JButton checkoutBtn = new JButton("Checkout");
        JButton changePassBtn = new JButton("Change Password");
        JButton logoutBtn = new JButton("Logout");

        bottom.add(new JLabel("Qty:"));
        bottom.add(qtyField);
        bottom.add(addCartBtn);
        bottom.add(checkoutBtn);
        bottom.add(changePassBtn);
        bottom.add(logoutBtn);

        // Chia 3 tab đơn giản: Sản phẩm, Giỏ hàng, Lịch sử đơn
        JTabbedPane tabs = new JTabbedPane();
        cartArea.setEditable(false);
        orderHistoryArea.setEditable(false);
        tabs.addTab("1. Products", new JScrollPane(table));
        tabs.addTab("2. Cart", new JScrollPane(cartArea));
        tabs.addTab("3. Order History", new JScrollPane(orderHistoryArea));

        panel.add(tabs, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        // Nút thêm vào giỏ
        addCartBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a product in table!");
                return;
            }
            try {
                int qty = Integer.parseInt(qtyField.getText().trim());
                Product prod = products.get(row);
                ((Customer) currentUser).getCart().addProduct(prod, qty);
                refreshData();
                JOptionPane.showMessageDialog(this, "Added to cart!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Nút thanh toán (Checkout)
        checkoutBtn.addActionListener(e -> {
            Customer customer = (Customer) currentUser;
            Cart cart = customer.getCart();
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty.");
                return;
            }
            String[] methods = {"Cash on delivery", "Bank transfer (demo)"};
            int choice = JOptionPane.showOptionDialog(this, "Choose payment method:", "Checkout",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, methods, methods[0]);
            if (choice < 0) return;

            try {
                Order order = checkoutService.checkout(customer, choice);
                refreshData();
                tabs.setSelectedIndex(2);
                JOptionPane.showMessageDialog(this, "Order placed! ID: " + order.getId());
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, "Checkout failed: " + ex.getMessage());
            }
        });

        changePassBtn.addActionListener(e -> changePasswordDialog());
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        return panel;
    }

    // ==================== 3. MÀN HÌNH ADMIN ====================
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(new JTable(adminProductModel)), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(10);
        JTextField priceField = new JTextField(7);
        JTextField stockField = new JTextField(4);
        JButton addBtn = new JButton("Add Product");
        JButton logoutBtn = new JButton("Logout");

        bottom.add(new JLabel("Name:")); bottom.add(nameField);
        bottom.add(new JLabel("Price:")); bottom.add(priceField);
        bottom.add(new JLabel("Stock:")); bottom.add(stockField);
        bottom.add(addBtn);
        bottom.add(logoutBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                if (name.isEmpty() || price <= 0 || stock < 0) {
                    JOptionPane.showMessageDialog(this, "Invalid product data!");
                    return;
                }
                String id = String.format("P%03d", nextProductId++);
                products.add(new Product(id, name, price, stock));
                refreshData();
                JOptionPane.showMessageDialog(this, "Product added: " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and stock.");
            }
        });

        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        return panel;
    }

    // ==================== 4. CẬP NHẬT DỮ LIỆU HIỂN THỊ ====================
    private void refreshData() {
        productModel.setRowCount(0);
        adminProductModel.setRowCount(0);
        for (Product p : products) {
            Object[] row = {p.getId(), p.getName(), p.getPrice(), p.getStock()};
            productModel.addRow(row);
            adminProductModel.addRow(row);
        }

        if (currentUser instanceof Customer) {
            Customer c = (Customer) currentUser;
            StringBuilder cartText = new StringBuilder("--- YOUR CART ---\n");
            for (CartItem item : c.getCart().getItems()) {
                cartText.append(item.getProduct().getName())
                        .append(" x ").append(item.getQuantity())
                        .append(" = ").append(item.getSubtotal()).append(" VND\n");
            }
            cartText.append("Total: ").append(c.getCart().getTotal()).append(" VND");
            cartArea.setText(cartText.toString());

            StringBuilder orderText = new StringBuilder("--- ORDER HISTORY ---\n");
            for (Order o : c.getOrderHistory()) {
                orderText.append("Order ID: ").append(o.getId())
                        .append(" | Total: ").append(o.getPayment().getAmount()).append(" VND")
                        .append(" | Method: ").append(o.getPayment().getMethod())
                        .append(" | Status: ").append(o.getPayment().isPaid() ? "PAID" : "UNPAID").append("\n");
                for (OrderItem item : o.getItems()) {
                    orderText.append("   + ").append(item).append("\n");
                }
                orderText.append("------------------------------\n");
            }
            orderHistoryArea.setText(orderText.toString());
        }
    }

    private void changePasswordDialog() {
        String oldP = JOptionPane.showInputDialog(this, "Current password:");
        String newP = JOptionPane.showInputDialog(this, "New password:");
        if (oldP != null && newP != null && currentUser.changePassword(oldP, newP)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Password change failed!");
        }
    }

    public static void main(String[] args) {
        new ShoppingSwingApp().setVisible(true);
    }
}
