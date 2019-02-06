package com.visualabstractlabs.bookshop.dao.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.visualabstractlabs.bookshop.dao.BookShopDao;
import com.visualabstractlabs.bookshop.dao.BorrowerDao;
import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.model.BorrowingInfo;
import com.visualabstractlabs.bookshop.model.LendingInfo;
import com.visualabstractlabs.bookshop.model.User;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;
import com.visualabstractlabs.bookshop.reuse.validations.ValidationsHelper;
import com.visualabstractlabs.bookshop.utils.BookshopUtils;

public class BorrowerDaoImpl implements BorrowerDao {

	BookShopDao bookShopDao = new BookShopDaoImpl();


	/**
	 * Method to display all the books in Book store which are available to borrow
	 */
	@Override
	public List<Book> getAllTheBooksPresentInTheStoreWhichAreAvailableToBorrow() {

		BookShop bookShop = BookShop.getInstance();

		List<Book> bookList = new ArrayList<Book>();
		if (bookShop.getBooks() != null && bookShop.getBooks().size() > 0) {
			for (Book book : bookShop.getBooks()) {
				if (book.isAvialableToBorrow() && book.getNoOfCopiesAvailableToBorrow() > 0) {
					System.out.println(book.toString());
					bookList.add(book);
				}
			}
		} else {
			System.out.println("No Books Borrowed by you");
		}
		return bookList;
	}

	@Override
	public List<Book> getTheBooksBorrowedByUser(String userId) throws UserNotFoundException {

		if (!ValidationsHelper.validateUserId(userId)) {
			throw new UserNotFoundException("Invalid UserId provided.");
		}

		BookShop bookShop = BookShop.getInstance();

		List<Book> bookList = new ArrayList<Book>();

		if (bookShop.getBorrowingInfo() != null && bookShop.getBorrowingInfo().size() > 0) {
			for (BorrowingInfo borrower : bookShop.getBorrowingInfo()) {
				if (borrower.getUserId().equals(userId) && !borrower.isProcessed()) {

					Book borrowedBook = borrower.getBorrowedBook();
					System.out.println(borrowedBook.toString());
					bookList.add(borrowedBook);

				}
			}
		}


		return bookList;
	}

	@Override
	public boolean checkIfBookIsAvailableToBorrow(String bookId) throws BookNotFoundException {

		if (!ValidationsHelper.validateBookId(bookId)) {
			throw new BookNotFoundException("Invalid BookId provided.");
		}

		BookShop bookShop = BookShop.getInstance();

		List<Book> book = bookShop.getBooks().stream().filter(b -> b.getBookId().equals(bookId))
				.collect(Collectors.toList());

		for (Book bookobj : book) {
			if (bookobj.getBookId().equals(bookId) && bookobj.getNoOfCopies() >= 1
					&& bookobj.isAvialableToBorrow()
					&& bookobj.getNoOfCopiesAvailableToBorrow() >= 1) {

				return true;
			}
		}

		return false;
	}

	@Override
	public void borrowABook(String userId, String bookId, int noOfDaysToBorrow)
			throws UserNotFoundException, BookNotFoundException, CustomException {

		// Validate UserId
		if (!ValidationsHelper.validateUserId(userId)) {
			throw new UserNotFoundException("Invalid UserId provided.");
		}

		// Validate BookId
		if (!ValidationsHelper.validateBookId(bookId)) {
			throw new BookNotFoundException("Invalid BookId provided.");
		}

		if (ValidationsHelper.checkWhetherUserHasAlreadyBorrowedThisBook(userId, bookId)) {
			throw new CustomException("You have already borrowed this book");
		}

		BookShop bookShop = BookShop.getInstance();

		if (checkIfBookIsAvailableToBorrow(bookId)) {

			// Add an entry in Borrower information
			BorrowingInfo borrower = new BorrowingInfo();
			borrower.setBorrowingId(BookshopUtils.getUUID());
			borrower.setUserId(userId);

			Book borrowBook = bookShop.getBooks().stream().filter(b -> b.getBookId().equals(bookId))
					.collect(Collectors.toList()).get(0);

			borrower.setBorrowedBook(borrowBook);
			LocalDate localDate = LocalDate.now();
			borrower.setBorrowedDate(localDate);
			borrower.setBorrowingPeriodInDays(noOfDaysToBorrow);


			// Decrement the quantity of books in Bookshop
			Book bookToRemove = null;
			for (Book bookobj : bookShop.getBooks()) {
				if (bookobj.getBookId().equals(bookId)) {
					bookobj.setNoOfCopies(bookobj.getNoOfCopies() - 1);
					bookobj.setNoOfCopiesAvailableToBorrow(
							bookobj.getNoOfCopiesAvailableToBorrow() - 1);

					if (bookobj.getNoOfCopies() == 0
							&& bookobj.getNoOfCopiesAvailableToBorrow() == 0) {
						bookToRemove = bookobj;
					}

				}
			}

			if (bookToRemove != null) {
				bookShop.getBooks().remove(bookToRemove);
			}

			borrower.setProcessed(false);

			if (bookShop.getBorrowingInfo() != null && bookShop.getBorrowingInfo().size() > 0) {
				bookShop.getBorrowingInfo().add(borrower);
			} else {
				List<BorrowingInfo> borrowingInfoList = new ArrayList<>();
				borrowingInfoList.add(borrower);
				bookShop.setBorrowingInfo(borrowingInfoList);
			}

			System.out.println("Borrowing Information");
			System.out.println("-------------------------");
			System.out.println(borrower.toString());
			System.out.println("-------------------------");


		} else {
			System.out.println("Book is not avialble to Borrow");
		}

	}

	@Override
	public void returnBook(String userId, String bookId, LocalDate returnedDate)
			throws UserNotFoundException, BookNotFoundException, CustomException {

		// Validate UserId
		if (!ValidationsHelper.validateUserId(userId)) {
			throw new UserNotFoundException("Invalid UserId provided.");
		}


		BookShop bookShop = BookShop.getInstance();

		// validate whether user has borrowed this book
		Long isBookBorrowedByUser = bookShop.getBorrowingInfo().stream()
				.filter(u -> (u.getUserId().equals(userId)
						&& u.getBorrowedBook().getBookId().equals(bookId)) && !u.isProcessed())
				.distinct().count();
		if (isBookBorrowedByUser == 0) {
			System.out.println("The mentioned Book is not borrowed by the User.");
			return;
		}

		// return the book

		Book bookToAdd = bookShop.getBorrowingInfo().stream()
				.filter(u -> (u.getUserId().equals(userId)
						&& u.getBorrowedBook().getBookId().equals(bookId) && !u.isProcessed()))
				.collect(Collectors.toList()).get(0).getBorrowedBook();

		bookShop.addBook(bookToAdd, 1, 1);


		// check whether this book is lended by another user
		boolean isBookLendedByOtherUser = false;
		Long isBookLendedByOtherUserCount = bookShop.getLendingInfo().stream()
				.filter(b -> b.getLendedBookId().equals(bookId)).distinct().count();
		if (isBookLendedByOtherUserCount > 0) {
			isBookLendedByOtherUser = true;
		}

		double charges = 0.0;
		if (isBookLendedByOtherUser) {
			LendingInfo lend = bookShop.getLendingInfo().stream()
					.filter(b -> b.getLendedBookId().equals(bookId)).collect(Collectors.toList())
					.get(0);
			charges = lend.getLendingCharges();
		}


		BorrowingInfo borr = bookShop.getBorrowingInfo().stream()
				.filter(u -> (u.getUserId().equals(userId)
						&& u.getBorrowedBook().getBookId().equals(bookId) && !u.isProcessed()))
				.collect(Collectors.toList()).get(0);
		borr.setReturnedDate(returnedDate);

		LocalDate expectedDateToReturn =
				borr.getBorrowedDate().plus(Period.ofDays(borr.getBorrowingPeriodInDays()));

		int expectedBorrowedPeriod =
				Period.between(borr.getBorrowedDate(), expectedDateToReturn).getDays();
		int actualBorrowedPeriod = Period.between(borr.getBorrowedDate(), returnedDate).getDays();

		/*
		 * If expectedBorrowedPeriod is same as actualBorrowedPeriod then, --> if book is lended by
		 * other user --> get the lending charges and set as borrowing charges
		 * 
		 * --> if book is lended by bookshop itself --> then charges will be (noofdays * 1)
		 * 
		 * 
		 */
		if (expectedBorrowedPeriod >= actualBorrowedPeriod) {

			if (isBookLendedByOtherUser) {

				// set the borrowing charges
				borr.setBorrowingCharges(charges + actualBorrowedPeriod);

				// enhance the totalSpend of the borrowed user
				User borrowedUser =
						bookShop.getUsers().stream().filter(u -> u.getUserID().equals(userId))
								.collect(Collectors.toList()).get(0);
				borrowedUser.setTotalSpend(
						borrowedUser.getTotalSpend() + charges + actualBorrowedPeriod);


			} else {
				// set the borrowing charges as Rs.1 per day
				borr.setBorrowingCharges(actualBorrowedPeriod * 1.0);

				// enhance the totalSpend of the borrowed user
				User borrowedUser =
						bookShop.getUsers().stream().filter(u -> u.getUserID().equals(userId))
								.collect(Collectors.toList()).get(0);
				borrowedUser.setTotalSpend(borrowedUser.getTotalSpend() + actualBorrowedPeriod);


			}
			// enhance the bookshop earnings
			bookShop.setBookShopEarnings(bookShop.getBookShopEarnings() + actualBorrowedPeriod);

			borr.setFinePaid(0.0);

		} else if (actualBorrowedPeriod > expectedBorrowedPeriod) {

			double fine = 5.0 * (actualBorrowedPeriod - expectedBorrowedPeriod);

			if (isBookLendedByOtherUser) {

				// set the borrowing charges
				borr.setBorrowingCharges(charges + actualBorrowedPeriod + fine);

				// enhance the totalSpend of the borrowed user
				User borrowedUser =
						bookShop.getUsers().stream().filter(u -> u.getUserID().equals(userId))
								.collect(Collectors.toList()).get(0);
				borrowedUser.setTotalSpend(
						borrowedUser.getTotalSpend() + charges + actualBorrowedPeriod + fine);


				// enhance the lending charges as the earnings for the lended user
				LendingInfo lend = bookShop.getLendingInfo().stream()
						.filter(b -> b.getLendedBookId().equals(bookId))
						.collect(Collectors.toList()).get(0);
				User lendedUser = bookShop.getUsers().stream()
						.filter(u -> u.getUserID().equals(lend.getUserId()))
						.collect(Collectors.toList()).get(0);
				lendedUser.setEarnings(lendedUser.getEarnings() + fine);

				// enhance the bookshop earnings
				bookShop.setBookShopEarnings(bookShop.getBookShopEarnings() + actualBorrowedPeriod);

			} else {

				// set the borrowing charges as Rs.1 per day
				borr.setBorrowingCharges(actualBorrowedPeriod * 1.0 + fine);

				// enhance the totalSpend of the borrowed user
				User borrowedUser =
						bookShop.getUsers().stream().filter(u -> u.getUserID().equals(userId))
								.collect(Collectors.toList()).get(0);
				borrowedUser
						.setTotalSpend(borrowedUser.getTotalSpend() + actualBorrowedPeriod + fine);

				// enhance the bookshop earnings
				bookShop.setBookShopEarnings(
						bookShop.getBookShopEarnings() + actualBorrowedPeriod + fine);

			}

			borr.setFinePaid(fine);

			System.out.println("Book returned later than expected.");
			System.out.println("Fine of " + fine + " has to be paid");

		}


		borr.setProcessed(true);

		System.out.println("\nBook returned successfully");
		System.out.println("----------------------------");
		System.out.println("Borrowing order processed:");
		System.out.println(borr.toString());
		System.out.println("----------------------------");

		System.out.println("\n Book returned");
		System.out.println();


	}
}
