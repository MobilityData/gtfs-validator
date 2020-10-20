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
import JsonDropzone from "../JsonDropzone";
import renderer from "react-test-renderer";

describe('JsonDropzone', () => {
    it('should be defined', () => {
        expect(JsonDropzone).toBeDefined();
    });

    it("should render correctly when on drag is not active", () => {
        let mockGetRootProps = jest.fn(() => {
            return jest.fn();
        });
        let mockGetInputProps = jest.fn(() => {
            return jest.fn();
        });
        let mockIsDragActive = false;
        let mockOnDrop = jest.fn();
        let mockUseDropZone = jest.fn(() => {
            return {
                getRootProps: mockGetRootProps,
                getInputProps: mockGetInputProps,
                isDragActive: mockIsDragActive
            };
        });
        const tree = renderer.create(
            <JsonDropzone id="id value" onDrop={mockOnDrop} accept="application/json" useDropzone={mockUseDropZone}/>
        ).toJSON();
        expect(tree).toMatchSnapshot();
    })

    it("should render correctly when on drag is active", () => {
        let mockGetRootProps = jest.fn();
        let mockGetInputProps = jest.fn();
        let mockIsDragActive = true;
        let mockUseDropZone = jest.fn(() => {
            return {
                getRootProps: mockGetRootProps,
                getInputProps: mockGetInputProps,
                isDragActive: mockIsDragActive
            };
        });
        let mockOnDrop = jest.fn();
        const tree = renderer.create(
            <JsonDropzone id="id value" onDrop={mockOnDrop} accept="application/json" useDropzone={mockUseDropZone}/>
        ).toJSON();
        expect(tree).toMatchSnapshot();
    })

    it("should call method when dropping element onto zone", () => {
        let mockOnDrop = jest.fn();
        let mockGetRootProps = jest.fn();
        let mockGetInputProps = jest.fn();
        let mockIsDragActive = false;
        let mockUseDropZone = jest.fn(() => {
            return {
                getRootProps: mockGetRootProps,
                getInputProps: mockGetInputProps,
                isDragActive: mockIsDragActive
            };
        });
        const tree = renderer.create(
            <JsonDropzone id="id value" onDrop={mockOnDrop} accept={"application/json"} useDropzone={mockUseDropZone}/>
        );
        tree.root.props.onDrop();
        expect(mockOnDrop).toHaveBeenCalledTimes(1);
    })
});
