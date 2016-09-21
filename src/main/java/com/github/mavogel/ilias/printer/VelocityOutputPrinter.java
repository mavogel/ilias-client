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

import com.github.mavogel.ilias.utils.Defaults;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;

/**
 * Base class for all printers.
 * <p>
 * Created by mavogel on 9/20/16.
 */
public class VelocityOutputPrinter {

    private static Logger LOG = Logger.getLogger(VelocityOutputPrinter.class);

    /**
     * The possible output types.
     */
    public enum OutputType {
        HTML(0, ".html.vm"),
        LATEX(1, ".tex.vm");

        private final String templateExtension;
        private final int index;

        OutputType(final int index, final String templateExtension) {
            this.templateExtension = templateExtension;
            this.index = index;
        }

        public String getTemplateExtension() {
            return templateExtension;
        }

        public String getFileExtension() {
            return templateExtension.replace(".vm", "");
        }

        /**
         * Retrieves the {@link OutputType} at the given index.
         *
         * @param index the index
         * @return the {@link OutputType} at the index
         * @throws IllegalArgumentException if the index is out of range
         */
        public static VelocityOutputPrinter.OutputType getAtIndex(final int index) {
            return Arrays.stream(VelocityOutputPrinter.OutputType.values())
                    .filter(t -> t.index == index)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("OutputType at index '" + index + "' not found"));
        }

        /**
         * Returns a string for displaying the information of the {@link OutputType}
         * on the command line.
         *
         * @param prefix an optional header
         * @return the string
         */
        public String asDisplayString(final String prefix) {
            StringBuilder sb = new StringBuilder();
            if (prefix != null && !prefix.isEmpty()) sb.append(prefix);
            sb.append(this.name());
            return sb.toString();
        }
    }

    /**
     * Prints the header, content and output to the given print stream.
     *
     * @param outputType   the desired output type. @see {@link OutputType}
     * @param templateName the name of the template
     * @param contextMap   the context for velocity
     * @throws Exception in case of a error, so the caller can handle it
     */
    public static void print(final OutputType outputType, final String templateName, final Map<String, Object> contextMap) throws Exception {
        if (!templateName.contains(outputType.getTemplateExtension())) {
            LOG.error("Extension of output type '" + outputType.getTemplateExtension() + "' does not match with the templateExtension of the template.");
            throw new Exception();
        }

        Velocity.init();
        try {
            final Template template = Velocity.getTemplate(templateName);
            final VelocityContext context = new VelocityContext();
            contextMap.forEach((k, v) -> context.put(k, v));
            final Writer writer = new BufferedWriter(createFileWriter(outputType, templateName));
            template.merge(context, writer);
            writer.flush();
        } catch (ResourceNotFoundException rnfe) {
            LOG.error("Couldn't find the template with name '" + templateName + "'");
            throw new Exception(rnfe.getMessage());
        } catch (ParseErrorException pee) {
            LOG.error("Syntax error: problem parsing the template ' " + templateName + "': " + pee.getMessage());
            throw new Exception(pee.getMessage());
        } catch (MethodInvocationException mie) {
            LOG.error("An invoked method on the template '" + templateName + "' threw an error: " + mie.getMessage());
            throw new Exception(mie.getMessage());
        } catch (Exception e) {
            LOG.error("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates a file writer with a timestamp from the current output type and template. Write
     * a file in the current directory. Striped the folders off the name if the given template.
     *
     * @param outputType   the output type
     * @param templateName the name of the template
     * @return the FileWriter
     * @throws IOException if the file could not be created.
     */
    private static FileWriter createFileWriter(final OutputType outputType, final String templateName) throws IOException {
        final String fileName;
        int lastIndexOf = templateName.lastIndexOf('/');
        if (lastIndexOf == -1) {
            fileName = templateName.replace(outputType.getTemplateExtension(), "") + outputType.getFileExtension();
        } else {
            fileName = templateName.substring(lastIndexOf + 1).replace(outputType.getTemplateExtension(), "") + outputType.getFileExtension();
        }
        String datedFileName = Defaults.DATE_FORMAT.format(ZonedDateTime.now()) + "_" + fileName;

        LOG.info("Writing to file '" + datedFileName + "'");
        return new FileWriter(datedFileName);
    }
}
