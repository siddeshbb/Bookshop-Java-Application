package com.visualabstractlabs.bookshop.dao.impl;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import com.visualabstractlabs.bookshop.dao.BookShopDao;
import com.visualabstractlabs.bookshop.dao.BorrowerDao;
import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.model.User;
import com.visualabstractlabs.bookshop.repository.BookRepository;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BorrowerDaoImplTest {

	BookShop bookshop;

	BorrowerDao borrowerDao = new BorrowerDaoImpl();

	BookShopDao bookShopDao = new BookShopDaoImpl();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() {
		bookshop = BookRepository.createBookDatabase();
	}

	@Test
	public void testA_getAllTheBooksPresentInTheStoreWhichAreAvailableToBorrow() {
		Assert.assertEquals(1,
				borrowerDao.getAllTheBooksPresentInTheStoreWhichAreAvailableToBorrow().size());
	}

	@Test
	public void testB_getTheBooksBorrowedByUser() throws UserNotFoundException {
		Assert.assertEquals(0, borrowerDao.getTheBooksBorrowedByUser("1234").size());
	}

	@Test
	public void testC_getTheBooksBorrowedByUser_WhenInvalidUserIdIsGiven()
			throws UserNotFoundException {

		thrown.expect(UserNotFoundException.class);
		thrown.expectMessage("Invalid UserId provided.");
		borrowerDao.getTheBooksBorrowedByUser("invalid_userid");
	}

	@Test
	public void testD_CheckIfBookIsAvailableToBorrow() throws BookNotFoundException {

		Assert.assertEquals(true, borrowerDao.checkIfBookIsAvailableToBorrow("7373"));
		Assert.assertEquals(false, borrowerDao.checkIfBookIsAvailableToBorrow("5678"));
	}

	@Test
	public void testE_CheckIfBookIsAvailableToBorrow_WhenInvalidBookIdIsGiven()
			throws BookNotFoundException {

		thrown.expect(BookNotFoundException.class);
		thrown.expectMessage("Invalid BookId provided.");
		borrowerDao.checkIfBookIsAvailableToBorrow("invalid_bookid");
	}

	@Test
	public void testF_BorrowABook_WithInvalidUserId()
			throws UserNotFoundException, BookNotFoundException, CustomException {
		thrown.expect(UserNotFoundException.class);
		thrown.expectMessage("Invalid UserId provided.");
		borrowerDao.borrowABook("invalid_userid", "7373", 12);

	}

	@Test
	public void testG_BorrowABook_WithInvalidBookId()
			throws UserNotFoundException, BookNotFoundException, CustomException {
		thrown.expect(BookNotFoundException.class);
		thrown.expectMessage("Invalid BookId provided.");

		borrowerDao.borrowABook("1234", "invalid_userid", 12);

	}

	@Test
	public void testH_ReturnABook_WithInvalidUserId()
			throws UserNotFoundException, BookNotFoundException, CustomException {
		thrown.expect(UserNotFoundException.class);
		thrown.expectMessage("Invalid UserId provided.");

		borrowerDao.returnBook("invalid_userid", "7373", LocalDate.parse("2019-02-25"));

	}


	@Test
	public void testJ_BorrowABook()
			throws UserNotFoundException, BookNotFoundException, CustomException {

		int borrowingInfoBeforeBorrow = bookshop.getBorrowingInfo().size();

		borrowerDao.borrowABook("1234", "7373", 12);

		int borrowingInfoBeforeAfter = bookshop.getBorrowingInfo().size();

		Assert.assertEquals(borrowingInfoBeforeBorrow + 1, borrowingInfoBeforeAfter);

		Assert.assertEquals(1, borrowerDao.getTheBooksBorrowedByUser("1234").size());

	}

	@Test
	public void testK_ReturnABook()
			throws UserNotFoundException, BookNotFoundException, CustomException {

		int borrowingInfoBeforeBorrow = bookshop.getBorrowingInfo().size();
		int quantityOfBookInBookShopBefore =
				bookshop.getBooks().stream().filter(b -> b.getBookId().equals("7373"))
						.collect(Collectors.toList()).get(0).getNoOfCopies();

		User user = bookShopDao.registerAnUserInBookShop("TestReturn", "street", "city", "country",
				"231231");

		borrowerDao.borrowABook(user.getUserID(), "7373", 12);

		int borrowingInfoBeforeAfter = bookshop.getBorrowingInfo().size();
		int quantityOfBookInBookShopAfter =
				bookshop.getBooks().stream().filter(b -> b.getBookId().equals("7373"))
						.collect(Collectors.toList()).get(0).getNoOfCopies();

		Assert.assertEquals(borrowingInfoBeforeBorrow + 1, borrowingInfoBeforeAfter);
		Assert.assertEquals(quantityOfBookInBookShopBefore - 1, quantityOfBookInBookShopAfter);


		// return the book
		borrowerDao.returnBook(user.getUserID(), "7373", LocalDate.parse("2019-02-25"));

		int quantityOfBookInBookShopAfterReturningBook =
				bookshop.getBooks().stream().filter(b -> b.getBookId().equals("7373"))
						.collect(Collectors.toList()).get(0).getNoOfCopies();
		Assert.assertEquals(quantityOfBookInBookShopBefore,
				quantityOfBookInBookShopAfterReturningBook);

	}


}
