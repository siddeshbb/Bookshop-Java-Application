package com.visualabstractlabs.bookshop.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;

public interface LenderDao {


	public void lendABook(String userId, String bookName, Set<String> authorNames,
			int lendingPeriodInDays, double lendingCharges)
			throws UserNotFoundException, CustomException;

	public List<Book> getTheBooksLentByUser(String userId) throws UserNotFoundException;

	public void returnTheBookToLenderAfterLendingPeriodIsOver(String bookId,
			LocalDate returningDate) throws BookNotFoundException;


}
