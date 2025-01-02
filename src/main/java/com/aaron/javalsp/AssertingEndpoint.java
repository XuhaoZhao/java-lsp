package com.aaron.javalsp;

import ch.qos.logback.core.joran.sanity.Pair;
import org.eclipse.lsp4j.jsonrpc.Endpoint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AssertingEndpoint implements Endpoint {
    public Map<String, Pair<Object, Object>> expectedRequests = new LinkedHashMap<>();

    @Override
    public CompletableFuture<?> request(String method, Object parameter) {
        Pair<Object, Object> result = expectedRequests.remove(method);
        return CompletableFuture.completedFuture(result);
    }

    public Map<String, Object> expectedNotifications = new LinkedHashMap<>();

    @Override
    public void notify(String method, Object parameter) {
        Object object = expectedNotifications.remove(method);
    }

    /**
     * wait max 1 sec for all expectations to be removed
     */
    public void joinOnEmpty() {
        long before = System.currentTimeMillis();
        do {
            if (expectedNotifications.isEmpty() && expectedNotifications.isEmpty()) {
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while ( System.currentTimeMillis() < before + 1000);
    }

    @Override
    public String toString() {
        return "";
    }
}
