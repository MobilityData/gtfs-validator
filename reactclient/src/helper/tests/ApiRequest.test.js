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

import {initConfig} from "../ApiRequest.js"
import mockAxios from "axios";
import {displayValidationReport, validateFeed} from "../ApiRequest";
jest.mock('axios')
// jest.mock('react')
jest.mock('../DomInteractions')

describe("InitConfig", () => {
    it("should de defined", () => {
        expect(initConfig).toBeDefined()
    })

    it("should call post method with correct arguments", () => {
        initConfig(mockAxios, "8090", "/actions/initialize/", "test");
        expect(mockAxios.post).toHaveBeenCalledTimes(1);
        expect(mockAxios.post).toHaveBeenCalledWith("http://localhost:8090/actions/initialize/", "test");
    })
});

describe("ValidateFeed", () => {
    it("should de defined", () => {
        expect(validateFeed).toBeDefined()
    })

    it("should call get method with correct argument", async () => {
        await validateFeed();
        done();
        expect(mockAxios.get).toHaveBeenCalledTimes(1);
        expect(mockAxios.get).toHaveBeenCalledWith("http://localhost:8090/actions/runvalidator");
    })
});

describe("DisplayValidationReport", () => {
    it("should de defined", () => {
        expect(displayValidationReport).toBeDefined()
    })

    it("should call get method with correct argument", () => {
        displayValidationReport()
        expect(mockAxios.get).toHaveBeenCalledTimes(1);
        expect(mockAxios.get).toHaveBeenCalledWith("http://localhost:8090/actions/openreport");
    })
})
