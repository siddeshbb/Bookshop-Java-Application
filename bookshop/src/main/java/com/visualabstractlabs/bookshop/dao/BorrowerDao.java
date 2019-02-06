package com.visualabstractlabs.bookshop.dao;

import java.time.LocalDate;
import java.util.List;

import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;

public interface BorrowerDao {

	public List<Book> getAllTheBooksPresentInTheStoreWhichAreAvailableToBorrow();

	public List<Book> getTheBooksBorrowedByUser(String userId) throws UserNotFoundException;

	public boolean checkIfBookIsAvailableToBorrow(String bookId) throws BookNotFoundException;

	public void borrowABook(String userId, String bookId, int noOfDaysToBorrow)
			throws UserNotFoundException, BookNotFoundException, CustomException;

	public void returnBook(String userId, String bookId, LocalDate returnedDate)
			throws UserNotFoundException, BookNotFoundException, CustomException;
}
