package com.visualabstractlabs.bookshop.model;

import java.util.Set;

public class Book {

	private String bookId;

	private String bookName;

	private Set<Author> authorIds;

	private double price;

	private int noOfCopies;

	private int noOfCopiesAvailableToBorrow;

	private boolean isAvialableToBorrow;

	public Book() {}

	public Book(String bookId, String bookName, Set<Author> authors, double price, int noOfCopies,
			int noOfCopiesAvailableToBorrow) {
		this.bookId = bookId;
		this.bookName = bookName;
		this.authorIds = authors;
		this.price = price;
		this.noOfCopies = noOfCopies;
		this.noOfCopiesAvailableToBorrow = noOfCopiesAvailableToBorrow;

	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}


	public Set<Author> getAuthorIds() {
		return authorIds;
	}

	public void setAuthorIds(Set<Author> authorIds) {
		this.authorIds = authorIds;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getNoOfCopies() {
		return noOfCopies;
	}

	public void setNoOfCopies(int noOfCopies) {
		this.noOfCopies = noOfCopies;
	}

	public int getNoOfCopiesAvailableToBorrow() {
		return noOfCopiesAvailableToBorrow;
	}

	public void setNoOfCopiesAvailableToBorrow(int noOfCopiesAvailableToBorrow) {
		this.noOfCopiesAvailableToBorrow = noOfCopiesAvailableToBorrow;
	}

	public boolean isAvialableToBorrow() {
		return isAvialableToBorrow;
	}

	public void setAvialableToBorrow(boolean isAvialableToBorrow) {
		this.isAvialableToBorrow = isAvialableToBorrow;
	}

	@Override
	public String toString() {

		if (price != 0.0) {
			return "Book [bookId=" + bookId + ", bookName=" + bookName + ", authorIds="
					+ authorIds.toString() + ", price=" + price + ", quantity=" + noOfCopies
					+ ", numberOfCopiesAvailableToBorrow=" + noOfCopiesAvailableToBorrow
					+ ", isAvailableToBorrow=" + isAvialableToBorrow + "]";
		} else {


			return "Book [bookId=" + bookId + ", bookName=" + bookName + ", authorIds="
					+ authorIds.toString() + ", quantity=" + noOfCopies
					+ ", numberOfCopiesAvailableToBorrow=" + noOfCopiesAvailableToBorrow
					+ ", isAvailableToBorrow=" + isAvialableToBorrow + "]";
		}
	}



}
