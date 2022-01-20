/*
 * Copyright 2020-2021 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.viewer;

public class ViewerIndex {
  public static String IndexHtmlAll(String reportJsonJs) {
    return "<html lang=en>\n"
        + "    <head>\n"
        + "        <meta charset=\"utf-8\" />\n"
        + "        <title>GTFS Validator Report</title>\n"
        + "        <!-- index.css -->\n"
        + "        <style>\n"
        + ViewerAssets.IndexCss()
        + "        </style>\n"
        + "        <script> <!-- report.json.js -->\n"
        + reportJsonJs
        + "        </script>\n"
        + "        <script><!-- snabbdom.browser.js -->\n"
        + ViewerAssets.SnabbdomBrowserJs()
        + "        </script>\n"
        + "        <script><!-- index.js -->\n"
        + ViewerAssets.IndexJs()
        + "        </script>\n"
        + "    </head>\n"
        + "    <body onload=\"ReportView.render()\">\n"
        + "        <div id=\"container\">\n"
        + "            Snabbdom container div for ReportView.\n"
        + "        </div>\n"
        + "    </body>\n"
        + "</html>\n";
  }
}
