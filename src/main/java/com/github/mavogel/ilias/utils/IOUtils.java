package com.github.mavogel.ilias.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by mavogel on 9/7/16.
 */
public class IOUtils {

    public static List<Integer> getAndParseChoicesFromUser(final List<?> choices) {
        boolean isCorrectInputDigits = false, isCorrectInputRanges = false;
        String line = null;
        final Pattern range = Pattern.compile("^(\\\\d+)-(\\\\d+)$");
        final Pattern digit = Pattern.compile("^\\\\d+$");
        List<Integer> digitsInput = null;
        List<String[]> rangesInput = null;

        while (!(isCorrectInputDigits && isCorrectInputRanges)) {
            try (Scanner scanner = new Scanner(System.in)) {
                line = scanner.nextLine();
                String[] split = line.split(",");

                digitsInput = Arrays.stream(split).filter(digit.asPredicate()).map(Integer::valueOf).collect(Collectors.toList());
                isCorrectInputDigits = digitsInput.stream().allMatch(idx -> isInRange(choices, idx));

                rangesInput = Arrays.stream(split).filter(range.asPredicate()).map(r -> r.split("-")).collect(Collectors.toList());
                isCorrectInputRanges = rangesInput.stream().allMatch(r -> isInMeaningfulRange(r, Integer.valueOf(r[0]), Integer.valueOf(r[1])));
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

        return IntStream.concat(digitsInput.stream().mapToInt(Integer::intValue), expandRanges(rangesInput).orElse(IntStream.empty()))
                .sorted()
                .distinct()
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public static int getAndParseSingleChoiceFromUser(final List<?> choices) {
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
     *     <li>lower is less or equals than upppe</li>
     *     <li>both bounds are in the range of the array</li>
     * </ul>
     *
     * @param array the array
     * @param lower the lower bound
     * @param upper the upper bound
     * @return <code>true</code> if the bounds are meanigful, <code>false</code> otherwise.
     */
    private static boolean isInMeaningfulRange(final String[] array, final int lower, final int upper) {
        return lower <= upper && isInRange(Arrays.asList(array), lower) && isInRange(Arrays.asList(array), upper);
    }

    /**
     * Expands the ranges.<br>
     * Example:
     * <ul>
     *   <li>1-4 -> 1,2,3,4</li>
     *   <li>1-4,6-8 -> 1,2,3,4,6,7,8</li>
     * </ul>
     *
     * @param ranges the ranges to expand
     * @return the expanded ranges.
     */
    private static Optional<IntStream> expandRanges(List<String[]> ranges) { // TODO may as Integer[]
        return ranges.stream().map(s -> IntStream.rangeClosed(Integer.valueOf(s[0]), Integer.valueOf(s[1])))
                .reduce(IntStream::concat);
    }
}
