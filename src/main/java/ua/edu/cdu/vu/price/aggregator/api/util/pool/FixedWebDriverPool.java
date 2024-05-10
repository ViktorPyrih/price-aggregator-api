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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Slf4j
public class FixedWebDriverPool implements WebDriverPool {

    private final int capacity;
    private final long timeoutMillis;
    private final BlockingQueue<WebDriverProxy> webDrivers;
    private final Supplier<WebDriver> factory;

    public FixedWebDriverPool(int capacity, long timeoutMillis, Supplier<WebDriver> factory) {
        this.capacity = capacity;
        this.webDrivers = new ArrayBlockingQueue<>(capacity);
        this.timeoutMillis = timeoutMillis;
        this.factory = factory;
    }

    @Override
    public void initialize() {
        log.info("About to initialize web driver pool");
        IntStream.range(0, capacity)
                .mapToObj(i -> factory.get())
                .map(this::createWebDriverProxy)
                .forEach(webDrivers::add);
        log.info("Pool of {} web drivers initialized successfully", capacity);
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
        log.info("Web driver pool closed successfully");
    }

    private WebDriverProxy createWebDriverProxy(WebDriver webDriver) {
        return (WebDriverProxy) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{WebDriverProxy.class}, new WebDriverInvocationHandler(webDriver));
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
