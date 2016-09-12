package com.github.mavogel.ilias.utils;

import com.github.mavogel.ilias.model.RegistrationPeriod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void shouldAcceptSecondInputDueToLetterInput() throws Exception {
        // == prepare
        final List<String> choices = new ArrayList<>(Arrays.asList(
                "A", "B", "C", "D",
                "E", "F", "G", "H",
                "U", "J", "K", "L"));

        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn("g").thenReturn("1");

        // == go
        List<Integer> madeChoices = IOUtils.readAndParseChoicesFromUser(choices);

        // == verify
        assertEquals(Arrays.asList(1), madeChoices);
    }

    @Test
    public void shouldAcceptSecondInputDueIncompleteRange() throws Exception {
        // == prepare
        final List<String> choices = new ArrayList<>(Arrays.asList(
                "A", "B", "C", "D",
                "E", "F", "G", "H",
                "U", "J", "K", "L"));

        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn("5-").thenReturn("1");

        // == go
        List<Integer> madeChoices = IOUtils.readAndParseChoicesFromUser(choices);

        // == verify
        assertEquals(Arrays.asList(1), madeChoices);
    }

    @Test
    public void shouldParseValidRegistrationPeriod() throws Exception {
        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine())
                .thenReturn("2014-01-11T14:00:00") // start
                .thenReturn("2014-01-22T16:00:00"); // end

        // == go
        RegistrationPeriod registrationPeriod = IOUtils.readAndParseRegistrationDates();

        // == verify
        assertEquals(LocalDateTime.of(2014, Month.JANUARY, 11, 14, 00), registrationPeriod.getRegistrationStart());
        assertEquals(LocalDateTime.of(2014, Month.JANUARY, 22, 16, 00), registrationPeriod.getRegistrationEnd());
    }

    @Test
    public void shouldReadRegistrationStartSeveralTimes() throws Exception {
        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine())
                .thenReturn("2014-01-31T54:00:00")
                .thenReturn("20140:00")
                .thenReturn("2014-01-3114:00:00")
                .thenReturn("2014-01-11T14:00:00") // start
                .thenReturn("2013-12-23T16:00:00")
                .thenReturn("2014-01-22T16:00:00"); // end

        // == go
        RegistrationPeriod registrationPeriod = IOUtils.readAndParseRegistrationDates();

        // == verify
        assertEquals(LocalDateTime.of(2014, Month.JANUARY, 11, 14, 00), registrationPeriod.getRegistrationStart());
        assertEquals(LocalDateTime.of(2014, Month.JANUARY, 22, 16, 00), registrationPeriod.getRegistrationEnd());
    }

    @Test
    public void shouldParseValidUserConfirmation() throws Exception {
        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn(" Y   " );

        // == go
        boolean confirmation = IOUtils.readAndParseUserConfirmation();

        // == verify
        assertTrue(confirmation);
    }

    @Test
    public void shouldParseValidNegativeUserConfirmation() throws Exception {
        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn(" N   " );

        // == go
        boolean confirmation = IOUtils.readAndParseUserConfirmation();

        // == verify
        assertFalse(confirmation);
    }

    @Test
    public void shouldRequestUserConfirmationMultipleTimes() throws Exception {
        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine())
                .thenReturn(" blalaa   " )
                .thenReturn("NO")
                .thenReturn("  Y");

        // == go
        boolean confirmation = IOUtils.readAndParseUserConfirmation();

        // == verify
        assertTrue(confirmation);
    }

}
