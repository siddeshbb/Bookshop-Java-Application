package com.visualabstractlabs.bookshop.dao.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.visualabstractlabs.bookshop.dao.BookShopDao;
import com.visualabstractlabs.bookshop.dao.LenderDao;
import com.visualabstractlabs.bookshop.model.Author;
import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.model.LendingInfo;
import com.visualabstractlabs.bookshop.model.User;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;
import com.visualabstractlabs.bookshop.reuse.validations.ValidationsHelper;
import com.visualabstractlabs.bookshop.utils.BookshopUtils;

public class LenderDaoImpl implements LenderDao {

	BookShopDao bookShopDao = new BookShopDaoImpl();

	@Override
	public void lendABook(String userId, String bookName, Set<String> authorNames,
			int lendingPeriodInDays, double lendingCharges)
			throws UserNotFoundException, CustomException {

		// Validate UserId
		if (!ValidationsHelper.validateUserId(userId)) {
			throw new UserNotFoundException("Invalid UserId provided.");
		}

		BookShop bookShop = BookShop.getInstance();

		String bookId = null;
		Book bookLended;

		// Check whether book already exists in book shop (same name and same authors)
		String existingBookId = getTheBookIdIfAlreadyExists(bookName, authorNames);

		if (existingBookId != null) {
			bookId = existingBookId;

			if (ValidationsHelper.checkWhetherUserHasAlreadyLendedThisBook(userId,
					existingBookId)) {
				throw new CustomException("You have already lended this book.");

			}

			bookShop.increaseQuantityOfBook(existingBookId, 1);
			bookLended =
					bookShop.getBooks().stream().filter(b -> b.getBookId().equals(existingBookId))
							.collect(Collectors.toList()).get(0);
			bookLended.setAvialableToBorrow(true);
			bookLended.setNoOfCopiesAvailableToBorrow(1);

		} else {

			// book id and author id
			bookId = BookshopUtils.getUUID();

			// author info
			Set<Author> authors = new HashSet<>();
			for (String authorName : authorNames) {
				String authorId = BookshopUtils.getUUID();
				Author author = new Author(authorId, authorName);
				authors.add(author);
			}


			bookLended = new Book(bookId, bookName, authors, 0, 1, 1);
			bookLended.setAvialableToBorrow(true);

			// add the book to book shop
			bookShop.addBook(bookLended, 1, 1);
		}



		LendingInfo lendingInfo =
				new LendingInfo(userId, bookId, lendingPeriodInDays, lendingCharges);
		lendingInfo.setLendedDate(LocalDate.now());

		if (bookShop.getLendingInfo() != null && bookShop.getLendingInfo().size() > 0) {
			bookShop.getLendingInfo().add(lendingInfo);
		} else {
			List<LendingInfo> lendingInfoList = new ArrayList<>();
			lendingInfoList.add(lendingInfo);
			bookShop.setLendingInfo(lendingInfoList);
		}



		System.out.println();
		System.out.println("Book successfully lended to Bookshop. Book Lended is:");
		System.out.println(bookLended.toString());


	}

	public String getTheBookIdIfAlreadyExists(String bookName, Set<String> authorNames) {

		List<Book> listOfBooksWithSameName = BookShop.getInstance().getBooks().stream()
				.filter(b -> b.getBookName().equals(bookName)).collect(Collectors.toList());

		String existingBookId = null;

		Set<Author> authors = new HashSet<Author>();
		for (String a : authorNames) {
			Author auth = new Author();
			auth.setAuthorName(a);
			authors.add(auth);
		}

		if (listOfBooksWithSameName.size() > 0) {

			// check whether authors are also same
			List<String> authorsOfBookToAdd = authors.stream()
					.sorted((a1, a2) -> a1.getAuthorName().compareTo(a2.getAuthorName()))
					.map(b -> new String(b.getAuthorName().trim())).collect(Collectors.toList());
			String authorNamesofBookToAddInSorted = String.join(",", authorsOfBookToAdd);

			for (Book bo : listOfBooksWithSameName) {

				List<String> authorsOfbo = bo.getAuthorIds().stream()
						.sorted((a1, a2) -> a1.getAuthorName().compareTo(a2.getAuthorName()))
						.map(b -> new String(b.getAuthorName().trim()))
						.collect(Collectors.toList());
				String authorsOfboSorted = String.join(",", authorsOfbo);

				if (authorsOfboSorted.equals(authorNamesofBookToAddInSorted)
						&& bo.getBookName().equals(bookName)) {
					existingBookId = bo.getBookId();
					break;
				}
			}
		}

		return existingBookId;
	}



	@Override
	public void returnTheBookToLenderAfterLendingPeriodIsOver(String bookId,
			LocalDate returningDate) throws BookNotFoundException {

		// Validate BookId
		if (!ValidationsHelper.validateBookId(bookId)) {
			throw new BookNotFoundException("Invalid BookId provided.");
		}

		BookShop bookShop = BookShop.getInstance();

		// validate bookId
		Long bookCount = bookShop.getBooks().stream().filter(u -> u.getBookId().equals(bookId))
				.distinct().count();

		if (bookCount < 0) {
			System.out.println("Invalid BookId entered");
			return;
		}

		// return the book to lender

		// is this book is lended by other user
		Long isBookLendedOneCount = bookShop.getLendingInfo().stream()
				.filter(b -> b.getLendedBookId().equals(bookId)).distinct().count();

		String userId;
		if (isBookLendedOneCount > 0) {
			userId = bookShop.getLendingInfo().stream()
					.filter(b -> b.getLendedBookId().equals(bookId)).collect(Collectors.toList())
					.get(0).getUserId();

		} else {

			System.out.println("This book is not lended by any user to return.");
			return;

		}


		// this book can be borrowed by other user - check that
		Long isBookBorrowedCount = bookShop.getBorrowingInfo().stream()
				.filter(b -> b.getBorrowedBook().getBookId().equals(bookId) && !b.isProcessed())
				.distinct().count();
		boolean isBookBorrowed = false;

		if (isBookBorrowedCount > 0) {
			isBookBorrowed = true;

		}

		if (isBookBorrowed) {
			System.out.println(" Book is currently borrowed by other user and cannot be returned.");
			return;
		}

		//
		LendingInfo lendingInfo = bookShop.getLendingInfo().stream()
				.filter(b -> b.getLendedBookId().equals(bookId) && b.getUserId().equals(userId))
				.collect(Collectors.toList()).get(0);

		LocalDate expectedDateToReturn = lendingInfo.getLendedDate()
				.plus(Period.ofDays(lendingInfo.getLendingPeriodInDays()));

		int expectedReturnToLender =
				Period.between(lendingInfo.getLendedDate(), expectedDateToReturn).getDays();
		int actualReturnToLender =
				Period.between(lendingInfo.getLendedDate(), returningDate).getDays();

		if (actualReturnToLender < expectedReturnToLender) {
			System.out.println(
					"Lending period is not over. Book cannot be returned before lending Period");
			return;
		}

		// 5% of lending charges will got to bookshop
		double bookShopCharges = 0.05 * lendingInfo.getLendingCharges();
		bookShop.setBookShopEarnings(bookShop.getBookShopEarnings() + bookShopCharges);
		System.out.println("Book Shop Commision charges:" + bookShopCharges);

		// add the lending charges to the user
		if (actualReturnToLender == expectedReturnToLender) {
			for (User user : bookShop.getUsers()) {
				if (user.getUserID().equals(userId)) {
					user.setEarnings(user.getEarnings()
							+ (lendingInfo.getLendingCharges() - bookShopCharges));

					System.out.println("Lender Earnings: "
							+ (lendingInfo.getLendingCharges() - bookShopCharges));
				}
			}
		} else if (actualReturnToLender > expectedReturnToLender) {

			// when actual return to lender is greater than expected then extra five rupees will be
			// paid each day which was collected by borrower while he was returning
			double extraChargesToBePaidToUser = (actualReturnToLender - expectedReturnToLender) * 5;
			for (User user : bookShop.getUsers()) {
				if (user.getUserID().equals(userId)) {
					user.setEarnings(user.getEarnings()
							+ ((lendingInfo.getLendingCharges() - bookShopCharges)
									+ extraChargesToBePaidToUser));

					System.out.println("Lender Earnings: "
							+ ((lendingInfo.getLendingCharges() - bookShopCharges)
									+ extraChargesToBePaidToUser));
				}
			}


		} else if (actualReturnToLender < expectedReturnToLender) {
			double daysBeforeActualReturn = expectedReturnToLender - actualReturnToLender;

			double eachDayCharge =
					(lendingInfo.getLendingCharges()) / lendingInfo.getLendingPeriodInDays();

			double lenderEarnings = (daysBeforeActualReturn * eachDayCharge) - bookShopCharges;

			for (User user : bookShop.getUsers()) {
				if (user.getUserID().equals(userId)) {
					user.setEarnings(user.getEarnings() + lenderEarnings);

					System.out.println("Lender Earnings: " + lenderEarnings);
				}
			}
		}

		// remove the book from bookshop
		Book bookToRemove = null;
		for (Book bookobj : bookShop.getBooks()) {
			if (bookobj.getBookId().equals(bookId)) {
				bookobj.setNoOfCopies(bookobj.getNoOfCopies() - 1);
				bookobj.setNoOfCopiesAvailableToBorrow(
						bookobj.getNoOfCopiesAvailableToBorrow() - 1);
				bookobj.setAvialableToBorrow(false);

				if (bookobj.getNoOfCopies() == 0 && bookobj.getNoOfCopiesAvailableToBorrow() == 0) {
					bookToRemove = bookobj;
				}
			}
		}
		if (bookToRemove != null) {
			bookShop.getBooks().remove(bookToRemove);
		}

		// remove the lending info
		LendingInfo lendToRemove = bookShop.getLendingInfo().stream()
				.filter(b -> b.getLendingId().equals(lendingInfo.getLendingId()))
				.collect(Collectors.toList()).get(0);
		bookShop.getLendingInfo().remove(lendToRemove);

		System.out.println("Book successfully returned to the Lender.");

	}



	@Override
	public List<Book> getTheBooksLentByUser(String userId) throws UserNotFoundException {

		// Validate UserId
		if (!ValidationsHelper.validateUserId(userId)) {
			throw new UserNotFoundException("Invalid UserId provided.");
		}

		BookShop bookShop = BookShop.getInstance();

		List<Book> bookList = new ArrayList<Book>();
		if (bookShop.getLendingInfo() != null && bookShop.getLendingInfo().size() > 0) {
			for (LendingInfo lendingInfo : bookShop.getLendingInfo()) {
				if (lendingInfo.getUserId().equals(userId)) {
					Book booksLent = bookShop.getBooks().stream()
							.filter(b -> b.getBookId().equals(lendingInfo.getLendedBookId()))
							.collect(Collectors.toList()).get(0);
					System.out.println(booksLent.toString());
					bookList.add(booksLent);
				}
			}
		}
		return bookList;

	}


}
