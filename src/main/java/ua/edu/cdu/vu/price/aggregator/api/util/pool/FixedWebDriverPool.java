package ua.edu.cdu.vu.price.aggregator.api.util.pool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Interactive;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Slf4j
public class FixedWebDriverPool implements WebDriverPool, Runnable {

    private static final int HEALTH_CHECK_INTERVAL_SECONDS = 60;
    private static final int CLOSE_TIMEOUT_SECONDS = 30;

    private final int capacity;
    private final long timeoutMillis;
    private final BlockingDeque<WebDriverProxy> webDrivers;
    private final Supplier<WebDriver> factory;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService executor;

    public FixedWebDriverPool(int capacity, long timeoutMillis, Supplier<WebDriver> factory) {
        this.capacity = capacity;
        this.webDrivers = new LinkedBlockingDeque<>(capacity);
        this.timeoutMillis = timeoutMillis;
        this.factory = factory;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void initialize() {
        log.info("About to initialize web driver pool");
        initialize(capacity);
        scheduler.scheduleWithFixedDelay(this, HEALTH_CHECK_INTERVAL_SECONDS, HEALTH_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
        log.info("Health check scheduled at fixed rate of {} seconds", HEALTH_CHECK_INTERVAL_SECONDS);
        log.info("Pool of {} web drivers initialized successfully", capacity);
    }

    private void initialize(int webDriversToAdd) {
        IntStream.range(0, webDriversToAdd)
                .mapToObj(i -> factory.get())
                .map(this::createWebDriverProxy)
                .forEach(webDrivers::addFirst);
    }

    @Override
    public WebDriver getDriver() {
        try {
            return Optional.ofNullable(webDrivers.poll(timeoutMillis, TimeUnit.MILLISECONDS))
                    .orElseThrow(() -> new WebDriverNotAvailableException(timeoutMillis));
        } catch (InterruptedException e) {
            throw new WebDriverNotAvailableException(e);
        }
    }

    @Override
    public void close() {
        log.info("About to close web driver pool");
        webDrivers.forEach(this::close);
        scheduler.shutdownNow();
        executor.shutdownNow();
        log.info("Web driver pool closed successfully");
    }

    private WebDriverProxy createWebDriverProxy(WebDriver webDriver) {
        return (WebDriverProxy) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{WebDriverProxy.class}, new WebDriverInvocationHandler(webDriver));
    }

    @Override
    public void run() {
        log.info("Health check of web driver pool started: {}", webDrivers.size());
        int webDriversRemovedCount = (int) webDrivers.stream()
                .filter(this::healthCheck)
                .filter(webDrivers::remove)
                .count();
        if (webDriversRemovedCount > 0) {
            log.info("About to reinitialize {} web drivers", webDriversRemovedCount);
            initialize(webDriversRemovedCount);
        }

        log.info("Health check of web driver pool finished");
    }

    private boolean healthCheck(WebDriverProxy webDriver) {
        try {
            webDriver.getCurrentUrl();
        } catch (RuntimeException e) {
            log.warn("Web driver is not available anymore, closing it. Cause: ", e);
            close(webDriver);

            return true;
        }

        return false;
    }

    private void close(WebDriverProxy webDriver) {
        close(webDriver.unwrap());
    }

    private void close(WebDriver webDriver) {
        try {
            executor.submit(webDriver::quit).get(CLOSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("Failed to close web driver in {} seconds", CLOSE_TIMEOUT_SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to close web driver", e);
        }
    }

    private interface WebDriverProxy extends WebDriver, JavascriptExecutor, TakesScreenshot, Interactive {

        WebDriver unwrap();
    }

    @RequiredArgsConstructor
    private class WebDriverInvocationHandler implements InvocationHandler {

        private static final String QUIT = "quit";
        private static final String UNWRAP = "unwrap";
        private static final String EQUALS = "equals";

        private final WebDriver delegate;

        private boolean shouldClose;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (shouldClose && method.getName().equals(QUIT)) {
                close(delegate);
                initialize(1);
                return null;
            }
            if (method.getName().equals(QUIT)) {
                return webDrivers.add(createWebDriverProxy(delegate));
            }
            if (method.getName().equals(UNWRAP)) {
                return delegate;
            }
            if (method.getName().equals(EQUALS)) {
                return proxy == args[0];
            }

            try {
                return method.invoke(delegate, args);
            } catch (InvocationTargetException e) {
                handleWebDriverException(e, method);
                throw e;
            }
        }

        private void handleWebDriverException(InvocationTargetException e, Method method) throws InvocationTargetException {
            if (e.getTargetException() instanceof WebDriverException) {
                shouldClose = true;
                log.debug("Failed to execute method: {} on web driver", method.getName(), e);
            }
        }
    }
}
