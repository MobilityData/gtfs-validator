package org.mobilitydata.gtfsvalidator.util;

import com.google.common.base.Strings;
import com.google.common.flogger.FluentLogger;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.jar.Manifest;
import org.mobilitydata.gtfsvalidator.runner.ApplicationType;

/**
 * Methods to resolve the {@link VersionInfo} for the current validator instance. Since resolving
 * the latest release version requires an external network request, we resolve the version
 * asynchronously.
 */
public class VersionResolver {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  /** We look up the latest release version at the JSON api endpoint. */
  private static final String LATEST_RELEASE_VERSION_URL =
      "https://gtfs-validator.mobilitydata.org/api/version";

  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final ApplicationType applicationType;

  private SettableFuture<VersionInfo> resolvedVersionInfo = SettableFuture.create();

  private boolean resolutionStarted = false;

  public VersionResolver(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  /**
   * Attempts to resolve the application {@link VersionInfo} within the specified timeout. If the
   * version info can't be resolved in the specified timeout, an empty info will be returned.
   */
  public VersionInfo getVersionInfoWithTimeout(Duration timeout, boolean skipValidatorUpdate) {
    VersionInfo versionInfo = VersionInfo.empty();
    try {
      versionInfo = resolve(skipValidatorUpdate);
    } catch (Throwable ex) {
      logger.atSevere().withCause(ex).log("Error resolving version");
    }

    try {
      versionInfo = resolvedVersionInfo.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (Throwable ex) {
      logger.atSevere().withCause(ex).log("Error obtaining release version");
    }
    return versionInfo;
  }

  /**
   * Adds a callback that will be called with the resolved {@link VersionInfo} if and when it
   * becomes available.
   */
  public void addCallback(Consumer<VersionInfo> callback) {
    resolve(false);
    Futures.addCallback(
        resolvedVersionInfo,
        new FutureCallback<>() {
          @Override
          public void onSuccess(VersionInfo result) {
            callback.accept(result);
          }

          @Override
          public void onFailure(Throwable t) {
            logger.atSevere().withCause(t).log("Error resolving version");
          }
        },
        executor);
  }

  /** Starts release version resolution on a background thread. */
  public synchronized VersionInfo resolve(boolean skipValidatorUpdate) {
    if (resolutionStarted) {
      return VersionInfo.empty();
    }
    resolutionStarted = true;

    final VersionInfo versionInfo = VersionInfo.empty();
    try {
      versionInfo.setCurrentVersion(resolveCurrentVersion());
    } catch (IOException ex) {
      logger.atSevere().withCause(ex).log("Error resolving version info");
    }

    executor.submit(
        () -> {
          if (!skipValidatorUpdate) {
            try {
              var version = resolveLatestReleaseVersion(versionInfo.currentVersion());
              versionInfo.setLatestReleaseVersion(version);
            } catch (Throwable ex) {
              logger.atSevere().withCause(ex).log("Error obtaining release version info from endpoint");
            }
          }
          resolvedVersionInfo.set(versionInfo);
          return versionInfo;
        });
    return versionInfo;
  }

  /**
   * We resolve the current application version by looking at META-INF/MANIFEST.MF entries. This
   * resolution is slightly complicated, depending on our deployment environment. For the
   * application shadow jar, there will be a single MANIFEST.MF entry, with an Implementation-Title
   * of `gtfs-validator`. In a non-shadow-jar deployment (e.g. unit-test or gradle :run), there will
   * be multiple MANIFEST.MF entries (different jars on the classpath can provide their own), so we
   * look for the `gtfs-validator-core` MANIFEST.MF, since the shadow jar won't be present.
   *
   * <p>The return value is Optional because it's possible no version info is found.
   *
   * @throws IOException
   */
  public Optional<String> resolveCurrentVersion() throws IOException {
    ScanResult scan = new ClassGraph().scan();
    Optional<String> gtfsValidatorCoreVersion = Optional.empty();
    for (Resource resource : scan.getResourcesWithPath("META-INF/MANIFEST.MF")) {
      Manifest m = new Manifest();
      m.read(resource.open());
      String title = m.getMainAttributes().getValue("Implementation-Title");
      if (title == null) {
        continue;
      }
      if (title.equals("gtfs-validator")) {
        // We prefer the gtfs-validator version and return it immediately if found.
        String version = m.getMainAttributes().getValue("Implementation-Version");
        if (version != null && !version.isBlank()) {
          return Optional.of(version);
        }
      } else if (title.equals("gtfs-validator-core")) {
        // We'll also use gtfs-validator-core version if available but keep processing in case
        // we find gtfs-validator instead.
        String version = m.getMainAttributes().getValue("Implementation-Version");
        if (version != null && !version.isBlank()) {
          gtfsValidatorCoreVersion = Optional.of(version);
        }
      }
    }
    return gtfsValidatorCoreVersion;
  }

  private Optional<String> resolveLatestReleaseVersion(Optional<String> currentVersion)
      throws IOException {
    URL url =
        new URL(
            String.format(
                "%s?application_type=%s&current_version=%s",
                LATEST_RELEASE_VERSION_URL, applicationType, currentVersion.orElse("")));
    try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
      Gson gson = new GsonBuilder().create();
      VersionResponse response = gson.fromJson(in, VersionResponse.class);
      if (response != null && !Strings.isNullOrEmpty(response.version)) {
        logger.atInfo().log("resolved release version=%s", response.version);
        return Optional.of(response.version);
      }
    }
    return Optional.empty();
  }

  /** Serialization object for parsing the /version API response. */
  public static class VersionResponse {
    String version;
  }
}
