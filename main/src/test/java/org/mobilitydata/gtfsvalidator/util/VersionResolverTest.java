package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.runner.ApplicationType;

@RunWith(JUnit4.class)
public class VersionResolverTest {

  private static final MockStreamHandler mockStreamHandler = new MockStreamHandler();

  private static final Duration TIMEOUT = Duration.ofSeconds(5);

  @BeforeClass
  public static void beforeClass() throws IOException {
    URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
    URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
    when(urlStreamHandlerFactory.createURLStreamHandler("https")).thenReturn(mockStreamHandler);
    mockStreamHandler.setContent("");
  }

  @Test
  public void testResolveLatestReleaseVersion() throws IOException {
    mockStreamHandler.setContent("{\"version\":\"10.0.5\"}");

    VersionResolver checker = new VersionResolver(ApplicationType.CLI);
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT, false);

    assertThat(versionInfo.latestReleaseVersion()).hasValue("10.0.5");
  }

  @Test
  public void testLatestReleaseVersionNotFound() throws IOException {
    mockStreamHandler.setContent("Page not found");

    VersionResolver checker = new VersionResolver(ApplicationType.CLI);
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT, false);

    assertThat(versionInfo.latestReleaseVersion()).isEmpty();
  }

  @Test
  public void testReleaseVersionUrlParams() throws IOException {
    // This property is set via gradle test { } stanza.
    String expectedVersion = System.getProperty("gtfsValidatorVersionForTest");
    assertThat(expectedVersion).isNotEmpty();

    mockStreamHandler.setContent("{\"version\":\"10.0.5\"}");

    VersionResolver checker = new VersionResolver(ApplicationType.WEB);
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT, false);

    assertThat(mockStreamHandler.url).isNotNull();
    assertThat(mockStreamHandler.url.getQuery())
        .isEqualTo("application_type=WEB&current_version=" + expectedVersion);
  }

  @Test
  public void testLocalVersion() {
    // This property is set via gradle test { } stanza.
    String expectedVersion = System.getProperty("gtfsValidatorVersionForTest");
    assertThat(expectedVersion).isNotEmpty();

    VersionResolver checker = new VersionResolver(ApplicationType.CLI);
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT, false);

    assertThat(versionInfo.currentVersion()).hasValue(expectedVersion);
  }

  @Test
  public void testVersionCallback() throws IOException, InterruptedException {
    mockStreamHandler.setContent("{\"version\":\"10.0.5\"}");

    // A dummy callback that captures the updated version info and triggers a countdown latch
    // that our test can wait for.
    AtomicReference<VersionInfo> versionInfo = new AtomicReference<>(VersionInfo.empty());
    CountDownLatch callbackLatch = new CountDownLatch(1);
    Consumer<VersionInfo> callback =
        (updatedVersionInfo) -> {
          versionInfo.set(updatedVersionInfo);
          callbackLatch.countDown();
        };

    VersionResolver checker = new VersionResolver(ApplicationType.CLI);
    checker.addCallback(callback);

    // Wait until the callback is actually triggered.
    callbackLatch.await(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

    assertThat(versionInfo.get().latestReleaseVersion()).hasValue("10.0.5");
  }

  private static class MockStreamHandler extends URLStreamHandler {

    private URLConnection connection = mock(URLConnection.class);

    private URL url = null;

    public void setContent(String content) throws IOException {
      when(connection.getInputStream())
          .thenReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public URL getUrl() {
      return this.url;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
      this.url = u;
      return connection;
    }
  }
}
