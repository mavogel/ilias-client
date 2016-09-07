package com.github.mavogel.ilias.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Created by mavogel on 9/7/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IOUtils.class, IOUtilsTest.class, Scanner.class})
public class IOUtilsTest {

    @Test
    public void shouldParseAValidChoice() throws Exception {
        // == prepare
        final List<String> choices = new ArrayList<>(Arrays.asList("A", "B", "C"));

        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn("1");

        // == go
        int parseChoicesFromUser = IOUtils.readAndParseSingleChoiceFromUser(choices);

        // == verify
        assertEquals(1, parseChoicesFromUser);
    }

    @Test
    public void shouldErrorWrongInputAndThenAcceptCorrect() throws Exception {
        // == prepare
        final List<String> choices = new ArrayList<>(Arrays.asList("A", "B", "C"));

        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn("5").thenReturn("a").thenReturn("2");

        // == go
        int parseChoicesFromUser = IOUtils.readAndParseSingleChoiceFromUser(choices);

        // == verify
        assertEquals(2, parseChoicesFromUser);
    }

    @Test
    public void shouldParseASingleDigitAndRange() throws Exception {
        // == prepare
        final List<String> choices = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G"));

        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn("1, 4-6");

        // == go
        List<Integer> madeChoices = IOUtils.readAndParseChoicesFromUser(choices);

        // == verify
        assertEquals(Arrays.asList(1, 4, 5, 6), madeChoices);
    }

    @Test
    public void shouldParseSingleDigitsAndRanges() throws Exception {
        // == prepare
        final List<String> choices = new ArrayList<>(Arrays.asList(
                "A", "B", "C", "D",
                "E", "F", "G", "H",
                "U", "J", "K", "L"));

        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn("1,  8- 11  , 4-6, 2");

        // == go
        List<Integer> madeChoices = IOUtils.readAndParseChoicesFromUser(choices);

        // == verify
        assertEquals(Arrays.asList(1, 2, 4, 5, 6, 8, 9, 10, 11), madeChoices);
    }

    @Test
    public void shouldAcceptSecondInputDueToBogusRange() throws Exception {
        // == prepare
        final List<String> choices = new ArrayList<>(Arrays.asList(
                "A", "B", "C", "D",
                "E", "F", "G", "H",
                "U", "J", "K", "L"));

        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn("1,  8- 115  , 4-6, 2, 5- 3")
                                             .thenReturn("10, 5 -4, 3")
                                             .thenReturn("4-8");

        // == go
        List<Integer> madeChoices = IOUtils.readAndParseChoicesFromUser(choices);

        // == verify
        assertEquals(Arrays.asList(4, 5, 6, 7, 8), madeChoices);
    }

}
