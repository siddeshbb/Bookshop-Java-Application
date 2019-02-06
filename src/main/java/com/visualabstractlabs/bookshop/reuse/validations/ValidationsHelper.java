package com.visualabstractlabs.bookshop.reuse.validations;

import com.visualabstractlabs.bookshop.model.BookShop;

public class ValidationsHelper {


	public static boolean validateUserId(String userId) {

		BookShop bookShop = BookShop.getInstance();

		Long userCount = bookShop.getUsers().stream().filter(u -> u.getUserID().equals(userId))
				.distinct().count();

		if (userCount > 0) {
			return true;
		}

		return false;
	}

	public static boolean validateBookId(String bookId) {

		BookShop bookShop = BookShop.getInstance();

		Long bookCount = bookShop.getBooks().stream().filter(u -> u.getBookId().equals(bookId))
				.distinct().count();

		if (bookCount > 0) {
			return true;
		}

		return false;
	}

	public static boolean checkWhetherUserHasAlreadyBorrowedThisBook(String userId, String bookId) {

		BookShop bookShop = BookShop.getInstance();

		Long isAlreadyBorrowedCount = bookShop.getBorrowingInfo().stream()
				.filter(b -> (b.getBorrowedBook().getBookId().equals(bookId)
						&& b.getUserId().equals(userId)))
				.distinct().count();

		if (isAlreadyBorrowedCount > 0) {
			return true;
		}

		return false;
	}

	public static boolean checkWhetherUserHasAlreadyLendedThisBook(String userId, String bookId) {

		BookShop bookShop = BookShop.getInstance();

		Long isAlreadyLendedCount = bookShop.getLendingInfo().stream()
				.filter(b -> (b.getLendedBookId().equals(bookId) && b.getUserId().equals(userId)))
				.distinct().count();

		if (isAlreadyLendedCount > 0) {
			return true;
		}

		return false;
	}


}
