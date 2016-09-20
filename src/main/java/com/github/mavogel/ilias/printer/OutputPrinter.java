package com.github.mavogel.ilias.printer;/*
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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Base class for all printers.
 * <p>
 * First comes prefix, then content and finally the suffix.
 *
 * Created by mavogel on 9/20/16.
 */
public abstract class OutputPrinter {

    protected final String title;

    /**
     * C'tor
     *
     * @param title the optional title of the document
     */
    protected OutputPrinter(final String title) {
        this.title = title;
    }

    /**
     * Prints the prefix or prelude for the output file.
     *
     * @return the prefix string
     */
    protected abstract String prefix();

    /**
     * Prints the prefix or prelude for the output file.
     *
     * @return the prefix string
     */
    protected abstract String content();

    /**
     * Prints the suffix for the output file.
     *
     * @return the suffix string
     */
    protected abstract String suffix();

    /**
     * Prints the prefix, content and output to the given print stream.
     *
     * @param out the out print stream.
     */
    public void print(OutputStream out) {
        try {
            out.write(prefix().getBytes(Charset.forName("UTF-8")));
            out.write(content().getBytes(Charset.forName("UTF-8")));
            out.write(suffix().getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {}
        }
    }
}
