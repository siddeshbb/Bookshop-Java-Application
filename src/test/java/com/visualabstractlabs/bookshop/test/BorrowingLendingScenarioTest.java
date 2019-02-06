package com.visualabstractlabs.bookshop.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.Period;
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
import com.visualabstractlabs.bookshop.dao.BorrowerDao;
import com.visualabstractlabs.bookshop.dao.LenderDao;
import com.visualabstractlabs.bookshop.dao.impl.BookShopDaoImpl;
import com.visualabstractlabs.bookshop.dao.impl.BorrowerDaoImpl;
import com.visualabstractlabs.bookshop.dao.impl.LenderDaoImpl;
import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.model.BorrowingInfo;
import com.visualabstractlabs.bookshop.model.User;
import com.visualabstractlabs.bookshop.repository.BookRepository;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BorrowingLendingScenarioTest {


	BookShop bookshop;

	BorrowerDao borrowerDao = new BorrowerDaoImpl();

	LenderDao lenderDao = new LenderDaoImpl();

	BookShopDao bookShopDao = new BookShopDaoImpl();

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void before() {
		bookshop = BookRepository.createBookDatabase();

		System.setOut(new PrintStream(outContent));

	}

	/**
	 * 
	 * Test the normal borrowing and lending scenario
	 * 
	 * User u1 lends the book b1 from book shop
	 * 
	 * User u2 borrows the same book b1 book shop
	 * 
	 * @throws CustomException
	 * @throws UserNotFoundException
	 * @throws BookNotFoundException
	 * 
	 * 
	 */
	@Test
	public void testAa_TestNormalBorrowingLendingScenario()
			throws UserNotFoundException, CustomException, BookNotFoundException {

		// User 1 registers and lends a book
		User lender = bookShopDao.registerAnUserInBookShop("Lender", "Whitefield", "Bangalore",
				"India", "12345");

		Set<String> authorNames = new HashSet<>();
		authorNames.add(new String("Suresh Kumar"));
		lenderDao.lendABook(lender.getUserID(), "In Quest of Honesty", authorNames, 12, 100.0);

		Assert.assertEquals(1, BookShop.getInstance().getLendingInfo().size());

		String bookId = bookshop.getLendingInfo().stream()
				.filter(l -> l.getUserId().equals(lender.getUserID())).collect(Collectors.toList())
				.get(0).getLendedBookId();

		// User 2 registers and borrows the same book
		User borrower = bookShopDao.registerAnUserInBookShop("Borrower", "Whitefield", "Bangalore",
				"India", "939939");

		borrowerDao.borrowABook(borrower.getUserID(), bookId, 12);

		Assert.assertEquals(1, bookshop.getBorrowingInfo().size());

		// After 13 days User 2 returns the book to book shop
		borrowerDao.returnBook(borrower.getUserID(), bookId,
				LocalDate.now().plus(Period.ofDays(13)));

		// As its 1 day late , a fine of Rs.5 should be paid
		BorrowingInfo borrowingInfo = bookshop.getBorrowingInfo().stream()
				.filter(l -> l.getUserId().equals(borrower.getUserID()))
				.collect(Collectors.toList()).get(0);
		Assert.assertEquals(5.0, borrowingInfo.getFinePaid(), 0.0);

		// Borrowing charges will be -> 100(lending charges for 12 days) + 5 * 1(fine) + 1*13 (Rs.1
		// each day for bookshop = 118
		Assert.assertEquals(118.0, borrowingInfo.getBorrowingCharges(), 0.0);

		// For each day bookshop will get Rs.1
		Assert.assertEquals(13.0, bookshop.getBookShopEarnings(), 0.0);

		// After that on 13th day the Admin returns the book back to lender
		lenderDao.returnTheBookToLenderAfterLendingPeriodIsOver(bookId,
				LocalDate.now().plus(Period.ofDays(13)));

		// Lender will get -> 100(lending charges) - 5( 5% book shop commision charges) + 1*5 (fine,
		// as 1 day late) = 105
		Assert.assertEquals(105.0,
				bookshop.getUsers().stream().filter(u -> u.getUserID().equals(lender.getUserID()))
						.collect(Collectors.toList()).get(0).getEarnings(),
				0.0);

		// Book shop earnings will be Rs.5 more which is 13+5 = 18
		Assert.assertEquals(18.0, bookshop.getBookShopEarnings(), 0.0);



	}


	@After
	public void restoreStreams() {
		System.setOut(originalOut);
	}

}
