package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Consumer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

@RunWith(JUnit4.class)
public class VersionResolverTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  private static final MockStreamHandler mockStreamHandler = new MockStreamHandler();

  private static final Duration TIMEOUT = Duration.ofSeconds(20);

  @Mock private Consumer<VersionInfo> callback;

  @Captor private ArgumentCaptor<VersionInfo> versionInfoCaptor;

  @BeforeClass
  public static void beforeClass() throws IOException {
    URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
    URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
    when(urlStreamHandlerFactory.createURLStreamHandler("https")).thenReturn(mockStreamHandler);
    mockStreamHandler.setContent("");
  }

  @Test
  public void testResolveLatestVersion() throws IOException {
    StringBuilder b = new StringBuilder();
    b.append("Version Information from the wiki\n");
    b.append("```\n");
    b.append("version=10.0.5\n");
    b.append("```\n");
    mockStreamHandler.setContent(b.toString());

    VersionResolver checker = new VersionResolver();
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT);

    assertThat(versionInfo.latestReleaseVersion()).hasValue("10.0.5");
  }

  @Test
  public void testVersionNotFound() throws IOException {
    StringBuilder b = new StringBuilder();
    b.append("Page not found");
    mockStreamHandler.setContent(b.toString());

    VersionResolver checker = new VersionResolver();
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT);

    assertThat(versionInfo.latestReleaseVersion()).isEmpty();
  }

  @Test
  public void testLocalVersion() {
    // This property is set via gradle test { } stanza.
    String expectedVersion = System.getProperty("gtfsValidatorVersionForTest");
    assertThat(expectedVersion).isNotEmpty();

    VersionResolver checker = new VersionResolver();
    VersionInfo versionInfo = checker.getVersionInfoWithTimeout(TIMEOUT);

    assertThat(versionInfo.currentVersion()).hasValue(expectedVersion);
  }

  @Test
  public void testVersionCallback() throws IOException {
    StringBuilder b = new StringBuilder();
    b.append("version=10.0.5\n");
    mockStreamHandler.setContent(b.toString());

    VersionResolver checker = new VersionResolver();
    checker.addCallback(callback);

    checker.getVersionInfoWithTimeout(TIMEOUT);

    verify(callback).accept(versionInfoCaptor.capture());
    VersionInfo versionInfo = versionInfoCaptor.getValue();
    assertThat(versionInfo.latestReleaseVersion()).hasValue("10.0.5");
  }

  private static class MockStreamHandler extends URLStreamHandler {

    private URLConnection connection = mock(URLConnection.class);

    public void setContent(String content) throws IOException {
      when(connection.getInputStream())
          .thenReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
      return connection;
    }
  }
}
