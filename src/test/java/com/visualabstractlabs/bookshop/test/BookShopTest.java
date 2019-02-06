package com.visualabstractlabs.bookshop.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

import com.visualabstractlabs.bookshop.model.Author;
import com.visualabstractlabs.bookshop.model.Book;
import com.visualabstractlabs.bookshop.model.BookShop;
import com.visualabstractlabs.bookshop.repository.BookRepository;
import com.visualabstractlabs.bookshop.reuse.exceptions.CustomException;
import com.visualabstractlabs.bookshop.utils.BookshopUtils;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookShopTest {

	BookShop bookshop;

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
	public void testA_SearchBookByBookName() {

		Assert.assertEquals(1, bookshop.getListOfBooksByName("Java").size());
	}


	@Test
	public void testB_SearchBookByAuthorName() {

		Assert.assertEquals(1, bookshop.getListOfBooksByAuthor("Bert").size());
	}

	@Test
	public void testC_SearchBookByPriceRange() {

		Assert.assertEquals(3, bookshop.getListOfBooksByPriceRange(150, 500).size());
	}

	@Test
	public void testD_AddingNewBookToBookShop() throws CustomException {

		int noOfBooksBeforeAdding = bookshop.getBooks().size();

		String bookIdToAdd = BookshopUtils.getUUID();
		Set<Author> al = new HashSet<>();
		Author aa = new Author(BookshopUtils.getUUID(), "J.K Rowling");
		al.add(aa);
		Book b = new Book(bookIdToAdd, "Harry Potter", al, 500.0, 2, 0);
		bookshop.addBook(b, 2, 2);

		int noOfBooksAfterAdding = bookshop.getBooks().size();

		Assert.assertEquals(noOfBooksBeforeAdding + 1, noOfBooksAfterAdding);
		Assert.assertEquals("Harry Potter",
				bookshop.getBooks().stream().filter(b1 -> b1.getBookId().equals(bookIdToAdd))
						.collect(Collectors.toList()).get(0).getBookName());

	}

	@Test
	public void testE_AddingNewBookToBookShopWhenBookAlreadyExists_shouldonlyincreasequantity()
			throws CustomException {

		int noOfBooksBeforeAdding = bookshop.getBooks().size();

		int quantityBeforerAdd = bookshop.getBooks().stream()
				.filter(b1 -> b1.getBookName().equals("Java Programming"))
				.collect(Collectors.toList()).get(0).getNoOfCopies();

		String bookIdToAdd = BookshopUtils.getUUID();
		Set<Author> al = new HashSet<>();
		Author aa = new Author(BookshopUtils.getUUID(), "Bert Bates");
		al.add(aa);
		Book b = new Book(bookIdToAdd, "Java Programming", al, 500.0, 2, 0);
		bookshop.addBook(b, 2, 2);

		int noOfBooksAfterAdding = bookshop.getBooks().size();

		// As book is already present it should only increase quantity
		Assert.assertEquals(noOfBooksBeforeAdding, noOfBooksAfterAdding);

		int quantityAfterAdd = bookshop.getBooks().stream()
				.filter(b1 -> b1.getBookName().equals("Java Programming"))
				.collect(Collectors.toList()).get(0).getNoOfCopies();

		Assert.assertEquals(quantityBeforerAdd + 2, quantityAfterAdd);


	}

	@Test
	public void testF_AddingNewBookToBookShopWhenBookNameAlreadyExistsButDifferentAuthor_shouldAddasNewBook()
			throws CustomException {

		int noOfBooksBeforeAdding = bookshop.getBooks().size();

		String bookIdToAdd = BookshopUtils.getUUID();
		Set<Author> al = new HashSet<>();
		Author aa = new Author(BookshopUtils.getUUID(), "John Davis");
		al.add(aa);
		Book b = new Book(bookIdToAdd, "Java Programming", al, 500.0, 2, 0);
		bookshop.addBook(b, 2, 2);

		int noOfBooksAfterAdding = bookshop.getBooks().size();

		// As book with book name is already present but with different author then it should add as
		// new book
		Assert.assertEquals(noOfBooksBeforeAdding + 1, noOfBooksAfterAdding);


	}


	@Test
	public void testG_AddingNewBookWithZeroQuantity() throws CustomException {

		thrown.expect(CustomException.class);
		thrown.expectMessage("Quantity should be greater than 0.");

		String bookIdToAdd = BookshopUtils.getUUID();
		Set<Author> al = new HashSet<>();
		Author aa = new Author(BookshopUtils.getUUID(), "Bert Bates");
		al.add(aa);
		Book b = new Book(bookIdToAdd, "Java Programming", al, 500.0, 0, 0);
		bookshop.addBook(b, 0, 0);

	}

	@Test
	public void testH_AddingNewBookWithBookIdNull() throws CustomException {

		thrown.expect(CustomException.class);
		thrown.expectMessage("Book Id cannot be null.");

		Set<Author> al = new HashSet<>();
		Author aa = new Author(BookshopUtils.getUUID(), "Bert Bates");
		al.add(aa);
		Book b = new Book(null, "Java Programming", al, 500.0, 0, 0);
		bookshop.addBook(b, 0, 0);

	}

	@Test
	public void testI_AddingNewBookWithBookNull() throws CustomException {

		thrown.expect(CustomException.class);
		thrown.expectMessage("Book cannot be null.");

		bookshop.addBook(null, 1, 1);

	}

	@Test
	public void testJ_IncreaseQuantityOfBooks() {

		int quantityBefore =
				bookshop.getBooks().stream().filter(b1 -> b1.getBookId().equals("1234"))
						.collect(Collectors.toList()).get(0).getNoOfCopies();

		bookshop.increaseQuantityOfBook("1234", 1);

		int quantityAfter = bookshop.getBooks().stream().filter(b1 -> b1.getBookId().equals("1234"))
				.collect(Collectors.toList()).get(0).getNoOfCopies();

		Assert.assertEquals(quantityBefore + 1, quantityAfter);

	}

	@Test
	public void testK_RemovingOfBookMethod() throws CustomException {

		// First Add a new Book
		int noOfBooksBeforeAdding = bookshop.getBooks().size();

		String bookIdToAdd = BookshopUtils.getUUID();
		Set<Author> al = new HashSet<>();
		Author aa = new Author(BookshopUtils.getUUID(), "William Shakespeare");
		al.add(aa);
		Book b = new Book(bookIdToAdd, "Hamlet", al, 500.0, 2, 0);
		bookshop.addBook(b, 2, 0);

		int noOfBooksAfterAdding = bookshop.getBooks().size();

		Assert.assertEquals(noOfBooksBeforeAdding + 1, noOfBooksAfterAdding);
		Assert.assertEquals("Hamlet",
				bookshop.getBooks().stream().filter(b1 -> b1.getBookId().equals(bookIdToAdd))
						.collect(Collectors.toList()).get(0).getBookName());

		// Remove this added Book
		bookshop.removeBook(bookIdToAdd, 2);

		int noOfBooksAfterRemoving = bookshop.getBooks().size();

		Assert.assertEquals(noOfBooksBeforeAdding, noOfBooksAfterRemoving);


	}


	@After
	public void restoreStreams() {
		System.setOut(originalOut);
	}
}
