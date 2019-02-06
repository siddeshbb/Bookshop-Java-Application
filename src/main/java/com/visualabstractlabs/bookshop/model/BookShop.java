package com.visualabstractlabs.bookshop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.reuse.validations.ValidationsHelper;

public class BookShop {

	private String name;

	private Address address;

	private List<Book> books;

	private List<User> users;

	private List<Order> orders;

	private List<BorrowingInfo> borrowingInfo;

	private List<LendingInfo> lendingInfo;

	private double bookShopEarnings;

	private BookShop() {
		this.books = new ArrayList<Book>();
		this.users = new ArrayList<User>();
		this.orders = new ArrayList<Order>();
		this.borrowingInfo = new ArrayList<BorrowingInfo>();
		this.lendingInfo = new ArrayList<LendingInfo>();
	}

	private static BookShop bookshop;


	public static BookShop getInstance() {
		if (bookshop == null) {
			bookshop = new BookShop();
		}
		return bookshop;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<BorrowingInfo> getBorrowingInfo() {
		return borrowingInfo;
	}

	public void setBorrowingInfo(List<BorrowingInfo> borrowingInfo) {
		this.borrowingInfo = borrowingInfo;
	}

	public List<LendingInfo> getLendingInfo() {
		return lendingInfo;
	}

	public void setLendingInfo(List<LendingInfo> lendingInfo) {
		this.lendingInfo = lendingInfo;
	}

	public double getBookShopEarnings() {
		return bookShopEarnings;
	}

	public void setBookShopEarnings(double bookShopEarnings) {
		this.bookShopEarnings = bookShopEarnings;
	}


	/**
	 * 
	 * Search Books by BookName
	 * 
	 * @param bookName
	 * @return
	 */
	public List<Book> getListOfBooksByName(String bookName) {

		return this.books.stream().filter(book -> book.getBookName().trim().toUpperCase()
				.contains(bookName.trim().toUpperCase())).collect(Collectors.toList());

	}

	/**
	 * 
	 * Search Books by Author
	 * 
	 * @param bookName
	 * @return
	 */
	public List<Book> getListOfBooksByAuthor(String authorName) {
		List<Book> resultSet = new ArrayList<Book>();
		for (Book book : this.books) {
			Set<Author> authors = book.getAuthorIds();
			for (Author author : authors) {
				if (author.getAuthorName().trim().toUpperCase()
						.contains(authorName.trim().toUpperCase())) {
					resultSet.add(book);
				}
			}
		}
		return resultSet;
	}


	/**
	 * 
	 * Search Books by BookName
	 * 
	 * @param bookName
	 * @return
	 */
	public List<Book> getListOfBooksByPriceRange(double from, double to) {
		List<Book> resultSet = new ArrayList<Book>();


		for (Book book : this.books) {
			if (book.getPrice() >= from && book.getPrice() <= to) {
				resultSet.add(book);
			}
		}
		return resultSet;

	}


	/**
	 * Add a book to bookshop, if book already exists then increase the quantity else add it as new
	 * book
	 * 
	 * @param book
	 * @param quantity
	 */
	public void addBook(Book bookToAdd, int quantity, int quantityAvailableToBorrow)
			throws CustomException {
		if (bookToAdd == null) {
			throw new CustomException("Book cannot be null.");
		}
		if (bookToAdd.getBookId() == null) {
			throw new CustomException("Book Id cannot be null.");
		}
		if (quantity <= 0) {

			throw new CustomException("Quantity should be greater than 0.");

		}

		// check whether there already exists with same book name

		List<Book> listOfBooksWithSameName =
				books.stream().filter(b -> b.getBookName().equals(bookToAdd.getBookName()))
						.collect(Collectors.toList());
		if (listOfBooksWithSameName.size() > 0) {

			// check whether authors are also same
			List<String> authorsOfBookToAdd = bookToAdd.getAuthorIds().stream()
					.sorted((a1, a2) -> a1.getAuthorName().compareTo(a2.getAuthorName()))
					.map(b -> new String(b.getAuthorName().trim())).collect(Collectors.toList());
			String authorNamesofBookToAddInSorted = String.join(",", authorsOfBookToAdd);


			boolean bookExists = false;
			String existingBookId = null;
			for (Book bo : listOfBooksWithSameName) {

				List<String> authorsOfbo = bo.getAuthorIds().stream()
						.sorted((a1, a2) -> a1.getAuthorName().compareTo(a2.getAuthorName()))
						.map(b -> new String(b.getAuthorName().trim()))
						.collect(Collectors.toList());
				String authorsOfboSorted = String.join(",", authorsOfbo);

				if (authorsOfboSorted.equals(authorNamesofBookToAddInSorted)
						&& bo.getBookName().equals(bookToAdd.getBookName())) {
					bookExists = true;
					existingBookId = bo.getBookId();
					break;
				}
			}

			if (bookExists) {
				for (Book b : books) {
					if (b.getBookId().equals(existingBookId)) {
						b.setNoOfCopies(b.getNoOfCopies() + quantity);
						b.setNoOfCopiesAvailableToBorrow(
								b.getNoOfCopiesAvailableToBorrow() + quantityAvailableToBorrow);
					}
				}

			} else {
				bookToAdd.setNoOfCopies(quantity);
				bookToAdd.setNoOfCopiesAvailableToBorrow(quantityAvailableToBorrow);
				this.getBooks().add(bookToAdd);
			}


		} else {
			bookToAdd.setNoOfCopies(quantity);
			bookToAdd.setNoOfCopiesAvailableToBorrow(quantityAvailableToBorrow);
			this.getBooks().add(bookToAdd);

		}
	}

	public void increaseQuantityOfBook(String bookId, int quantity) {

		// Validate BookId
		if (!ValidationsHelper.validateBookId(bookId)) {
			System.out.println("Invalid BookId provided.");
			return;
		}


		for (Book b : books) {
			if (b.getBookId().equals(bookId)) {
				b.setNoOfCopies(b.getNoOfCopies() + quantity);
			}
		}

	}

	public void removeBook(String bookId, int quantityToRemove) {

		if (bookId == null) {
			System.out.println("Book Id cannot be null");
			return;
		}
		if (quantityToRemove <= 0) {
			System.out.println("Quantity should be greater than 0.");
			return;
		}

		Long doesBookExistsCount =
				books.stream().filter(b -> b.getBookId().equals(bookId)).distinct().count();
		if (doesBookExistsCount > 0) {

			int quantitiesLeft = 0;

			for (Book b : books) {
				if (b.getBookId().equals(bookId)) {

					if (quantityToRemove > b.getNoOfCopies()) {
						System.out.println("Invalid Quantity entered to remove. Book shop has only "
								+ b.getNoOfCopies() + " copies.");
						return;
					}

					quantitiesLeft = b.getNoOfCopies() - quantityToRemove;
					b.setNoOfCopies(quantitiesLeft);

				}
			}

			if (quantitiesLeft == 0) {
				Book bookToRemove = books.stream().filter(b -> b.getBookId().equals(bookId))
						.collect(Collectors.toList()).get(0);
				books.remove(bookToRemove);
			}

		} else {
			System.out.println("Book does not exists");
		}

	}
}
