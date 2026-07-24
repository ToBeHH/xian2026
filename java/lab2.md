# Exercise: A Library Catalogue — Collections, Sorting & Exceptions

**Estimated time:** 60–75 minutes
**Topics covered:** `List`/`Set`, `equals()`/`hashCode()`, `Comparable`/`Comparator`, `Collections.sort()`/`List.sort()`, `thenComparing()`, checked exceptions, custom exception classes, `try`/`catch`/`finally`

A library needs a small system to keep track of its books. Books can be added, looked up by ISBN, and listed in different orders. Some operations can fail — an ISBN might already exist, or a lookup might not find anything — and the library wants those failures handled properly, not ignored.

There's no single "correct" design here — aim to use collections, sorting, and exception handling each at least once, sensibly.

---

## Part 1 — The `Book` Class (10 min)

Create a class `Book` with:

- Four **private final** instance variables: `String title`, `String author`, `String isbn`, `int year`
- A constructor taking all four
- Getters for all four (no setters — a `Book`'s details don't change once catalogued)
- `equals()` and `hashCode()` based **only** on `isbn`
- A `toString()` that prints something like `Book[title=Effective Java, author=Bloch, isbn=978-0134685991, year=2018]`

**Questions to answer before you move on:**

1. Why does it make sense to base `equals()`/`hashCode()` on `isbn` alone, rather than on all four fields?
2. If you later put `Book` objects into a `HashSet<Book>`, what would go wrong if you forgot to override `hashCode()` but did override `equals()`?

---

## Part 2 — Two Custom Exceptions (10 min)

The library wants two distinct, meaningful failure cases rather than generic exceptions.

1. Create `BookNotFoundException extends Exception` with a constructor that takes a `String message` and passes it to `super(message)`.
2. Create `DuplicateIsbnException extends Exception` the same way.
3. Both should be **checked** exceptions (don't extend `RuntimeException`).

**Questions to answer:**

1. What's the practical difference to a *caller* between a checked exception and an unchecked one?
2. Why might "an ISBN that already exists" be a good candidate for a **checked** exception, while "an array index out of bounds" is not?

---

## Part 3 — The `LibraryCatalogue` (20 min)

Create a class `LibraryCatalogue` that holds books in a `private final List<Book> books = new ArrayList<>();` and provides:

1. `void addBook(Book b) throws DuplicateIsbnException` — search the list for a book with the same ISBN first; if found, throw `DuplicateIsbnException` with a message that includes the ISBN. Otherwise add it.
2. `Book findByIsbn(String isbn) throws BookNotFoundException` — search the list and return the matching `Book`, or throw `BookNotFoundException` (again, include the ISBN in the message) if nothing matches.
3. `List<Book> getBooks()` — returns the current list (for printing/testing).

**Questions to answer:**

1. `addBook` and `findByIsbn` both loop over `books` doing a linear search. What data structure could make the "does this ISBN already exist?" check faster, and what would you need to keep in sync if you used it alongside the `List`?
2. Why is it `addBook(Book b) throws DuplicateIsbnException` and not `void addBook(Book b) throws Exception`? What do you lose by throwing the more general `Exception` type instead?

---

## Part 4 — Sorting the Catalogue (10 min)

Add two more methods to `LibraryCatalogue`:

1. `void sortByTitle()` — sorts `books` in place, alphabetically by title, using `Comparator.comparing()`.
2. `void sortByYearThenTitle()` — sorts `books` in place by `year` ascending, and for books published in the same year, by `title` ascending — using `thenComparing()`.

**Questions to answer:**

1. Could `Book` implement `Comparable<Book>` instead, to get one of these two orderings "for free" with `Collections.sort(books)`? Which of the two orderings would make sense as the *natural* one, and why can't `Comparable` give you both?
2. What's the difference between `books.sort(...)` (used above) and `Collections.sort(books, ...)`? Do they behave differently, or is it purely a style choice?

---

## Part 5 — Wiring It Together in `Main` (10 min)

Write a `main` method that:

1. Creates a `LibraryCatalogue` and adds **six** books (using literal `Book` objects), covering at least two different years and two different authors.
2. Deliberately tries to add a **seventh** book that reuses one of the first six ISBNs, inside a `try/catch` for `DuplicateIsbnException` — print a friendly message on catch.
3. Calls `findByIsbn()` twice: once with an ISBN that exists, and once with one that doesn't — wrap both in a single `try/catch` for `BookNotFoundException`, with a `finally` block that always prints `"Lookup attempted"`.
4. Calls `sortByYearThenTitle()` and prints the whole catalogue, then calls `sortByTitle()` and prints it again, so you can see the ordering change.

---

## Bonus Challenges

- Keep a `Set<String>` of ISBNs alongside the `List<Book>` so `addBook` can check for duplicates in O(1) instead of scanning the whole list. What has to stay in sync now that you have two data structures describing the same collection?
- Add `void removeBook(String isbn) throws BookNotFoundException`.
- Add a **descending**-by-year ordering using `.reversed()` combined with `thenComparing()` for title (ascending).
- Replace your `System.out.println()` calls in `Main`'s catch blocks with a `java.util.logging.Logger`, logged at `WARNING` level.
- Add simple file persistence: `void save(String filename)` and a matching `load()`, using **try-with-resources** around a `FileWriter`/`FileReader`.

---

## Checklist — you should now be able to explain:

- [ ] Why `equals()`/`hashCode()` should usually be based on a class's "identity" fields, not every field
- [ ] The difference between a checked and an unchecked exception, and when to choose each
- [ ] How `Comparator.comparing()` and `.thenComparing()` build up a multi-key ordering
- [ ] The difference between `Comparable` (one natural ordering, on the class) and `Comparator` (any ordering, external)
- [ ] Why a `finally` block runs even when the `try` block throws and is caught
- [ ] What trade-off you'd be making by swapping a `List`-only design for a `List` + `Set` design
