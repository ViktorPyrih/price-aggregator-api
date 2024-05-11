package ua.edu.cdu.vu.price.aggregator.api.util.pool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Interactive;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Slf4j
public class FixedWebDriverPool implements WebDriverPool, Runnable {

    private static final int HEALTH_CHECK_INTERVAL_SECONDS = 60;

    private final int capacity;
    private final long timeoutMillis;
    private final BlockingQueue<WebDriverProxy> webDrivers;
    private final Supplier<WebDriver> factory;
    private final ScheduledExecutorService scheduler;

    public FixedWebDriverPool(int capacity, long timeoutMillis, Supplier<WebDriver> factory) {
        this.capacity = capacity;
        this.webDrivers = new ArrayBlockingQueue<>(capacity);
        this.timeoutMillis = timeoutMillis;
        this.factory = factory;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
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
                .forEach(webDrivers::add);
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
        webDrivers.stream()
                .map(WebDriverProxy::unwrap)
                .forEach(WebDriver::close);
        scheduler.shutdownNow();
        log.info("Web driver pool closed successfully");
    }

    private WebDriverProxy createWebDriverProxy(WebDriver webDriver) {
        return (WebDriverProxy) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{WebDriverProxy.class}, new WebDriverInvocationHandler(webDriver));
    }

    @Override
    @SuppressWarnings("all")
    public void run() {
        log.info("Health check of web driver pool started: {}", webDrivers.size());
        int webDriversRemovedCount = (int) webDrivers.stream()
                .filter(this::healthCheck)
                .toList()
                .stream()
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
            log.warn("Web driver is not available anymore, closing it");
            webDriver.unwrap().close();

            return true;
        }

        return false;
    }

    private interface WebDriverProxy extends WebDriver, JavascriptExecutor, TakesScreenshot, Interactive {

        WebDriver unwrap();
    }

    @RequiredArgsConstructor
    private class WebDriverInvocationHandler implements InvocationHandler {

        private static final String QUIT = "quit";
        private static final String UNWRAP = "unwrap";

        private final WebDriver delegate;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals(QUIT)) {
                return webDrivers.add(createWebDriverProxy(delegate));
            }
            if (method.getName().equals(UNWRAP)) {
                return delegate;
            }

            return method.invoke(delegate, args);
        }
    }
}
