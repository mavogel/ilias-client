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
    public void shouldParseAValidChoiceFromUser() throws Exception {
        // == prepare
        final List<String> choices = new ArrayList<>(Arrays.asList("A", "B", "C"));

        // == train
        Scanner scanner = PowerMockito.mock(Scanner.class);
        PowerMockito.whenNew(Scanner.class).withArguments(System.in).thenReturn(scanner);
        PowerMockito.when(scanner.nextLine()).thenReturn("1");

        // == go
        int parseChoicesFromUser = IOUtils.getAndParseSingleChoiceFromUser(choices);

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
        int parseChoicesFromUser = IOUtils.getAndParseSingleChoiceFromUser(choices);

        // == verify
        assertEquals(2, parseChoicesFromUser);
    }

//    @Test
//    @Ignore(value = "Easymock in inifinite loop")
//    public void shouldErrorWrongInputAndThenAcceptCorrect() throws Exception {
//        // == prepare
//        final List<String> choices = new ArrayList<>(Arrays.asList("A", "B", "C"));
//
//        // == train
//        Scanner scanner = PowerMock.createNiceMockAndExpectNew(Scanner.class, System.in);
//        EasyMock.expect(scanner.nextLine()).andReturn("5").andReturn("2");
//        PowerMock.replayAll();
//
//        // == go
//        int parseChoicesFromUser = IOUtils.getAndParseSingleChoiceFromUser(choices);
//
//        // == verify
//        PowerMock.verifyAll();
//        assertEquals(2, parseChoicesFromUser);
//    }
}
