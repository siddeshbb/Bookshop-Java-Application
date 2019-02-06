package com.visualabstractlabs.bookshop.dao;

import java.util.List;

import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.model.MembershipStatus;
import com.visualabstractlabs.bookshop.model.Order;
import com.visualabstractlabs.bookshop.model.User;
import com.visualabstractlabs.bookshop.reuse.exceptions.BookNotFoundException;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.exceptions.UserNotFoundException;

public interface BookShopDao {


	public User registerAnUserInBookShop(String name, String street, String city, String country,
			String phoneNumber);


	public List<Book> getAllTheBooksPresentInTheStore();


	public List<Order> getAllTheOrders();


	public List<Order> getAllTheOrdersForAUser(String userId) throws UserNotFoundException;

	public List<User> getAllTheUsers();

	public boolean checkIfBookIsAvailableToOrder(String bookId, int quantityToOrder)
			throws BookNotFoundException;


	public void placeAnOrder(String userId, String bookId, int quantityToOrder)
			throws BookNotFoundException, UserNotFoundException, CustomException;


	public boolean checkWhetherUserExists(String userId);

	public boolean checkWhetherBookExists(String bookId);


	public MembershipStatus getCurrentMembershipStatusOfUser(String userId);


	public void suggestNextMemberShipStatus(String userId);


	public double calculateDiscountPercentageBasedOnMembershipStatus(String userId);

}

