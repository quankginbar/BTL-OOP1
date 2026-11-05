# ShoppingOnlineSystem - Java Swing

A small Java Swing online shopping application for practicing core Java implementation.

## Milestone 4 requirements covered

- **Java classes and OOP:** `User`, `Customer`, `Admin`, `Product`, `Cart`, `CartItem`, `Order`, `OrderItem`, `Payment`, and `CheckoutService`.
- **Encapsulation:** fields are private and accessed through methods.
- **Inheritance and polymorphism:** `Customer` and `Admin` extend the abstract `User` class and implement `getRole()`.
- **Collections:** `ArrayList` stores users, products, cart items, and order history.
- **Exception handling:** `NumberFormatException` is caught when the user enters invalid numeric input; `IllegalArgumentException` is caught when a cart operation has invalid quantity or insufficient stock.
- **Runnable Swing application:** login, customer registration, product listing, cart, checkout, order history, password change, and admin product creation.
- **Checkout service:** creates orders, processes demo payments, updates stock, and clears the cart independently of the Swing interface.

## Requirements

- JDK 8 or later
- Terminal / Command Prompt

## Compile and run

Open PowerShell in the `ShoppingOnlineSystem` folder and run:

```powershell
javac -encoding UTF-8 (Get-ChildItem .\src\*.java | ForEach-Object { $_.FullName })
java -cp .\src ShoppingSwingApp
```

## Demo accounts

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Customer | `quan` | `123456` |

You can register another customer from the main menu.

## Suggested manual tests

1. Log in as admin and enter invalid price or stock values: the app should show a validation message.
2. Log in with an incorrect password: login should fail.
3. Add a quantity of `0` or a negative quantity to the cart: the program should reject it.
4. Try adding more items than the available stock: the program should reject it.
5. Log in as admin and add a product, then view the product list.
6. Log in as customer, place an order, and check the order history.

## Limitations

- Data is stored in memory and resets when the program exits; no database or file persistence is implemented.
- Payment is simulated; no real money is transferred.
- Passwords are stored as plain text for this beginner educational example and are not suitable for production use.
