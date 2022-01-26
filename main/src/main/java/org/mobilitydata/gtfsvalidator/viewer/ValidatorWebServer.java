package org.mobilitydata.gtfsvalidator.viewer;

import static spark.Spark.*;

import com.google.common.flogger.FluentLogger;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.mobilitydata.gtfsvalidator.cli.Main;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.input.GtfsZipFileInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

public class ValidatorWebServer {
  public static void runServer() {
    startSparkServer();
    while (true) {
      System.out.println("Sleeping for 10 minutes, press ^C to interrupt.");
      try {
        Thread.sleep(600000);
      } catch (InterruptedException e) {
        System.out.println("Caught interrupt, stopping embedded web server.");
        stop();
        System.exit(0);
      }
    }
  }

  public static void startSparkServer() {
    port(6888);

    get("/", (req, res) -> ViewerAssets.FormHtml());
    get("/form.html", (req, res) -> ViewerAssets.FormHtml());
    get("/index.js", (req, res) -> ViewerAssets.IndexJs());
    get(
        "/index.css",
        (req, res) -> {
          res.type("text/css");
          return ViewerAssets.IndexCss();
        });
    get("/snabbdom.browser.js", (req, res) -> ViewerAssets.SnabbdomBrowserJs());

    post(
        "/validate",
        (req, res) -> {
          req.attribute(
              // Jetty must be configured to allow multipart/form-data uploads.
              "org.eclipse.jetty.multipartConfig",
              new MultipartConfigElement(
                  // Save multipart/form-data files here.
                  System.getProperty("java.io.tmpdir", "/tmp"),
                  MaxZipFileSize, // Maximum file upload size.
                  MaxZipFileSize, // Maximum total form size.
                  1 * 1024 * 1024 // Save any files over 1MB in size to disk.
                  ));

          if (!req.contentType().toLowerCase().startsWith("multipart/form-data")) {
            System.out.printf(
                "Content type is %s, expected multipart/form-data.\n", req.contentType());
            return "Expected multipart/form-data";
          }

          String vehicle = req.queryParams("vehicle");
          if (vehicle == null) {
            vehicle = "<null>";
          }
          if (validVehicleNameString(vehicle)) {
            System.out.printf("Vehicle name %s is valid.\n", vehicle);
          } else {
            System.out.printf("Vehicle name %s is not valid.\n", vehicle);
            return "Not familiar with that mass transit vehicle name.";
          }

          Part filePart = req.raw().getPart("file");
          var tempFile = java.io.File.createTempFile("validatorWebServer.", ".upload.zip");
          tempFile.deleteOnExit();
          filePart.write(tempFile.getName());
          try (InputStream inputStream = filePart.getInputStream();
              // var tempFileNIO = new java.nio.file.File.(tempFile.getName());
              // var seekableByteChannel = tempFile.getChannel();
              var seekableByteChannel =
                  Files.newByteChannel(Paths.get(tempFile.getPath()), StandardOpenOption.READ);
              ZipArchiveInputStream zis = new ZipArchiveInputStream(inputStream)) {
            if (validZipArchiveInputStream(zis)) {
              System.out.println("Zip file upload is valid.");
              // return validationReport(req, res, zis);
              return validationReport(req, res, seekableByteChannel);

            } else {
              System.out.println("Zip file upload does not look like GTFS, or it is too large.");
              return "Zip file upload does not look like GTFS, or it is too large.";
            }
          }
        });
  }

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  static String validationReport(spark.Request req, spark.Response res, SeekableByteChannel sbb) {

    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    var prettyGson = builder.create();

    NoticeContainer noticeContainer = new NoticeContainer();

    ValidationContext validationContext =
        ValidationContext.builder()
            .setCountryCode(CountryCode.forStringOrUnknown(CountryCode.ZZ))
            .setCurrentDateTime(new CurrentDateTime(ZonedDateTime.now(ZoneId.systemDefault())))
            .build();

    ValidatorLoader validatorLoader = null;
    try {
      validatorLoader = new ValidatorLoader();
    } catch (ValidatorLoaderException e) {
      // logger.atSevere().withCause(e).log("Cannot load validator classes");
      // System.exit(1);
    }
    GtfsFeedLoader feedLoader = new GtfsFeedLoader();
    GtfsInput gtfsInput = null;
    GtfsFeedContainer feedContainer;

    try {
      gtfsInput = new GtfsZipFileInput(new ZipFile(sbb));
      feedContainer =
          Main.loadAndValidate(
              validatorLoader, feedLoader, noticeContainer, gtfsInput, validationContext);
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("IO Exception during Validation");
      return "";
    } catch (InterruptedException e) {
      logger.atSevere().withCause(e).log("Validation was interrupted");
      // logger.atSevere().withCause(e).log("Validation was interrupted");
      // System.exit(1);
      return "";
    }

    return ViewerIndex.IndexHtmlAll(
        "var report = "
            + prettyGson.toJson(noticeContainer.exportValidationNotices())
            // + "{ notices: [] }" // TBD
            + ";");
  }

  public static boolean validVehicleNameString(String s) {
    // Simple captcha.
    if (s == null) {
      return false;
    }
    String[] validNames = {"bus", "train", "tram"};
    for (String name : validNames) {
      if (s.equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  // Sanity-check parameters.
  //
  // Maximum zip file size, 512 MB. Largest I've seen in wild is 190M.
  public static int MaxZipEntrySize = 512 * 1024 * 1024;
  // Maximum zip file size, 128 MB. Largest I've seen in wild is 33M (stop_times.txt).
  public static int MaxZipFileSize = 128 * 1024 * 1024;
  // Maximum number of zip file entries.
  public static int MaxZipFileEntries = 128;

  public static boolean validZipArchiveInputStream(ZipArchiveInputStream zis) {
    System.out.printf("ZipArchiveInputStream is %s\n", zis);
    ZipArchiveEntry ze;
    int entriesCount = 0;
    try {
      while ((ze = zis.getNextZipEntry()) != null) {
        entriesCount++;
        System.out.printf("Found ZipEntry %s size %d.\n", ze.getName(), ze.getSize());
        if (ze.getName().contains("\\")
            || ze.getName().contains("/")
            || ze.getName().contains("..")) {
          System.out.printf("Entry name looks like it could be a directory, aborting.\n");
          return false;
        }

        /* Many files give -1 entry sizes on otherwise good GTFS, don't know
         * why, but disabling size checks until we do know. */
        if (false) {
          if (ze.getSize() < 1) {
            System.out.printf("Entry is smaller than 1 byte, aborting.\n");
            return false;
          }
          if (ze.getSize() > MaxZipEntrySize) {
            System.out.printf(
                "Entry is larger than maximum size of %d, aborting.\n", MaxZipEntrySize);
            return false;
          }
        }
      }
      if (entriesCount > MaxZipFileEntries) {
        System.out.printf(
            "Found %d entries; more than the maxiumum %d, aborting.\n",
            entriesCount, MaxZipEntrySize);
        return false;
      }
    } catch (IOException e) {
      System.out.println("IO exception when reading zip.");
      return false;
    }
    return true;
  }
}
