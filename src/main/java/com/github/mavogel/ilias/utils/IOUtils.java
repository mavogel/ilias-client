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
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Util class for operations of reading, parsing and validating user input.
 * <p>
 * Created by mavogel on 9/7/16.
 */
public class IOUtils {

    private static Logger LOG = Logger.getLogger(IOUtils.class);

    /**
     * Reads and parses a multiple choices from the user.<br>
     * Handles wrong inputs and ensures the choices are meaningful (lo <= up) and in
     * the range of the possible choices.
     *
     * @param choices the possible choices
     * @return the choice of the user.
     */
    public static List<Integer> readAndParseChoicesFromUser(final List<?> choices) {
        boolean isCorrectDigits = false, isCorrectRanges = false;
        String line = null;
        List<Integer> digits = null;
        List<String[]> ranges = null;

        Scanner scanner = new Scanner(System.in);
        while (!(isCorrectDigits && isCorrectRanges)) {
            try {
                LOG.info("Your choice: \n - Comma separated choices and/or ranges (e.g.: 1, 2, 4-6, 8, 10-15 -> or a combination) \n - The Wildcard 'A' for selecting all choices");
                line = scanner.nextLine();

                if (containsWildcard(line)) {
                    return IntStream.range(0, choices.size()).mapToObj(Integer::valueOf).collect(Collectors.toList());
                }
                // == 1
                List<String> trimmedSplit = splitAndTrim(line);
                checkForInvalidCharacters(trimmedSplit);

                // == 2
                digits = parseDigits(trimmedSplit);
                isCorrectDigits = checkInputDigits(choices, digits);

                // == 3
                ranges = parseRanges(trimmedSplit);
                isCorrectRanges = checkRanges(choices, ranges);
            } catch (NumberFormatException nfe) {
                if (!isCorrectDigits) {
                    LOG.error("'" + line + "' contains incorrect indexes! Try again");
                }
                if (!isCorrectRanges) {
                    LOG.error("'" + line + "' contains incorrect ranges! Try again");
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }

        return concatDigitsAndRanges(digits, ranges);
    }

    /**
     * Concatenates the digits and ranges to a distinct and sorted range: 1,3,4,6,7,...
     *
     * @param digitsInput the single digits
     * @param rangesInput the ranges @see {@link Defaults#RANGE_PATTERN} for details
     * @return the sorted range
     */
    private static List<Integer> concatDigitsAndRanges(final List<Integer> digitsInput, final List<String[]> rangesInput) {
        return Stream.concat(digitsInput.stream(), expandRanges(rangesInput).orElse(Stream.empty()))
                .sorted()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Splits with ',' and trims each token.
     *
     * @param line the line to split
     * @return the tokens
     */
    private static List<String> splitAndTrim(final String line) {
        return Arrays.stream(line.split(","))
                .map(StringUtils::deleteWhitespace)
                .collect(Collectors.toList());
    }

    /**
     * Checks the ranges if they are meaningful against the choices.
     *
     * @param choices the choices
     * @param ranges the ranges
     * @return true of they are correct, false otherwise
     */
    private static boolean checkRanges(final List<?> choices, final List<String[]> ranges) {
        return ranges.stream().allMatch(r -> isInMeaningfulRange(choices, Integer.valueOf(r[0]), Integer.valueOf(r[1])));
    }

    /**
     * Checks the digits if they are in range against the choices.
     *
     * @param choices the choices
     * @param digits the digits
     * @return true of they are correct, false otherwise
     */
    private static boolean checkInputDigits(final List<?> choices, final List<Integer> digits) {
        return digits.stream().allMatch(idx -> isInRange(choices, idx));
    }

    /**
     * Parses the ranges from the tokens.
     *
     * @param tokens the tokens @see {@link IOUtils#splitAndTrim(String)}
     * @return the ranges
     */
    private static List<String[]> parseRanges(final List<String> tokens) {
        return tokens.stream()
                .filter(s -> Defaults.RANGE_PATTERN.matcher(s).matches())
                .map(r -> r.split("-"))
                .collect(Collectors.toList());
    }

    /**
     * Parses the digits from the tokens
     *
     * @param tokens the tokens @see {@link IOUtils#splitAndTrim(String)}
     * @return the tokens
     */
    private static List<Integer> parseDigits(final List<String> tokens) {
        return tokens.stream()
                .filter(s -> Defaults.DIGIT_PATTERN.matcher(s).matches())
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * Checks for valid characters in tokens.
     *
     * @param tokens the tokens @see {@link IOUtils#splitAndTrim(String)}
     * @throws IllegalArgumentException if there is an invalid token
     */
    private static void checkForInvalidCharacters(final List<String> tokens) {
        Optional<String> invalidChoice = tokens.stream()
                .filter(s -> !Defaults.DIGIT_PATTERN.matcher(s).matches() && !Defaults.RANGE_PATTERN.matcher(s).matches())
                .findAny();
        if (invalidChoice.isPresent())
            throw new IllegalArgumentException("Contains invalid indexes and/or ranges or an invalid wildcard!");
    }


    /**
     * Checks if the line contains the {@link Defaults#CHOICE_WILDCARD} character.
     *
     * @param line the line to check
     * @return true if it contains the character, false otherwise
     */
    private static boolean containsWildcard(final String line) {
        return StringUtils.deleteWhitespace(line).equalsIgnoreCase(Defaults.CHOICE_WILDCARD.toLowerCase());
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
                line = StringUtils.deleteWhitespace(scanner.nextLine());
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
            String line = StringUtils.deleteWhitespace(scanner.nextLine());
            try {
                registrationStart = LocalDateTime.parse(line, Defaults.DATE_FORMAT);
                validStart = true;
            } catch (DateTimeParseException dtpe) {
                LOG.error("'" + line + "' is not a valid date");
            }
        }

        while (!validEnd) {
            LOG.info("Registration end:  ");
            String line = scanner.nextLine();
            try {
                registrationEnd = LocalDateTime.parse(line, Defaults.DATE_FORMAT);
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
     * Reads and parses a positive integer.
     *
     * @return the positive integer.
     */
    public static int readAndParsePositiveInteger() {
        boolean isCorrectInput = false;
        String line = null;
        int positiveInteger = -1;

        Scanner scanner = new Scanner(System.in);
        while (!isCorrectInput) {
            try {
                LOG.info("Enter a positive integer:");
                line = StringUtils.deleteWhitespace(scanner.nextLine());
                positiveInteger = Integer.valueOf(line);
                isCorrectInput = positiveInteger >= 0;
            } catch (NumberFormatException nfe) {
                LOG.error("'" + line + "' is not a positive integer! Try again");
            } catch (IllegalArgumentException iae) {
                LOG.error(iae.getMessage());
            }
        }

        return positiveInteger;
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

    /**
     * Just reads the next line from System in and deleting whitespaces.
     *
     * @return the line
     */
    public static String readLine() {
        Scanner scanner = new Scanner(System.in);
        String nextLine = scanner.nextLine();
        return StringUtils.deleteWhitespace(nextLine);
    }
}
