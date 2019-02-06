package com.visualabstractlabs.bookshop.dao.impl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import com.visualabstractlabs.bookshop.dao.BookShopDao;
import com.visualabstractlabs.bookshop.dao.LenderDao;
import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.repository.BookRepository;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LenderDaoImplTest {

	BookShop bookshop;

	LenderDao lenderDao = new LenderDaoImpl();
	BookShopDao bookShopDao = new BookShopDaoImpl();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() {
		bookshop = BookRepository.createBookDatabase();
	}

	@Test
	public void testA_testGetTheBooksLentByUser() throws UserNotFoundException, CustomException {

		Assert.assertEquals(0, lenderDao.getTheBooksLentByUser("1234").size());
	}

	@Test
	public void testB_testGetTheBooksLentByUser_WhenInvalidUserIdIsGiven()
			throws UserNotFoundException, CustomException {
		thrown.expect(UserNotFoundException.class);
		thrown.expectMessage("Invalid UserId provided.");

		lenderDao.getTheBooksLentByUser("invalid_userid");

	}

	@Test
	public void testC_testLendingANewBookToBookShop_WhenInvalidUserIdIsGiven()
			throws UserNotFoundException, CustomException {
		thrown.expect(UserNotFoundException.class);
		thrown.expectMessage("Invalid UserId provided.");

		Set<String> authors = new HashSet<String>();
		authors.add("J K Rowling");
		lenderDao.lendABook("invalid_userid", "Harry Potter", authors, 12, 100);
	}

	@Test
	public void testD_testLendingANewBookToBookShop()
			throws UserNotFoundException, CustomException {

		int noOfBooksBefore = bookshop.getBooks().size();

		Set<String> authors = new HashSet<String>();
		authors.add("J K Rowling");
		lenderDao.lendABook("1234", "Harry Potter", authors, 12, 100);

		int noOfBooksAfter = bookshop.getBooks().size();

		Assert.assertEquals(noOfBooksBefore + 1, noOfBooksAfter);
		Assert.assertEquals(1, lenderDao.getTheBooksLentByUser("1234").size());

	}

	@Test
	public void testE_testReturningBookToUserWhenLendingPeriodIsOver()
			throws UserNotFoundException, CustomException, BookNotFoundException {

		int noOfBooksBefore = bookshop.getBooks().size();

		// User 1234 lends the book to book shop
		Set<String> authors = new HashSet<String>();
		authors.add("J K Rowling");
		String userId =
				bookShopDao.registerAnUserInBookShop("Test", "street", "city", "country", "993030")
						.getUserID();
		lenderDao.lendABook(userId, "Harry Potter Chamber of Secrets", authors, 12, 100);

		int noOfBooksAfter = bookshop.getBooks().size();

		Assert.assertEquals(noOfBooksBefore + 1, noOfBooksAfter);

		// As Admin the book will be returned to user 1234 after lending period is over
		for (Book b : bookshop.getBooks()) {
			System.out.println(b.toString());
		}

		String lendedBookId =
				bookshop.getLendingInfo().stream().filter(l -> l.getUserId().equals(userId))
						.collect(Collectors.toList()).get(0).getLendedBookId();

		lenderDao.returnTheBookToLenderAfterLendingPeriodIsOver(lendedBookId,
				LocalDate.parse("2019-02-28"));


	}

	@After
	public void after() {
		bookshop.getLendingInfo().removeAll(bookshop.getLendingInfo());
		bookshop.getBorrowingInfo().removeAll(bookshop.getBorrowingInfo());
		bookshop.setBookShopEarnings(0.0);
	}


}
