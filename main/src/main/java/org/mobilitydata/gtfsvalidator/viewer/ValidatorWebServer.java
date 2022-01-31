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
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.mobilitydata.gtfsvalidator.cli.Main; // For loadAndValidate().
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
          // FIXME. Jetty's filePart.write() seems like it could fail if for no
          // other reason than a full filesystem.  However I don't see where it
          // returns an error code or could throw an exception.
          filePart.write(tempFile.getName());
          try (InputStream inputStream = filePart.getInputStream();
              var seekableByteChannel =
                  Files.newByteChannel(Paths.get(tempFile.getPath()), StandardOpenOption.READ);
              ZipFile zf = new ZipFile(seekableByteChannel)) {
            if (saneZipArchive(zf)) {
              System.out.println("Zip file upload is sane.");
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
      logger.atSevere().withCause(e).log("Cannot load validator classes.");
      res.status(500);
      // TODO show a better error page so the user isn't confused, give them
      // instructions on how to diagnose or get support.
      return "Server error.";
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
      res.status(500);
      return "Server error.";

    } catch (InterruptedException e) {
      logger.atSevere().withCause(e).log("Validation was interrupted");
      res.status(500);
      return "Server error.";
    }

    return ViewerIndex.IndexHtmlAll(
        "var report = " + prettyGson.toJson(noticeContainer.exportValidationNotices()) + ";");
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
  // Maximum uncompressed size for any file within the zip file, 512 MB.
  // Largest I've seen in wild is 190M (stop_times.txt).
  public static int MaxZipEntrySize = 512 * 1024 * 1024;
  // Maximum zip file size, 128 MB. Largest I've seen in wild is 33M.
  public static int MaxZipFileSize = 128 * 1024 * 1024;
  // Maximum number of zip file entries.
  public static int MaxZipFileEntries = 128;

  public static boolean saneZipArchive(ZipFile zf) {
    // Sanity-check a zip file before we pass it to the GTFS validator.
    System.out.printf("ZipFile is %s\n", zf);
    ZipArchiveEntry ze;
    int entriesCount = 0;
    try {
      for (var e = zf.getEntries(); e.hasMoreElements(); ) {
        ze = e.nextElement();

        if (entriesCount++ > MaxZipFileEntries) {
          System.out.printf(
              "Found %d entries; more than the maximum %d, aborting.\n",
              entriesCount, MaxZipFileEntries);
          return false;
        }

        System.out.printf("Found ZipEntry %s.\n", ze.getName());
        if (ze.getName().contains("\\")
            || ze.getName().contains("/")
            || ze.getName().contains("..")) {
          System.out.printf("Entry name looks like it could be a directory, aborting.\n");
          return false;
        }

        try (InputStream zei = zf.getInputStream(ze)) {
          // Zip headers don't always indicate the size, and when they do
          // they're not always accurate.
          //
          // Since the goal here is to reject huge files before passing them to
          // the validator, verify a file's size by reading its uncompressed
          // contents.
          long fileSize = 0;
          while (zei.read() != -1) {
            if (fileSize++ > MaxZipEntrySize) {
              System.out.printf(
                  "Entry uncompressed size is larger than the maximum of %d, aborting.\n",
                  MaxZipEntrySize);
              return false;
            }
          }
          System.out.printf(
              "Read %d bytes from file, header says it should have %d bytes.\n",
              fileSize, ze.getSize());
        }
      }
    } catch (IOException e) {
      System.out.println("IO exception when reading zip.");
      return false;
    }
    return true;
  }
}
