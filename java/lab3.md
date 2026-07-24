# Exercise: Concurrent Order Processing — Records, Streams, Concurrency & Testing

**Estimated time:** 75–90 minutes
**Topics covered:** `record`, the Stream API (`filter`/`map`/`sorted`/`collect`), `Collectors.groupingBy()`, `Optional<T>`, `ExecutorService`, `Callable`/`Future`, JUnit 5 (`assertEquals`, `assertThrows`, `@ParameterizedTest`)

A shop wants to analyse its orders and process them faster by running the (simulated) processing work concurrently instead of one order at a time. You'll build a small analytics module using streams, a small concurrent processor using an executor, and prove both work with JUnit tests.

It's fine to skip the bonus challenges if you run out of time — a working streams pipeline plus a working executor plus at least one real test is a complete answer.

---

## Part 1 — The `Order` Record 

Declare a `record`:

```java
public record Order(int id, String customer, double amount, String status) {}
```

where `status` is either `"PAID"` or `"PENDING"`.

1. Create a `List<Order> orders` (e.g. in a small `static` helper method `sampleOrders()`) containing **at least 10** orders, across at least 3 different customers, with a mix of `PAID` and `PENDING` statuses and varied amounts.
2. Print the list using the record's generated `toString()` — you didn't write one, so where did it come from?

**Questions to answer:**

1. What did declaring this as a `record` give you "for free" compared with writing an equivalent class by hand?
2. Records are immutable — every field is `final` and there are no setters. Why is that a genuinely good fit for something like an `Order` line, rather than just a convenient shortcut?
3. Add a **compact constructor** that rejects a negative `amount` with an `IllegalArgumentException`. Where exactly does this code go, and why doesn't it need to reassign every field?

---

## Part 2 — Streaming Analytics

Create a class `OrderAnalytics` with these methods, each implemented as a **single stream pipeline** (no manual loops):

1. `double totalPaid(List<Order> orders)` — the sum of `amount` for orders whose `status` is `"PAID"`.
2. `Map<String, List<Order>> byCustomer(List<Order> orders)` — every order, grouped by `customer`, using `Collectors.groupingBy()`.
3. `Optional<Order> highestValue(List<Order> orders)` — the single largest order by `amount`. Think carefully about what should come back for an **empty** list.
4. `List<String> paidCustomerNamesSorted(List<Order> orders)` — the `customer` names (not the whole `Order`) of every `PAID` order, **deduplicated**, sorted alphabetically. (Hint: `filter` → `map` → `distinct` → `sorted` → `collect`.)

**Questions to answer:**

1. In `totalPaid`, which stream operation is the *terminal* one, and which are *intermediate*? What would happen if you accidentally called `.stream()` on the same `List` twice within one method — is that allowed?
2. Why does `highestValue` return `Optional<Order>` rather than `Order` directly? Show, with a short snippet, both `orElse(...)` and `orElseThrow(...)` being used to unwrap it in two different ways.
3. In `paidCustomerNamesSorted`, does the order of `.distinct()` and `.sorted()` in the pipeline matter for the *result*? Does it matter for *performance*?

---

## Part 3 — Concurrent Processing 

Create a class `OrderProcessor` with:

1. `String processOrder(Order o) throws InterruptedException` — call `Thread.sleep(200)` to simulate slow work (e.g. calling a payment gateway), then return a summary string like `"Order 3 for Priya processed"`.
2. `List<String> processAllSequential(List<Order> orders) throws InterruptedException` — call `processOrder` for every order in a plain loop, collecting the results into a `List<String>`. Time this with `System.currentTimeMillis()` before/after and print the elapsed time.
3. `List<String> processAllConcurrent(List<Order> orders) throws Exception` — using an `ExecutorService` from `Executors.newFixedThreadPool(4)`:
   - Submit a `Callable<String>` per order that calls `processOrder`
   - Collect every returned `Future<String>` into a `List<Future<String>>`
   - Then loop over the futures calling `.get()` on each, collecting into a `List<String>`
   - `shutdown()` the executor before returning
   - Time this the same way as step 2 and print the elapsed time

**Questions to answer:**

1. With 10 orders and a 200ms simulated delay each, roughly how long should `processAllSequential` take? Run it and check your prediction was right.
2. Roughly how long should `processAllConcurrent` take with a pool of 4? Run it — was it close to your prediction? What would you expect if you changed the pool size to 1? To 10?
3. `Future<String>.get()` can throw a checked `ExecutionException` as well as `InterruptedException`. What does an `ExecutionException` actually mean — i.e. what would have had to go wrong inside `processOrder` for you to see one?
4. You called `pool.shutdown()`, not `pool.shutdownNow()`. What's the difference, and which is more appropriate here?

---

## Part 4 — Testing It

Write JUnit 5 tests (in a `OrderAnalyticsTest` class, with a `@BeforeEach` that builds a small fixed list of sample orders):

1. A test that `totalPaid()` returns the correct sum for a known list containing a mix of `PAID` and `PENDING` orders.
2. A test that `highestValue()` on a **non-empty** list returns the order you expect.
3. A test that `highestValue()` on an **empty** list returns `Optional.empty()` — don't use `assertThrows` here, since an empty result isn't an exception. What assertion *should* you use instead?
4. Convert your `totalPaid()` test into a `@ParameterizedTest`, covering at least 3 different order lists and their expected totals (you'll need a way to pass a `List<Order>` in — a small `static` method annotated with `@MethodSource` is one option, or keep it simple with just varying amounts via `@CsvSource` against a fixed two-order list).

**Questions to answer:**

1. Why does testing `highestValue()`'s empty-list case matter just as much as testing the "normal" case — what real-world bug would slip through if you only tested non-empty lists?
2. If `totalPaid()` used `int` instead of `double` for `amount`, would `assertEquals(expected, actual)` still be safe to use directly? (Look up why comparing `double`s with `assertEquals` sometimes needs a **delta** argument.)

---

# Starter Skeleton

:: left ::

```java
public class OrderAnalytics {

    public double totalPaid(List<Order> orders) {
        return orders.stream()
            // filter "PAID", sum amount
            .mapToDouble(Order::amount)
            .sum();
    }

    public Map<String, List<Order>> byCustomer(
            List<Order> orders) {
        return orders.stream()
            .collect(Collectors.groupingBy(
                Order::customer));
    }

    public Optional<Order> highestValue(
            List<Order> orders) {
        return orders.stream()
            .max(Comparator.comparing(
                Order::amount));
    }
}
```

:: right ::

```java
public class OrderProcessor {

    public String processOrder(Order o)
            throws InterruptedException {
        Thread.sleep(200); // pretend this is slow I/O
        return "Order " + o.id() + " for "
             + o.customer() + " processed";
    }

    public List<String> processAll(
            List<Order> orders) throws Exception {
        ExecutorService pool =
            Executors.newFixedThreadPool(4);
        List<Future<String>> futures = new ArrayList<>();

        for (Order o : orders) {
            futures.add(pool.submit(
                () -> processOrder(o)));
        }

        List<String> results = new ArrayList<>();
        for (Future<String> f : futures) {
            results.add(f.get());
        }
        pool.shutdown();
        return results;
    }
}
```

---

## Bonus Challenges

- Replace `Future` with `CompletableFuture.supplyAsync(...)`, chaining `.thenApply()` to format each order's result before collecting them all with `.join()`.
- Rewrite `status` as an `enum OrderStatus { PAID, PENDING }` instead of a `String`. What changes in `Order`, in `OrderAnalytics`, and in your sample data?
- Add a method to `OrderAnalytics` that returns total spend **per customer**, sorted highest-spend-first — combine `groupingBy()` with a downstream `Collectors.summingDouble()`, then sort the resulting `Map` entries.
- Rewrite the pattern-matching idea from this morning: declare a `sealed interface OrderState permits Paid, Pending` with `record` implementations instead of a `status` string, and use a `switch` expression somewhere to handle both cases exhaustively.
- Compare `Executors.newFixedThreadPool(4)` with `Executors.newVirtualThreadPerTaskExecutor()` for this workload — do you see a difference for 10 orders? What about for 1,000?

---

## Checklist — you should now be able to explain:

- [ ] What a `record` generates for you automatically, and why that suits an immutable data class
- [ ] The difference between an intermediate and a terminal stream operation, and why a stream can only be consumed once
- [ ] Why `Optional<T>` is often a better return type than `null` for a "might not exist" result
- [ ] Why `start()`/an executor gives you real concurrency while calling a method directly does not
- [ ] What a `Future.get()` actually does (and blocks on), and what an `ExecutionException` means
- [ ] Why testing edge cases (like an empty list) is as important as testing the everyday case
