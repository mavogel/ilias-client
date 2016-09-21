package com.github.mavogel.ilias.printer;

import org.junit.Test;

import static org.junit.Assert.*;
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

/**
 * Created by mavogel on 9/21/16.
 */
public class OutputTypeTest {

    @Test
    public void shouldGetAtIndex() throws Exception {
        // == go
        VelocityOutputPrinter.OutputType atIndex0 = VelocityOutputPrinter.OutputType.getAtIndex(0);
        VelocityOutputPrinter.OutputType atIndex1 = VelocityOutputPrinter.OutputType.getAtIndex(1);

        // == verify
        assertEquals(VelocityOutputPrinter.OutputType.HTML, atIndex0);
        assertEquals(VelocityOutputPrinter.OutputType.LATEX, atIndex1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeFoundAtIndex() throws Exception {
        try {
            // == go
            VelocityOutputPrinter.OutputType.getAtIndex(55);
        } catch (Exception e) {
            // == verify
            assertTrue(e.getMessage().contains("not found"));
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeFoundAtNegativeIndex() throws Exception {
        try {
            // == go
            VelocityOutputPrinter.OutputType.getAtIndex(-4);
        } catch (Exception e) {
            // == verify
            assertTrue(e.getMessage().contains("not found"));
            throw e;
        }
    }

}