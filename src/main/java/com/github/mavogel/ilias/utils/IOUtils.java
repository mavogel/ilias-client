/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */
package com.github.mavogel.ilias.utils;

import com.github.mavogel.ilias.model.RegistrationPeriod;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by mavogel on 9/7/16.
 */
public class IOUtils {

    private static Logger LOG = Logger.getLogger(IOUtils.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Reads and parses a multiple choices from the user.<br>
     * Handles wrong inputs and ensures the choices are meaningful (lo <= up) and in
     * the range of the possible choices.
     *
     * @param choices the possible choices
     * @return the choice of the user.
     */
    public static List<Integer> readAndParseChoicesFromUser(final List<?> choices) {
        boolean isCorrectInputDigits = false, isCorrectInputRanges = false;
        String line = null;
        final Pattern range = Pattern.compile("(\\d+)-(\\d+)");
        final Pattern digit = Pattern.compile("\\d+");
        List<Integer> digitsInput = null;
        List<String[]> rangesInput = null;

        Scanner scanner = new Scanner(System.in);
        while (!(isCorrectInputDigits && isCorrectInputRanges)) {
            try {
                LOG.info("Your choice: \n - Comma separated choices and/or ranges (e.g.: 1, 2, 4-6, 8, 10-15 -> or a combination) \n - The Wildcard 'A' for selecting all choices");
                line = scanner.nextLine();

                // == step 1: check for wild card first
                if (StringUtils.deleteWhitespace(line).equalsIgnoreCase(Defaults.CHOICE_WILDCARD.toLowerCase())) {
                    return IntStream.range(0, choices.size()).mapToObj(Integer::valueOf).collect(Collectors.toList());
                }

                List<String> trimmedSplit = Arrays.stream(line.split(","))
                        .map(StringUtils::deleteWhitespace)
                        .collect(Collectors.toList());
                
                // == step 2: checks for invalids
                Optional<String> invalidChoice = trimmedSplit.stream()
                        .filter(s -> !digit.matcher(s).matches() && !range.matcher(s).matches())
                        .findAny();
                if (invalidChoice.isPresent()) throw new IllegalArgumentException("Contains invalid indexes and/or ranges or an invalid wildcard!");

                // == step 3: parse digits
                digitsInput = trimmedSplit.stream()
                        .filter(s -> digit.matcher(s).matches())
                        .map(Integer::valueOf)
                        .collect(Collectors.toList());
                isCorrectInputDigits = digitsInput.stream().allMatch(idx -> isInRange(choices, idx));

                // == step 4: parse ranges
                rangesInput = trimmedSplit.stream()
                        .filter(s -> range.matcher(s).matches())
                        .map(r -> r.split("-"))
                        .collect(Collectors.toList());
                isCorrectInputRanges = rangesInput.stream().allMatch(r -> isInMeaningfulRange(choices, Integer.valueOf(r[0]), Integer.valueOf(r[1])));
            } catch (NumberFormatException nfe) {
                if (!isCorrectInputDigits) {
                    LOG.error("'" + line + "' contains incorrect indexes! Try again");
                }
                if (!isCorrectInputRanges) {
                    LOG.error("'" + line + "' contains incorrect ranges! Try again");
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }

        return Stream.concat(digitsInput.stream(), expandRanges(rangesInput).orElse(Stream.empty()))
                .sorted()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Reads and parses a single choice from the user.<br>
     * Handles wrong inputs and ensures the choice is in
     * the range of the possible choices.
     *
     * @param choices the possible choices
     * @return the choice of the user.
     */
    public static int readAndParseSingleChoiceFromUser(final List<?> choices) {
        boolean isCorrectInput = false;
        String line = null;
        int userChoice = -1;

        Scanner scanner = new Scanner(System.in);
        while (!isCorrectInput) {
            try {
                LOG.info("A single choice only! (E.g.: 1)");
                line = scanner.nextLine();
                userChoice = Integer.valueOf(line);
                isCorrectInput = isInRange(choices, userChoice);
            } catch (NumberFormatException nfe) {
                LOG.error("'" + line + "' is not  a number! Try again");
            } catch (IllegalArgumentException iae) {
                LOG.error(iae.getMessage());
            }
        }

        return userChoice;
    }

    /**
     * Checks if the given index is in the range of the list
     *
     * @param list  the list
     * @param index the index to check
     * @return <code>true</code> if the indes is in the range of the list
     * @throws IllegalArgumentException if the index is not in the range.
     */

    private static boolean isInRange(final List<?> list, final int index) {
        if (index >= 0 && index < list.size()) {
            return true;
        } else {
            throw new IllegalArgumentException("Choice '" + index + "' is not in range! Try again");
        }
    }

    /**
     * Checks if the given range of the array is in meaningful sense. <br>
     * <ul>
     * <li>lower is less or equals than upper</li>
     * <li>both bounds are in the range of the array</li>
     * </ul>
     *
     * @param choices the list of choices
     * @param lower   the lower bound
     * @param upper   the upper bound
     * @return <code>true</code> if the bounds are meanigful, <code>false</code> otherwise.
     */
    private static boolean isInMeaningfulRange(final List<?> choices, final int lower, final int upper) {
        return lower <= upper && isInRange(choices, lower) && isInRange(choices, upper);
    }

    /**
     * Expands the ranges.<br>
     * Example:
     * <ul>
     * <li>1-4 -> 1,2,3,4</li>
     * <li>1-4,6-8 -> 1,2,3,4,6,7,8</li>
     * </ul>
     *
     * @param ranges the ranges to expand
     * @return the expanded ranges.
     */
    private static Optional<Stream<Integer>> expandRanges(List<String[]> ranges) { // TODO maybe as Integer[]
        return ranges.stream()
                .map(eachRange -> IntStream.rangeClosed(Integer.valueOf(eachRange[0]), Integer.valueOf(eachRange[1])))
                .map(eachExpandedRange -> eachExpandedRange.mapToObj(Integer::valueOf))
                .reduce(Stream::concat);
    }

    /**
     * Reads and parses the registration period.<br>
     * Validates the format and that the start is after the end.
     *
     * @return the {@link RegistrationPeriod}
     */
    public static RegistrationPeriod readAndParseRegistrationDates() {
        LOG.info("Date need to be of the format 'yyyy-MM-ddTHH:mm'. E.g.: 2016-04-15T13:00"); // TODO get from the formatter
        LocalDateTime registrationStart = null, registrationEnd = null;
        boolean validStart = false, validEnd = false;

        Scanner scanner = new Scanner(System.in);
        while (!validStart) {
            LOG.info("Registration start: ");
            String line = scanner.nextLine();
            try {
                registrationStart = LocalDateTime.parse(line, DATE_FORMAT);
                validStart = true;
            } catch (DateTimeParseException dtpe) {
                LOG.error("'" + line + "' is not a valid date");
            }
        }

        while (!validEnd) {
            LOG.info("Registration end:  ");
            String line = scanner.nextLine();
            try {
                registrationEnd = LocalDateTime.parse(line, DATE_FORMAT);
                validEnd = registrationStart.isBefore(registrationEnd);
                if (!validEnd) {
                    LOG.error("End of registration has to be after the start'" + registrationStart + "'");
                }
            } catch (DateTimeParseException dtpe) {
                LOG.error("'" + line + "' is not a valid date");
            }
        }

        return new RegistrationPeriod(registrationStart, registrationEnd);
    }

    /**
     * Reads and parses the confirmation of the user for an action.
     *
     * @return <code>true</code> if the user confirmed with Y for YES, <code>false</code> otherwise.
     */
    public static boolean readAndParseUserConfirmation() {
        LOG.info("Confirm please: [Y] or [N]");
        boolean validChoice = false;
        boolean choice = false;

        Scanner scanner = new Scanner(System.in);
        while (!validChoice) {
            String line = scanner.nextLine();
            if (StringUtils.deleteWhitespace(line).equalsIgnoreCase("Y")) {
                choice = true;
                validChoice = true;
            } else if (StringUtils.deleteWhitespace(line).equalsIgnoreCase("N")) {
                choice = false;
                validChoice = true;
            } else {
                LOG.error("Invalid choice. Try again!");
            }
        }

        return choice;
    }
}
