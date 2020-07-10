/*
 * Copyright (c) 2020. MobilityData IO.
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
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.RouteColorAndTextInsufficientContrastNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.Collection;

/**
 * Use case to validate that a Route color contrast with a Route text color.
 */
public class ValidateRouteColorAndTextContrast {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     */
    public ValidateRouteColorAndTextContrast(final GtfsDataRepository dataRepo,
                                             final ValidationResultRepository resultRepo,
                                             final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if Route color does not contrast enough with Route text color
     * for every Routes in a {@link GtfsDataRepository}. A new notice is generated each time this condition is true.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E025 - Insufficient route color contrast'" + System.lineSeparator());
        Collection<Route> routes = dataRepo.getRouteAll();
        routes.stream()
                .filter(route -> !areContrasting(route.getRouteColor(), route.getRouteTextColor()))
                .forEach(route -> resultRepo.addNotice(new RouteColorAndTextInsufficientContrastNotice("routes.txt",
                        route.getRouteId(), contrast(route.getRouteColor(), route.getRouteTextColor()))));
    }

    /**
     * @param color an hexadecimal string representing a color. Ex. a5ff00
     * @return the resulting luminance for the color
     */
    private double luminance(String color) {
        int r = Integer.parseInt(color.substring(0, 2), 16);
        int g = Integer.parseInt(color.substring(2, 4), 16);
        int b = Integer.parseInt(color.substring(4, 6), 16);

        //W3 G17 formula is used to compute the luminance
        //Ref. https://www.w3.org/TR/WCAG20-TECHS/G17.html#G17-procedure
        return (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }

    /**
     * @param routeColor an hexadecimal string representing a Route color. Ex. a5ff00
     * @param textColor  an hexadecimal string representing a Route text color. Ex. a5ff00
     * @return the contrast ratio between the 2 colors
     */
    private double contrast(String routeColor, String textColor) {
        double routeLuminance = luminance(routeColor);
        double textLuminance = luminance(textColor);
        double brightest = Math.max(routeLuminance, textLuminance);
        double darkest = Math.min(routeLuminance, textLuminance);

        return (brightest + 0.05) / (darkest + 0.05);
    }

    /**
     * Verifies if 2 colors are contrasting according to W3C recommendations
     * Ref. https://www.w3.org/TR/WCAG20-TECHS/G17.html#G17-procedure
     *
     * @param routeColor an hexadecimal string representing a Route color. Ex. a5ff00
     * @param textColor  an hexadecimal string representing a Route text color. Ex. a5ff00
     * @return true if contrast ratio between the 2 colors is higher than 4.5, false if not.
     */
    private boolean areContrasting(String routeColor, String textColor) {
        boolean areContrasting = true;
        if (routeColor != null && textColor != null) {
            areContrasting = contrast(routeColor, textColor) >= 4.5;
        }
        return areContrasting;
    }
}