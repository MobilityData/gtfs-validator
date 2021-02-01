package org.mobilitydata.gtfsvalidator.util;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class GeospatialUtilTest {

  @Test
  public void distanceFromToSameCoordinateIsZero() {
    assertThat(GeospatialUtil.distanceBetweenMeter(45.508888, -73.561668, 45.508888, -73.561668))
        .isEqualTo(0);
  }

  @Test
  public void distanceReferenceCheck() {
    // geographic data extracted and validated with an external tool
    assertThat(GeospatialUtil.distanceBetweenMeter(45.508888, -73.561668, 45.507753, -73.562677))
        .isEqualTo(148);
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void stopWithinTripShapeBufferShouldNotGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void stopOutsideTripShapeBufferShouldGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void twoTripsWithSameShapeStopOutsideBufferShouldGenerateOneNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void tripWithoutShapeShouldNotGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void stopWithoutLocationShouldNotGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void stopLocationTypeNotZeroOrFourShouldNotGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void nullStopShouldNotGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void nullTripShouldNotGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void nullStopTimeShouldNotGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void emptyStopTimesShouldNotGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void nullShapePointsShouldNotGenerateNotice() {
    // TODO: implement
  }

  /**
   * Test geospatial implementation
   *
   * <p>See map of trip shape and stops (in GeoJSON) at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
   *
   * <p>For debugging, you can export a JTS-version of the buffer in WKT format using code at
   * https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a#gistcomment-3425554. The WKT
   * output can then be visualized at https://arthur-e.github.io/Wicket/sandbox-gmaps3.html.
   *
   * <p>The spatial4j version of the buffer can't easily be visualized using GeoJSON or WKT because
   * it uses a LineString and a proprietary "buffer" extension to GeoJSON and WKT, which most tools
   * don't support.
   */
  @Test
  public void emptyShapePointsShouldNotGenerateNotice() {
    // TODO: implement
  }
}
