package org.mobilitydata.gtfsvalidator.model

import java.io.Serializable

/*
 * Original work Copyright (C) 2011-2017 Nipuna Gunathilake, University of South Florida.
 * Modified work Copyright (c) 2019. MobilityData IO. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */   data class ValidationRule(val errorId: String?,
                                val severity: String?,
                                val title: String?,
                                val errorDescription: String?,
                                val occurrenceSuffix: String?) : Serializable