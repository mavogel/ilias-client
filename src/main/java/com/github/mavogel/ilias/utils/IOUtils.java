package com.github.mavogel.ilias.utils;

import org.apache.commons.lang3.StringUtils;

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

        while (!(isCorrectInputDigits && isCorrectInputRanges)) {
            try (Scanner scanner = new Scanner(System.in)) {
                line = scanner.nextLine();
                List<String> trimmedSplit = Arrays.stream(line.split(","))
                        .map(StringUtils::deleteWhitespace)
                        .collect(Collectors.toList());

                // digits
                digitsInput = trimmedSplit.stream()
                        .filter(s -> digit.matcher(s).matches())
                        .map(Integer::valueOf)
                        .collect(Collectors.toList());
                isCorrectInputDigits = digitsInput.stream().allMatch(idx -> isInRange(choices, idx));

                // ranges
                rangesInput = trimmedSplit.stream()
                        .filter(s -> range.matcher(s).matches())
                        .map(r -> r.split("-"))
                        .collect(Collectors.toList());
                isCorrectInputRanges = rangesInput.stream().allMatch(r -> isInMeaningfulRange(choices, Integer.valueOf(r[0]), Integer.valueOf(r[1])));
            } catch (NumberFormatException nfe) {
                if (!isCorrectInputDigits) {
                    System.err.println("'" + line + " contains incorrect indexes! Try again");
                }
                if (!isCorrectInputRanges) {
                    System.err.println("'" + line + " contains incorrect ranges! Try again");
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
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

        while (!isCorrectInput) {
            try (Scanner scanner = new Scanner(System.in)) {
                line = scanner.nextLine();
                userChoice = Integer.valueOf(line);
                isCorrectInput = isInRange(choices, userChoice);
            } catch (NumberFormatException nfe) {
                System.err.println("'" + line + " is not a number! Try again");
            } catch (IllegalArgumentException iae) {
                System.err.println(iae.getMessage());
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
}