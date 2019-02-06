package com.visualabstractlabs.bookshop.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.visualabstractlabs.bookshop.dao.BookShopDao;
import com.visualabstractlabs.bookshop.dao.BorrowerDao;
import com.visualabstractlabs.bookshop.dao.impl.BookShopDaoImpl;
import com.visualabstractlabs.bookshop.dao.impl.BorrowerDaoImpl;
import com.visualabstractlabs.bookshop.dao.impl.LenderDaoImpl;
import com.visualabstractlabs.bookshop.model.Author;
import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.model.BorrowingInfo;
import com.visualabstractlabs.bookshop.model.LendingInfo;
import com.visualabstractlabs.bookshop.model.User;
import com.visualabstractlabs.bookshop.repository.BookRepository;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;
import com.visualabstractlabs.bookshop.utils.BookshopUtils;

public class BookshopApplication {

	private static Scanner sc = new Scanner(System.in);
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	private static BookShopDao bookShopDao = new BookShopDaoImpl();

	private static BorrowerDao borrowerDao = new BorrowerDaoImpl();

	private static LenderDaoImpl lenderDao = new LenderDaoImpl();

	public static void main(String[] args) throws IOException {

		BookShop bookshop = BookRepository.createBookDatabase();
		boolean quit = false, quitBeforeLogin = false, adminLogin = false;
		int userChoice, userChoiceBeforeLogin;

		String enteredUserId = "0";
		boolean isUserLoggedIn = false;

		do {

			displayWelcomeMenu();
			userChoiceBeforeLogin = takeInput(0, 5);
			switch (userChoiceBeforeLogin) {
				case 1:
					System.out.println("Enter your userId");
					enteredUserId = reader.readLine();
					if (bookShopDao.checkWhetherUserExists(enteredUserId)) {
						isUserLoggedIn = true;
						quit = false;
						adminLogin = false;
					} else {
						System.out.println("Invalid userId entered.Please register");
					}
					break;
				case 2:
					System.out.println();
					System.out.println("Registration Page");
					System.out.println("------------------");
					System.out.println("Enter your name:");
					String name = reader.readLine();
					System.out.println("Enter your street:");
					String street = reader.readLine();
					System.out.println("Enter your city:");
					String city = reader.readLine();
					System.out.println("Enter your country:");
					String country = reader.readLine();
					System.out.println("Enter your phone number:");
					String phoneNumber = reader.readLine();
					User registeredUser = bookShopDao.registerAnUserInBookShop(name, street, city,
							country, phoneNumber);
					isUserLoggedIn = true;
					quit = false;
					adminLogin = false;
					enteredUserId = registeredUser.getUserID();
					System.out.println();
					System.out.println("Hi " + registeredUser.getName() + ",");
					System.out.println("Your UserId is : " + registeredUser.getUserID()
							+ ".Please use this to Login.");
					break;
				case 3:
					quit = false;
					isUserLoggedIn = false;
					adminLogin = true;
					break;
				case 4:
					quitBeforeLogin = true;
					break;
				default:
					System.out.println("Invalid Option Selected.");
					break;
			}

			int userType = 3;

			if (isUserLoggedIn) {
				displayBorrowerLenderBothMenu();
				userType = takeInput(0, 4);

				if (!(userType == 1 || userType == 2 || userType == 3)) {
					System.out.println("Invalid option selected");
				}
			}

			if (isUserLoggedIn && userType == 1) {


				try {
					do {
						System.out.println();
						System.out
								.println("Welcome to TKS Group-4 BookShop - Logged in as BORROWER");
						System.out
								.println("-------------------------------------------------------");
						System.out.println("1) Search the books by name");
						System.out.println("2) Search the books by author name");
						System.out.println("3) Search the books by price range");
						System.out
								.println("4) Display all the books which are available to borrow");
						System.out.println("5) Display all the books borrowed by you");
						System.out.println("6) Borrow a book");
						System.out.println("7) Return a book");
						System.out.println("8) Check your Membership Status");
						System.out.println("100) Quit");
						System.out.println();
						userChoice = takeInput(0, 101);
						switch (userChoice) {
							case 1:
								searchBooksByName();
								break;
							case 2:
								searchBooksByAuthorName();
								break;

							case 3:
								searchBooksByPriceRange();
								break;
							case 4:
								getTheBooksAvailableToBorrow();
								break;
							case 5:
								getTheBooksBorrowedByUser(enteredUserId);
								break;
							case 6:
								borrowABook(enteredUserId);
								break;
							case 7:
								returnABook(enteredUserId);
								break;
							case 8:
								System.out.println();
								bookShopDao.suggestNextMemberShipStatus(enteredUserId);
								break;
							case 100:
								quit = true;
								isUserLoggedIn = false;
								break;
							default:
								System.out.println(
										"Invalid Option Selected. Please renter your choice.");
								break;
						}

					} while (!quit && isUserLoggedIn);

				} catch (InputMismatchException ex) {
					System.out.println("Input entered wrongly");
				}
			} else if (isUserLoggedIn && userType == 2) {

				try {
					do {
						System.out.println();
						System.out.println("Welcome to TKS Group-4 BookShop - Logged in as LENDER");
						System.out.println("-----------------------------------------------------");
						System.out.println("1) Search the books by name");
						System.out.println("2) Search the books by author name");
						System.out.println("3) Search the books by price range");
						System.out.println("4) Display all the books");
						System.out.println("5) Lend a Book");
						System.out.println("6) Display all the books lent by you");
						System.out.println("7) Check your membership status");
						System.out.println("100) Quit");
						System.out.println();
						userChoice = takeInput(0, 101);
						switch (userChoice) {
							case 1:
								searchBooksByName();
								break;
							case 2:
								searchBooksByAuthorName();
								break;

							case 3:
								searchBooksByPriceRange();
								break;
							case 4:
								getAllTheBooksIntheStore();
								break;
							case 5:
								lendABook(enteredUserId);
								break;
							case 6:
								getTheBooksLentByUser(enteredUserId);
								break;
							case 7:
								System.out.println();
								bookShopDao.suggestNextMemberShipStatus(enteredUserId);
								break;
							case 100:
								quit = true;
								isUserLoggedIn = false;
								break;
							default:
								System.out.println(
										"Invalid Option Selected. Please renter your choice.");
								break;
						}

					} while (!quit && isUserLoggedIn);

				} catch (InputMismatchException ex) {
					System.out.println("Input entered wrongly");
				}

			} else if (isUserLoggedIn && userType == 3) {


				try {
					do {
						System.out.println();
						System.out.println(
								"Welcome to TKS Group-4 BookShop - Logged in as BORROWER/LENDER/BUYER");
						System.out.println(
								"------------------------------------------------------------------");
						System.out.println("1) Search the books by name");
						System.out.println("2) Search the books by author name");
						System.out.println("3) Search the books by price range");
						System.out.println("4) Display all the books");
						System.out.println("5) Display orders placed by me");
						System.out.println("6) Place an order");
						System.out.println("7) Check whether book is available to order");
						System.out.println("8) Check your Membership Status");
						System.out.println("9) Display the books borrowed by you");
						System.out.println("10) Borrow a Book");
						System.out.println("11) Lend a Book");
						System.out.println("12) Display the books lent by you");
						System.out.println("13) Return a Book");
						System.out.println("100) Quit");
						System.out.println();
						userChoice = takeInput(0, 101);
						switch (userChoice) {
							case 1:
								searchBooksByName();
								break;
							case 2:
								searchBooksByAuthorName();
								break;

							case 3:
								searchBooksByPriceRange();
								break;
							case 4:
								getAllTheBooksIntheStore();
								break;
							case 5:
								getAllTheOrdersForAUserPresentInStore(enteredUserId);
								break;
							case 6:
								placeAnOrder(enteredUserId);
								break;

							case 7:
								System.out.println();
								System.out.println("Enter the BookId:");
								String bookIdToCheck = reader.readLine();
								System.out.println("Enter the Quantity:");
								int quantityToOrder = sc.nextInt();
								boolean isAvailableToOrder = false;
								try {
									isAvailableToOrder = bookShopDao.checkIfBookIsAvailableToOrder(
											bookIdToCheck, quantityToOrder);
								} catch (BookNotFoundException e) {
									System.out.println(e.getMessage());
								}
								if (isAvailableToOrder) {
									System.out.println("Book is available to Order");
								} else {
									System.out.println(
											"Book cannot be ordered as there is no sufficent amount of quantity available");
								}
								System.out.println();
								break;
							case 8:
								System.out.println();
								bookShopDao.suggestNextMemberShipStatus(enteredUserId);
								break;
							case 9:
								getTheBooksBorrowedByUser(enteredUserId);
								break;
							case 10:
								borrowABook(enteredUserId);
								break;
							case 11:
								lendABook(enteredUserId);
								break;
							case 12:
								getTheBooksLentByUser(enteredUserId);
								break;
							case 13:
								returnABook(enteredUserId);
								break;
							case 100:
								quit = true;
								isUserLoggedIn = false;
								break;
							default:
								System.out.println(
										"Invalid Option Selected. Please renter your choice.");
								break;
						}

					} while (!quit && isUserLoggedIn);

				} catch (InputMismatchException ex) {
					System.out.println("Input entered wrongly");
				}

			} else if (adminLogin) {


				try {
					do {
						System.out.println();
						System.out.println("Welcome to TKS Group-4 BookShop --ADMIN LOGIN");
						System.out.println("----------------------------------------------");
						System.out.println("1) Search the books by name");
						System.out.println("2) Search the books by author name");
						System.out.println("3) Search the books by price range");
						System.out.println("4) Display all the books");
						System.out.println("5) Display all the orders");
						System.out.println("6) Display all users");
						System.out.println("7) Display all the borrowing info");
						System.out.println("8) Display all the lending info");
						System.out.println("9) Return the book to Lender");
						System.out.println("10) Display the Bookshop Earnings");
						System.out.println("11) Add a Book");
						System.out.println("12) Remove a Book");
						System.out.println("100) Quit");
						System.out.println();
						userChoice = takeInput(0, 101);
						switch (userChoice) {
							case 1:
								searchBooksByName();
								break;
							case 2:
								searchBooksByAuthorName();
								break;
							case 3:
								searchBooksByPriceRange();
								break;
							case 4:
								getAllTheBooksIntheStore();
								break;
							case 5:
								getAllTheOrdersPresentInStore();
								break;
							case 6:
								getAllTheUsersPresentInStore();
								break;
							case 7:
								System.out.println("Borrowing Details");
								for (BorrowingInfo b : BookShop.getInstance().getBorrowingInfo()) {
									System.out.println(b.toString());
								}
								break;
							case 8:
								System.out.println("Lending Details");
								for (LendingInfo b : BookShop.getInstance().getLendingInfo()) {
									System.out.println(b.toString());
								}
								break;
							case 9:
								System.out.println("\nReturning book to Lender \n---------------");
								System.out.println("Enter the BookId to return:");
								String bookId = reader.readLine();
								System.out.println("Enter the returning date (yyyy-mm-dd):");
								String returningDate = reader.readLine();
								try {
									lenderDao.returnTheBookToLenderAfterLendingPeriodIsOver(bookId,
											LocalDate.parse(returningDate));
								} catch (BookNotFoundException e) {
									System.out.println(e.getMessage());
								}
								break;
							case 10:
								System.out.println("\nBookShop Earnings:");
								System.out.println("---------------------");
								System.out.println(
										"       Rs." + bookshop.getBookShopEarnings() + " ");
								System.out.println("---------------------");
								break;
							case 11:
								addABookByAdmin();
								break;
							case 12:
								System.out.println("Remove a Book");
								System.out.println("Enter book ID");
								String bookIDToRemove = reader.readLine();
								System.out.println("Enter the quantity to remove");
								int quantityToRemove = sc.nextInt();
								bookshop.removeBook(bookIDToRemove, quantityToRemove);
								break;
							case 100:
								quit = true;
								adminLogin = false;
								break;
							default:
								System.out.println(
										"Invalid Option Selected. Please renter your choice.");
								break;
						}

					} while (!quit && adminLogin);

				} catch (InputMismatchException ex) {
					System.out.println("Input entered wrongly");
				}

			}

		} while (!quitBeforeLogin && !isUserLoggedIn);

	}

	// Asking for Input as Choice
	public static int takeInput(int min, int max) {
		String choice;
		Scanner input = new Scanner(System.in);

		while (true) {
			System.out.println("\nEnter your Choice: ");

			choice = input.next();

			if ((!choice.matches(".*[a-zA-Z]+.*"))
					&& (Integer.parseInt(choice) > min && Integer.parseInt(choice) < max)) {
				return Integer.parseInt(choice);
			}

			else
				System.out.println("\nInvalid Input.");
		}


	}

	public static void displayWelcomeMenu() {

		System.out.println();
		System.out.println("Welcome to TKS Group-4 BookShop");
		System.out.println("-------------------------------");
		System.out.println("1) Login");
		System.out.println("2) New User? then Register");
		System.out.println("3) Admin Login");
		System.out.println("4) Quit");
		System.out.println();

	}

	public static void displayBorrowerLenderBothMenu() {
		System.out.println();
		System.out.println("Welcome to TKS Group-4 BookShop");
		System.out.println("-------------------------------");
		System.out.println("Would you be interested in Borrowing/Lending/Both?");
		System.out.println("1) Borrower");
		System.out.println("2) Lender");
		System.out.println("3) Borrower/Lender/Buyer");
		System.out.println("Please enter the option for the same:");
	}

	/**
	 * Book Search By Name
	 * 
	 * @throws IOException
	 */
	public static void searchBooksByName() throws IOException {

		System.out.println("Enter the Book Name you want to search:");
		String bookName = reader.readLine();

		List<Book> books = BookShop.getInstance().getListOfBooksByName(bookName);

		if (books.size() > 0) {
			for (Book book : books) {
				System.out.println(book.toString());
			}
		} else {
			System.out.println("No Books available for the search.");
		}
		System.out.println();
	}

	/**
	 * Books search by author Name
	 * 
	 * @throws IOException
	 * 
	 */
	public static void searchBooksByAuthorName() throws IOException {

		System.out.println("Enter the Author Name you want to search:");
		String authorName = reader.readLine();
		List<Book> authorBooks = BookShop.getInstance().getListOfBooksByAuthor(authorName);

		if (authorBooks.size() > 0) {
			for (Book book : authorBooks) {
				System.out.println(book.toString());
			}
		} else {
			System.out.println("No Books available for the search.");
		}
		System.out.println();
	}

	/**
	 * Books search by Price Range
	 * 
	 * @throws IOException
	 * 
	 */
	public static void searchBooksByPriceRange() throws IOException {

		System.out.println("Search Books by price range:");
		System.out.println("From:");
		double from = sc.nextDouble();
		System.out.println("To:");
		double to = sc.nextDouble();
		List<Book> booksByPriceRange = BookShop.getInstance().getListOfBooksByPriceRange(from, to);

		if (booksByPriceRange.size() > 0) {
			for (Book book : booksByPriceRange) {
				System.out.println(book.toString());
			}
		} else {
			System.out.println("No Books available for the search.");
		}
		System.out.println();
	}


	public static void getAllTheBooksIntheStore() {

		System.out.println("Books Present");
		System.out.println("--------------");
		bookShopDao.getAllTheBooksPresentInTheStore();

	}

	public static void getAllTheOrdersPresentInStore() {
		System.out.println("Orders Present");
		System.out.println("--------------");
		bookShopDao.getAllTheOrders();
	}

	public static void getAllTheOrdersForAUserPresentInStore(String userId) {
		System.out.println("Orders Present");
		System.out.println("--------------");
		try {
			bookShopDao.getAllTheOrdersForAUser(userId);
		} catch (UserNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void getAllTheUsersPresentInStore() {
		System.out.println("Users Present");
		System.out.println("-------------");
		bookShopDao.getAllTheUsers();
	}

	public static void placeAnOrder(String userId) throws IOException {
		System.out.println("Place an Order");
		System.out.println("Enter BookId:");
		String bookId = reader.readLine();
		System.out.println("Enter the quantity:");
		int quantity = sc.nextInt();
		try {
			bookShopDao.placeAnOrder(userId, bookId, quantity);
		} catch (BookNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (UserNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (CustomException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void borrowABook(String userId) throws IOException {

		System.out.println("Borrow a book");
		System.out.println("Enter BookId you want to borrow:");
		String bookIdToBorrow = reader.readLine();
		System.out.println("Enter the number of days you want to borrow :");
		int noOfDaysToBorrow = sc.nextInt();
		try {
			borrowerDao.borrowABook(userId, bookIdToBorrow, noOfDaysToBorrow);
		} catch (BookNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (UserNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (CustomException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void returnABook(String userId) throws IOException {
		System.out.println();
		System.out.println("Return a book");
		System.out.println("Enter the BookId you want to return:");
		String bookIdToReturn = reader.readLine();
		System.out.println("Enter the returned Date(yyyy-mm-dd):");
		String returnedDate = reader.readLine();
		try {
			borrowerDao.returnBook(userId, bookIdToReturn, LocalDate.parse(returnedDate));
		} catch (BookNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (UserNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (CustomException e) {
			System.out.println(e.getMessage());
		}
		System.out.println();
	}

	public static void lendABook(String userId) throws IOException {

		System.out.println("As you are using this book shop as market place to Lend the book.");
		System.out.println(
				"You have to pay 5% of the Lending charges will be applied once the lending period is over.");
		System.out.println(
				"And You will get the lending charges to you once lending period is over\n");

		System.out.println("Lend a Book");
		System.out.println("----------------");
		System.out.println("Enter the book name:");
		String bookNameToLend = reader.readLine();
		System.out.println("Enter the number of authors for this book:");
		int numberOfAuthor = sc.nextInt();

		Set<String> authorNames = new HashSet<>();
		for (int i = 0; i < numberOfAuthor; i++) {

			System.out.println("Enter the author " + (i + 1) + " name:");
			String authorNameOfBookToLend = reader.readLine();
			authorNames.add(authorNameOfBookToLend);
		}

		System.out.println("Enter the Lending period in Days:");
		int lendingPeriodInDays = sc.nextInt();
		System.out.println("Enter the Lending charges:");
		double lendingCharges = sc.nextDouble();
		try {
			lenderDao.lendABook(userId, bookNameToLend, authorNames, lendingPeriodInDays,
					lendingCharges);
		} catch (UserNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (CustomException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void getTheBooksBorrowedByUser(String userId) {

		System.out.println("Books Borrowed By you:");
		System.out.println("----------------------");
		try {
			borrowerDao.getTheBooksBorrowedByUser(userId);
		} catch (UserNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void getTheBooksLentByUser(String userId) {
		System.out.println("Books lent By you:");
		System.out.println("----------------------");
		try {
			lenderDao.getTheBooksLentByUser(userId);
		} catch (UserNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void getTheBooksAvailableToBorrow() {
		System.out.println("Books Available To Borrow:");
		System.out.println("--------------------------");
		borrowerDao.getAllTheBooksPresentInTheStoreWhichAreAvailableToBorrow();
	}

	public static void addABookByAdmin() throws IOException {
		System.out.println("Add a Book");
		System.out.println("Enter book name");
		String bookNameA = reader.readLine();
		System.out.println("Enter the number of authors for this book:");
		int numberOfAuthor = sc.nextInt();

		Set<String> authorNames = new HashSet<>();
		for (int i = 0; i < numberOfAuthor; i++) {

			System.out.println("Enter the author " + (i + 1) + " name:");
			String authorNameOfBookToLend = reader.readLine();
			authorNames.add(authorNameOfBookToLend);
		}

		System.out.println("Enter quantity");
		int quan = sc.nextInt();
		System.out.println("Enter price of book");
		double price = sc.nextDouble();

		System.out.println("Do you want to make this book available to Borrow?");
		System.out.println("Press y or Y for Yes and n or N for No:");
		String choiceForBorrow = takeInputForBorrow();
		boolean isAvailableToBorrow = false;
		int quantityAvailableToBorrow = 0;
		if (choiceForBorrow.equalsIgnoreCase("Y")) {
			isAvailableToBorrow = true;

			System.out.println("How many copies do you want to make as available to borrow:");
			quantityAvailableToBorrow = takeInput(0, quan + 1);

		}

		String bookIdA = BookshopUtils.getUUID();
		Set<Author> al = new HashSet<>();
		for (String aName : authorNames) {
			Author aa = new Author(BookshopUtils.getUUID(), aName);
			al.add(aa);
		}
		Book b = new Book(bookIdA, bookNameA, al, price, quan, quantityAvailableToBorrow);
		b.setAvialableToBorrow(isAvailableToBorrow);
		try {
			BookShop.getInstance().addBook(b, quan, quantityAvailableToBorrow);
		} catch (CustomException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static String takeInputForBorrow() throws IOException {

		String choice;
		while (true) {
			System.out.println("\nEnter your Choice: ");

			choice = reader.readLine();

			if (choice.equals("y") || choice.equals("Y") || choice.equals("n")
					|| choice.equals("Y")) {
				return choice;
			}

			else
				System.out.println("\nInvalid Input.Press y or Y for Yes and n or N for No:");
		}


	}

}
