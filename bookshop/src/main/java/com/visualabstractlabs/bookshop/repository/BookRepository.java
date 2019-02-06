package com.visualabstractlabs.bookshop.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.visualabstractlabs.bookshop.model.Address;
import com.visualabstractlabs.bookshop.model.Author;
import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.model.User;

public class BookRepository {



	public static BookShop createBookDatabase() {

		BookShop bookShop = BookShop.getInstance();

		// bookshop name
		bookShop.setName("TKS Group4 BookShop");


		// author 1
		Author javaAuthor = new Author("1234", "Bert Bates");

		// author 2
		Author pythonAuthor = new Author("5678", "John Doe");

		Set<Author> javaauthors = new HashSet<>();
		javaauthors.add(javaAuthor);

		Set<Author> pyauthors = new HashSet<>();
		pyauthors.add(pythonAuthor);


		// book1
		Book book1 = new Book("1234", "Java Programming", javaauthors, 400.0, 50, 0);
		book1.setAvialableToBorrow(false);


		// book2
		Book book2 = new Book("5678", "Python Programming", pyauthors, 300.0, 25, 0);
		book2.setAvialableToBorrow(false);

		// book2
		Book book3 = new Book("7373", "Basic Programming", pyauthors, 400.0, 25, 25);
		book3.setAvialableToBorrow(true);


		List<Book> books = new ArrayList<Book>();
		books.add(book1);
		books.add(book2);
		books.add(book3);
		bookShop.setBooks(books);

		// Address
		Address address = new Address("Koramangala", "Bangalore", "India");
		bookShop.setAddress(address);

		// User
		List<User> usersList = new ArrayList<User>();
		Address userAddress = new Address("Whitefield", "Bangalore", "India");
		User user = new User("1234", "Test", userAddress, "989898932", 0);
		usersList.add(user);
		bookShop.setUsers(usersList);


		return bookShop;


	}


}
