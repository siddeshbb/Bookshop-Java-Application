# Bookshop-Java-Application
Bookshop Java Application

[![Build Status](https://travis-ci.com/siddeshbb/Bookshop-Java-Application.svg?token=RLELX5m9XtGxLBzReeZQ&branch=master)](https://travis-ci.com/siddeshbb/Bookshop-Java-Application) 

## Bookshop application

- ### Features of the bookshop
  - Search for books based on the name
  - Search for books based on the author
  - Search for books based on the price range
  - Customer will be able to check if a book is available to order
  - Customer will be able to place an order
  - Display all the orders placed 
  - Display the customers with their membership status
  - Customer shall be able to check their membership status and a hint on how they can move to next level (i.e, how much more needs to be spent) 
  - Customers will be able to place the order at the discounted price when they place an order 
  
 ## Book lending and Borrowing system
  Our bookshop becomes a kind of martket place. Here existing buyers can lend their books to borrowers. The borrowers are also customers of the bookshop but they are not interested to buy the books but only want to borrow the books for short period of time to read.
  Our Bookshop also handles the fine for late return of the book.
  
Explain on how the whole end to end borrowing and lending scenario works:

```
* User U1 registers in our bookshop and lends a book for 12 days with lending charges of Rs.100.
* User U2 register in our bookshop and borrows the same book for 12 days.

* User U2 returns the book on 13th day then borrowing charges is calculated as follows:
  - Borrowing charges(to be paid by User U2) = Rs.100(lending charges for 12 days) * 1 + Rs.1 *13(for each day of borrowing) + R.5 * 1 (Rs.5 fine for each extra day) = Rs.118.0
  - Bookshop Earnings (out of the above charges)= Rs.13 * 1(for each of borrowing) = Rs.13.0
  
 * Then Admin of the bookshop returns the book on 13th day to lender (User U1), then 
    - Lender (User U1) will get = Rs.105(borrowing charges paid by borrower while returning(borrowing charges -bookshop earnings)) - R.5 (5% bookshop commision) + Rs.5 (fine paid by the borrower) = 105.0
   - Bookshop Earnings (out of the above charges)= Rs.5 (5% bookshop commision) = Rs.5.0
   
  * So Out of the whole process of borrowing and lending process:
    - Bookshop will earn = Rs.13 (by borrower) + R.5 (by lender) = Rs.18.0
    - Lender will earn = Rs.105.0
    - Borrower will pay = Rs.118.0

```
  
