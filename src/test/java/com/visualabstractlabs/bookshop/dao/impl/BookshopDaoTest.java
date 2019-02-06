package com.visualabstractlabs.bookshop.dao.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.model.MembershipStatus;
import com.visualabstractlabs.bookshop.model.User;
import com.visualabstractlabs.bookshop.repository.BookRepository;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookshopDaoTest {

	BookShop bookshop;

	BookShopDaoImpl bookShopDao = new BookShopDaoImpl();

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;


	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void before() {
		bookshop = BookRepository.createBookDatabase();

		System.setOut(new PrintStream(outContent));
	}

	@Test
	public void testA_RegisterAnUserInBookshop() {
		int numberOfUsersBefore = BookShop.getInstance().getUsers().size();

		User user = bookShopDao.registerAnUserInBookShop("New user", "street", "city", "country",
				"phoneNumber");

		int numberOfUsersAfter = BookShop.getInstance().getUsers().size();

		Assert.assertEquals(numberOfUsersBefore + 1, numberOfUsersAfter);

		// When new user is created he should be in Bronze membership status
		Assert.assertEquals(MembershipStatus.Bronze.toString(),
				user.getMembershipStatus().toString());

	}

	@Test
	public void testB_GetAllTheBooksPresentInTheStore() {
		Assert.assertEquals(3, bookShopDao.getAllTheBooksPresentInTheStore().size());

	}

	@Test
	public void testC_GetAllUsers() {
		Assert.assertEquals(1, bookShopDao.getAllTheUsers().size());

	}

	@Test
	public void testD_GetAllOrders() {
		Assert.assertEquals(0, bookShopDao.getAllTheOrders().size());

	}

	@Test
	public void testE_GetAllOrdersForUser() throws UserNotFoundException {

		Assert.assertEquals(0, bookShopDao.getAllTheOrdersForAUser("1234").size());

	}

	@Test
	public void testF_GetAllOrdersForUser_WhenInvalidUserIdIsGiven() throws UserNotFoundException {

		thrown.expect(UserNotFoundException.class);
		thrown.expectMessage("Invalid UserId provided.");

		bookShopDao.getAllTheOrdersForAUser("invalid_userid");
	}

	@Test
	public void testG_checkIfBookIsAvailableToOrder() throws BookNotFoundException {
		Assert.assertEquals(true, bookShopDao.checkIfBookIsAvailableToOrder("1234", 1));
	}

	@Test
	public void testH_checkIfBookIsAvailableToOrder_whenInvalidBookIdIsGiven()
			throws BookNotFoundException {
		thrown.expect(BookNotFoundException.class);
		thrown.expectMessage("Invalid BookId provided.");

		Assert.assertEquals(true, bookShopDao.checkIfBookIsAvailableToOrder("invalid_bookid", 1));
	}


	@Test
	public void testI_PlaceAnOrder()
			throws BookNotFoundException, UserNotFoundException, CustomException {

		int orderCountBefore = bookShopDao.getAllTheOrders().size();

		bookShopDao.placeAnOrder("1234", "1234", 1);

		int orderCountAfter = bookShopDao.getAllTheOrders().size();

		Assert.assertEquals(orderCountBefore + 1, orderCountAfter);

		Assert.assertEquals(1, bookShopDao.getAllTheOrders().size());

		Assert.assertEquals(1, bookShopDao.getAllTheOrdersForAUser("1234").size());

	}

	@Test
	public void testJ_PlaceAnOrder_WhenInvalidUserIdIsGiven()
			throws BookNotFoundException, UserNotFoundException, CustomException {

		thrown.expect(UserNotFoundException.class);
		thrown.expectMessage("Invalid UserId provided.");

		bookShopDao.placeAnOrder("invalid_userid", "1234", 1);
	}

	@Test
	public void testK_PlaceAnOrder_WhenInvalidBookIdIsGiven()
			throws BookNotFoundException, UserNotFoundException, CustomException {

		thrown.expect(BookNotFoundException.class);
		thrown.expectMessage("Invalid BookId provided.");

		bookShopDao.placeAnOrder("1234", "invalid_bookid", 1);
	}

	@Test
	public void testL_PlaceAnOrder_WhenQuantityToOrderIsLessThanZero()
			throws BookNotFoundException, UserNotFoundException, CustomException {

		thrown.expect(CustomException.class);
		thrown.expectMessage("Quantity should be greater than 0.");

		bookShopDao.placeAnOrder("1234", "1234", 0);
	}

	@Test
	public void testM_PlaceAnOrder_WhenBookIsNotAvailableToOrder()
			throws BookNotFoundException, UserNotFoundException, CustomException {

		thrown.expect(CustomException.class);
		thrown.expectMessage(
				"Order cannot be placed as there is no sufficient quantity of books or Book is not available to Order.");

		bookShopDao.placeAnOrder("1234", "7373", 1);
	}

	@Test
	public void testN_PlaceAnOrder_WhenBookIsAvailableToOrder_ButNotSufficientQuantity()
			throws BookNotFoundException, UserNotFoundException, CustomException {

		thrown.expect(CustomException.class);
		thrown.expectMessage(
				"Order cannot be placed as there is no sufficient quantity of books or Book is not available to Order.");

		bookShopDao.placeAnOrder("1234", "7373", 100);
	}


	@Test
	public void testO_GetCurrentMembershipStatusOfUser() {
		MembershipStatus membershipStatus = bookShopDao.getCurrentMembershipStatusOfUser("1234");
		Assert.assertEquals(MembershipStatus.Bronze.toString(), membershipStatus.toString());
	}


	@Test
	public void testP_CalculateDiscountPercentageBasedOnMembershipStatus() {
		double discount = bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus("1234");
		assertEquals(0.0, discount, 0.0);
	}

	@Test
	public void testQ_WhenUserOrdersMoreThan1000_HeShouldBeMovedToSilverMembership()
			throws BookNotFoundException, UserNotFoundException, CustomException {
		int numberOfUsersBefore = BookShop.getInstance().getUsers().size();

		User user = bookShopDao.registerAnUserInBookShop("New user", "street", "city", "country",
				"phoneNumber");

		int numberOfUsersAfter = BookShop.getInstance().getUsers().size();

		Assert.assertEquals(numberOfUsersBefore + 1, numberOfUsersAfter);

		// When new user is created he should be in Bronze membership status
		Assert.assertEquals(MembershipStatus.Bronze.toString(),
				user.getMembershipStatus().toString());

		// place an order for more than 1000 rs
		bookShopDao.placeAnOrder(user.getUserID(), "1234", 6);

		Assert.assertEquals(MembershipStatus.Silver.toString(),
				bookShopDao.getCurrentMembershipStatusOfUser(user.getUserID()).toString());

		// as he is in Silver membership status now discount should be 5%
		Assert.assertEquals(0.05,
				bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus(user.getUserID()),
				0.0);

	}

	@Test
	public void testR_CalculateDiscountPercentageBasedOnMembershipStatusForDifferentMembershipStatus() {

		User user = bookShopDao.registerAnUserInBookShop("New user", "street", "city", "country",
				"32131");
		String userId = user.getUserID();

		// initially he will be in Bronze So 0% discount
		assertEquals(0.0, bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus(userId),
				0.0);

		// set to silver - 5%
		user.setMembershipStatus(MembershipStatus.Silver);
		assertEquals(0.05, bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus(userId),
				0.0);

		// set to gold - 10%
		user.setMembershipStatus(MembershipStatus.Gold);
		assertEquals(0.1, bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus(userId),
				0.0);

		// set to platinum - 20%
		user.setMembershipStatus(MembershipStatus.Platinum);
		assertEquals(0.2, bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus(userId),
				0.0);
	}

	@Test
	public void testS_MovingTheUserToNextMemberShipStatusBasedOnTotalSpend()
			throws BookNotFoundException, UserNotFoundException, CustomException {

		User user = bookShopDao.registerAnUserInBookShop("New user", "street", "city", "country",
				"32131");
		String userId = user.getUserID();

		// initially he will be in Bronze So 0% discount
		assertEquals(0.0, bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus(userId),
				0.0);
		bookShopDao.suggestNextMemberShipStatus(userId);

		assertEquals(
				"Your Current MemberShip Status is: Bronze\nYou can spend 1000.0 more to move to Silver Membership status.\n",
				outContent.toString());

		// place an order for 6 books -> 400*6 =2400
		bookShopDao.placeAnOrder(userId, "1234", 6);

		// check the membership status now, it should be Silver
		assertEquals(MembershipStatus.Silver.toString(),
				bookShopDao.getCurrentMembershipStatusOfUser(userId).toString());

		// now user should get 5% discount as he is in Silver
		assertEquals(0.05, bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus(userId),
				0.0);
		bookShopDao.suggestNextMemberShipStatus(userId);

		// Now place an order for 10 more books
		bookShopDao.placeAnOrder(userId, "1234", 10);

		// check the membership status now, it should be Gold
		assertEquals(MembershipStatus.Gold.toString(),
				bookShopDao.getCurrentMembershipStatusOfUser(userId).toString());

		// now user should get 10% discount as he is in Silver
		assertEquals(0.1, bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus(userId),
				0.0);
		bookShopDao.suggestNextMemberShipStatus(userId);


		// Now place an order for 13 more books
		bookShopDao.placeAnOrder(userId, "1234", 13);

		// check the membership status now, it should be Platinum
		assertEquals(MembershipStatus.Platinum.toString(),
				bookShopDao.getCurrentMembershipStatusOfUser(userId).toString());

		// now user should get 20% discount as he is in Silver
		assertEquals(0.2, bookShopDao.calculateDiscountPercentageBasedOnMembershipStatus(userId),
				0.0);
		bookShopDao.suggestNextMemberShipStatus(userId);


	}

	@Test
	public void testT_TestCheckWhetherBookExistsMethod() {
		Assert.assertEquals(true, bookShopDao.checkWhetherBookExists("1234"));
	}

	@Test
	public void testU_TestCheckWhetherUserExistsMethod() {
		Assert.assertEquals(true, bookShopDao.checkWhetherUserExists("1234"));
	}

	@Test
	public void testV_TestCheckWhetherBookExistsMethod_WithInvalidBookId() {
		Assert.assertEquals(false, bookShopDao.checkWhetherBookExists("1111"));
	}

	@Test
	public void testW_TestCheckWhetherUserExistsMethod_WithInvalidUserId() {
		Assert.assertEquals(false, bookShopDao.checkWhetherUserExists("1111"));
	}

	@After
	public void restoreStreams() {
		System.setOut(originalOut);
	}
}
