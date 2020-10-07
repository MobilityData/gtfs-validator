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

import React from "react";
import JsonBeautyfier from "../JsonBeautyfier";
import renderer from "react-test-renderer";

describe("JsonBeautyfier", () => {
    it("should be defined", () => {
        expect(JsonBeautyfier).toBeDefined();
    });

    it("should render correctly without data", () => {
        const tree = renderer.create(
            <JsonBeautyfier />
        ).toJSON();
        expect(tree).toMatchSnapshot();
    })

    it("should render correctly with data", () => {
        let mockData = {
            "key1": "value1",
            "key2": "value2"
        }
        const tree = renderer.create(
            <JsonBeautyfier data={mockData}/>
        ).toJSON();
        expect(tree).toMatchSnapshot();
    })
});
