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

/**
 * Default port to be use
 * @returns {string} the default port to be used
 * @constructor
 */
export const Port = () => "8090";

/**
 * The command to run the validator
 * @returns {string} the command to run the validator
 * @constructor
 */
export const RunValidatorCommand = () => "/actions/runvalidator";

/**
 * The command to open the validation report
 * @returns {string} the  command to open the validation report
 * @constructor
 */
export const OpenReportContentCommand = () => "/actions/openreport";

/**
 * the command to initialize the gtfs-validator DefaultConfig
 * @returns {string}
 * @constructor
 */
export const InitConfigCommand = () => "/actions/initialize/";
