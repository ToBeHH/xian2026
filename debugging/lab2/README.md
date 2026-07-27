# Lab 2

1. Set a breakpoint on the `return` line
2. Call `http://localhost:8080/discount?orderTotal=100&tier=gold` (lowercase)
3. Check `rate` in the **Variables** pane — it's `0.0`, even though this clearly looks like a gold order (output is 100 - no discount)
4. Use **Evaluate Expression** to try `tier.equalsIgnoreCase("GOLD")` — `true`. There's your root cause: an exact, case-sensitive match
