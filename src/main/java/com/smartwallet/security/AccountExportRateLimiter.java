
package com.smartwallet.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccountExportRateLimiter {

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_MILLIS = 60_000L;

    private final ConcurrentHashMap<String, Window> windows =
            new ConcurrentHashMap<>();

    private final AtomicInteger requestCounter = new AtomicInteger();

    public boolean isAllowed(String userKey) {
        long now = System.currentTimeMillis();

        Window window = windows.compute(userKey, (key, current) -> {
            if (current == null
                    || now - current.startTime >= WINDOW_MILLIS) {
                return new Window(now);
            }

            return current;
        });

        boolean allowed = window.tryAcquire(now);

        // Periodically remove expired entries for inactive users.
        if (requestCounter.incrementAndGet() % 100 == 0) {
            windows.entrySet().removeIf(entry ->
                    now - entry.getValue().startTime >= WINDOW_MILLIS
            );
        }

        return allowed;
    }

    private static final class Window {
        private final long startTime;
        private int requests;

        private Window(long startTime) {
            this.startTime = startTime;
            this.requests = 0;
        }

        private synchronized boolean tryAcquire(long now) {
            if (now - startTime >= WINDOW_MILLIS) {
                return false;
            }

            if (requests >= MAX_REQUESTS) {
                return false;
            }

            requests++;
            return true;
        }
    }
}
