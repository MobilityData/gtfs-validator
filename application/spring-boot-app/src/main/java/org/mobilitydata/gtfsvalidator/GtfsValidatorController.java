/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("actions")
@Component
public class GtfsValidatorController {
    private final ServiceManager serviceManager;

    public GtfsValidatorController(final ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @GetMapping("/initconfig/{jsonAsString}")
    public void initConfig(@PathVariable("jsonAsString") final String execParamAsJsonString) throws Exception {
        serviceManager.initConfig(execParamAsJsonString);
    }

    @GetMapping("/runvalidator")
    @ResponseBody
    public String validateFeed() {
        try {
            return serviceManager.validateFeed();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/getreportcontent")
    @ResponseBody
    public String getPathToReport() {
        return serviceManager.getPathToReport();
    }
}
