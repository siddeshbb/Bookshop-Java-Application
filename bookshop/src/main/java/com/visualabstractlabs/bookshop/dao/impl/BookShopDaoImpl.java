package com.visualabstractlabs.bookshop.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.visualabstractlabs.bookshop.dao.BookShopDao;
import com.visualabstractlabs.bookshop.model.Address;
import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.model.MembershipStatus;
import com.visualabstractlabs.bookshop.model.Order;
import com.visualabstractlabs.bookshop.model.User;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;
import com.visualabstractlabs.bookshop.reuse.validations.ValidationsHelper;
import com.visualabstractlabs.bookshop.utils.BookshopUtils;

public class BookShopDaoImpl implements BookShopDao {


	@Override
	public User registerAnUserInBookShop(String name, String street, String city, String country,
			String phoneNumber) {

		BookShop bookShop = BookShop.getInstance();
		User user = new User();
		user.setName(name);
		Address address = new Address(street, city, country);
		user.setAddress(address);
		user.setPhoneNumber(phoneNumber);
		user.setTotalSpend(0);
		bookShop.getUsers().add(user);

		return user;

	}

	/**
	 * Method to display all the books in Book store
	 */
	@Override
	public List<Book> getAllTheBooksPresentInTheStore() {

		BookShop bookShop = BookShop.getInstance();

		List<Book> bookList = new ArrayList<Book>();
		if (bookShop.getBooks() != null && bookShop.getBooks().size() > 0) {
			for (Book book : bookShop.getBooks()) {
				System.out.println(book.toString());
				bookList.add(book);
			}
		} else {
			System.out.println("No Books present");
		}
		return bookList;
	}

	@Override
	public List<Order> getAllTheOrders() {

		BookShop bookShop = BookShop.getInstance();

		List<Order> orderList = new ArrayList<Order>();
		if (bookShop.getOrders() != null && bookShop.getOrders().size() > 0) {
			for (Order order : bookShop.getOrders()) {

				System.out.println(order.toString());
				orderList.add(order);
			}
		} else {
			System.out.println("No orders present");
		}
		return orderList;
	}

	@Override
	public List<Order> getAllTheOrdersForAUser(String userId) throws UserNotFoundException {

		if (!ValidationsHelper.validateUserId(userId)) {
			throw new UserNotFoundException("Invalid UserId provided.");
		}

		BookShop bookShop = BookShop.getInstance();

		List<Order> orderList = new ArrayList<Order>();
		if (bookShop.getOrders() != null && bookShop.getOrders().size() > 0) {
			for (Order order : bookShop.getOrders()) {

				if (order.getUserID().equals(userId)) {
					System.out.println(order.toString());
					orderList.add(order);
				}
			}
		} else {
			System.out.println("No orders present");
		}
		return orderList;
	}

	@Override
	public List<User> getAllTheUsers() {

		BookShop bookShop = BookShop.getInstance();

		List<User> userList = new ArrayList<User>();
		if (bookShop.getUsers() != null && bookShop.getUsers().size() > 0) {
			for (User user : bookShop.getUsers()) {
				System.out.println(user.toString());
				userList.add(user);
			}
		} else {
			System.out.println("No Users present");
		}
		return userList;
	}


	/**
	 * 
	 * Method to check if book is available to Order
	 * 
	 * @param bookId
	 * @param quantityToOrder
	 * @return
	 * @throws BookNotFoundException
	 */
	@Override
	public boolean checkIfBookIsAvailableToOrder(String bookId, int quantityToOrder)
			throws BookNotFoundException {

		if (!ValidationsHelper.validateBookId(bookId)) {
			throw new BookNotFoundException("Invalid BookId provided.");
		}

		BookShop bookShop = BookShop.getInstance();

		List<Book> book = bookShop.getBooks().stream().filter(b -> b.getBookId().equals(bookId))
				.collect(Collectors.toList());

		for (Book bookobj : book) {
			if (bookobj.getBookId().equals(bookId) && quantityToOrder <= bookobj.getNoOfCopies()
					&& !bookobj.isAvialableToBorrow()) {

				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * Place an Order
	 * 
	 * @param userId
	 * @param bookId
	 * @param quantityToOrder
	 * @throws BookNotFoundException
	 */
	@Override
	public void placeAnOrder(String userId, String bookId, int quantityToOrder)
			throws BookNotFoundException, UserNotFoundException, CustomException {

		// Validate UserId
		if (!ValidationsHelper.validateUserId(userId)) {
			throw new UserNotFoundException("Invalid UserId provided.");
		}

		// Validate BookId
		if (!ValidationsHelper.validateBookId(bookId)) {
			throw new BookNotFoundException("Invalid BookId provided.");
		}

		// Quantity to Order should be greater than 0
		if (quantityToOrder <= 0) {
			throw new CustomException("Quantity should be greater than 0.");
		}

		BookShop bookShop = BookShop.getInstance();

		if (checkIfBookIsAvailableToOrder(bookId, quantityToOrder)) {

			// Place an order
			Order order = new Order();
			order.setBookID(bookId);
			order.setUserID(userId);
			order.setQuantity(quantityToOrder);

			// Calculate the discount percentage based on membership status
			double discountPercentage = calculateDiscountPercentageBasedOnMembershipStatus(userId);

			List<Book> book = bookShop.getBooks().stream().filter(b -> b.getBookId().equals(bookId))
					.collect(Collectors.toList());
			double amountSpent = (book.get(0).getPrice() * quantityToOrder)
					- ((discountPercentage) * (book.get(0).getPrice() * quantityToOrder));

			order.setAmount(amountSpent);

			order.setOrderID(BookshopUtils.getUUID());

			if (bookShop.getOrders() != null && bookShop.getOrders().size() > 0) {
				bookShop.getOrders().add(order);
			} else {
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(order);
				bookShop.setOrders(orderList);
			}

			// Decrement the quantity of books in Bookshop
			int bookStockLeftInBookShop = 0;
			for (Book bookobj : book) {
				if (bookobj.getBookId().equals(bookId)) {
					bookStockLeftInBookShop = bookobj.getNoOfCopies() - quantityToOrder;
					bookobj.setNoOfCopies(bookStockLeftInBookShop);
				}
			}

			// if stock of the book is 0 then remove the book details from bookshop
			if (bookStockLeftInBookShop == 0) {
				bookShop.getBooks().remove(book.get(0));
			}

			MembershipStatus currentMembershipStatus;
			// Update the total spend of the User and the Membership status of user
			for (User user : bookShop.getUsers()) {
				if (user.getUserID().equals(userId)) {
					user.setTotalSpend(user.getTotalSpend() + amountSpent);
					currentMembershipStatus = getCurrentMembershipStatusOfUser(userId);

					if (currentMembershipStatus == MembershipStatus.Bronze
							&& user.getTotalSpend() >= 1000 && user.getTotalSpend() < 5000) {
						user.setMembershipStatus(MembershipStatus.Silver);

					} else if (currentMembershipStatus == MembershipStatus.Silver
							&& user.getTotalSpend() >= 5000 && user.getTotalSpend() <= 10000) {
						user.setMembershipStatus(MembershipStatus.Gold);

					} else if (currentMembershipStatus == MembershipStatus.Gold
							&& user.getTotalSpend() > 10000) {
						user.setMembershipStatus(MembershipStatus.Platinum);

					}

				}
			}

			// add amount to bookshop earnings
			bookShop.setBookShopEarnings(bookShop.getBookShopEarnings() + amountSpent);

			// Print the order
			System.out.println("Order Placed:");
			System.out.println("-----------------");
			System.out.println(order.toString());
			System.out.println("-----------------");


		} else {
			throw new CustomException(
					"Order cannot be placed as there is no sufficient quantity of books or Book is not available to Order.");

		}
	}

	/**
	 * 
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public boolean checkWhetherUserExists(String userId) {

		BookShop bookShop = BookShop.getInstance();

		Long userCount = bookShop.getUsers().stream().filter(u -> u.getUserID().equals(userId))
				.distinct().count();

		if (userCount > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean checkWhetherBookExists(String bookId) {

		BookShop bookShop = BookShop.getInstance();

		Long bookCount = bookShop.getBooks().stream().filter(u -> u.getBookId().equals(bookId))
				.distinct().count();

		if (bookCount > 0) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 * Method to get the current member ship status of the user
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public MembershipStatus getCurrentMembershipStatusOfUser(String userId) {

		BookShop bookShop = BookShop.getInstance();

		if (bookShop.getUsers() != null && bookShop.getUsers().size() > 0) {
			for (User user : bookShop.getUsers()) {
				if (user.getUserID().equals(userId)) {
					return user.getMembershipStatus();
				}
			}
		}
		return null;

	}

	/**
	 * 
	 * Method to suggest the next membership status for the user
	 * 
	 * @param userId
	 */
	@Override
	public void suggestNextMemberShipStatus(String userId) {

		BookShop bookShop = BookShop.getInstance();

		MembershipStatus currentMemberShipStatus = null;
		double totalSpend = 0.0;
		if (bookShop.getUsers() != null && bookShop.getUsers().size() > 0) {
			for (User user : bookShop.getUsers()) {
				if (user.getUserID().equals(userId)) {
					currentMemberShipStatus = user.getMembershipStatus();
					totalSpend = user.getTotalSpend();
				}
			}
		}

		System.out.println(
				"Your Current MemberShip Status is: " + currentMemberShipStatus.toString());

		if (currentMemberShipStatus == MembershipStatus.Bronze && totalSpend <= 1000
				&& (1000 - totalSpend) != 0.0) {

			System.out.println("You can spend " + (1000 - totalSpend)
					+ " more to move to Silver Membership status.");

		} else if (currentMemberShipStatus == MembershipStatus.Silver && totalSpend <= 5000
				&& (5000 - totalSpend) != 0.0) {

			System.out.println("You can spend " + (5000 - totalSpend)
					+ " more to move to Gold Membership status.");

		} else if (currentMemberShipStatus == MembershipStatus.Gold && totalSpend < 10000) {

			System.out.println("You can spend " + (10000 - totalSpend)
					+ " more to move to Platinum Membership status.");

		} else if (currentMemberShipStatus == MembershipStatus.Platinum) {

			System.out.println(
					"Congratulations. You  already get the best discounted price as a Platinum member.");

		}


	}

	/**
	 * 
	 * Method to calculate the discount percentage based on current membership status of the user
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public double calculateDiscountPercentageBasedOnMembershipStatus(String userId) {
		MembershipStatus currentMemberShipStatus = getCurrentMembershipStatusOfUser(userId);

		switch (currentMemberShipStatus) {
			case Bronze:
				return 0.0;
			case Silver:
				System.out.println("Congratulations. As you are "
						+ currentMemberShipStatus.toString() + " , You are getting 5% discount.");
				return 0.05;
			case Gold:
				System.out.println("Congratulations. As you are "
						+ currentMemberShipStatus.toString() + " , You are getting 10% discount.");
				return 0.1;
			case Platinum:
				System.out.println("Congratulations. As you are "
						+ currentMemberShipStatus.toString() + " , You are getting 20% discount.");
				return 0.2;
			default:
				return 0.0;
		}
	}

}
