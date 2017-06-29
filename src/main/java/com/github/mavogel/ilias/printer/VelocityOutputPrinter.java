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
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

/**
 * Output writer based on velocity templates.
 * <p>
 * Created by mavogel on 9/20/16.
 */
public class VelocityOutputPrinter {

    private static Logger LOG = Logger.getLogger(VelocityOutputPrinter.class);

    /**
     * The possible output types.
     */
    public enum OutputType {
        HTML(0, ".html.vm", "templates" + System.getProperty("file.separator") + "group-members.html.vm"),
        LATEX(1, ".tex.vm", "templates" + System.getProperty("file.separator") + "group-members.tex.vm");
        private final int index;
        private final String templateExtension;
        private final String defaultTemplateLocation;

        OutputType(final int index, final String templateExtension, final String defaultTemplateLocation) {
            this.templateExtension = templateExtension;
            this.index = index;
            this.defaultTemplateLocation = defaultTemplateLocation;
        }

        public String getTemplateExtension() {
            return templateExtension;
        }

        public String getFileExtension() {
            return templateExtension.replace(".vm", "");
        }

        public String getDefaultTemplateLocation() {
            return defaultTemplateLocation;
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
     * @param templateName the name of the template, if empty the default template from classpath will be used
     * @param contextMap   the context for velocity
     * @throws Exception in case of a error, so the caller can handle it
     */
    public static void print(final OutputType outputType, final String templateName, final Map<String, Object> contextMap) throws Exception {

        if (templateName.trim().isEmpty()) {
            printWithDefaultTemplate(outputType, contextMap);
        } else {
            if (!templateName.contains(outputType.getTemplateExtension())) {
                LOG.error("Extension of output type '" + outputType.getTemplateExtension() + "' does not match with the templateExtension of the template.");
                throw new Exception();
            }
            printWithCustomTemplate(outputType, templateName, contextMap);
        }
    }

    /**
     * Prints the header, content and output to the given print stream with a custom template.
     *
     * @param outputType   the desired output type. @see {@link OutputType}
     * @param templateName the name of the template
     * @param contextMap   the context for velocity
     * @throws Exception in case of a error, so the caller can handle it
     */
    private static void printWithCustomTemplate(final OutputType outputType, final String templateName, final Map<String, Object> contextMap) throws Exception {
        Velocity.init();
        Writer writer = null;
        try {
            final Template template = Velocity.getTemplate(templateName);
            final VelocityContext context = new VelocityContext();
            contextMap.forEach((k, v) -> context.put(k, v));
            writer = new BufferedWriter(createFileWriter(outputType, templateName));

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
        } finally {
            if (writer != null) writer.close();
        }
    }

    /**
     * Prints the header, content and output to the given print stream with the default template from the classpath.
     *
     * @param outputType the desired output type. @see {@link OutputType}
     * @param contextMap the context for velocity
     * @throws Exception in case of a error, so the caller can handle it
     */
    private static void printWithDefaultTemplate(final OutputType outputType, final Map<String, Object> contextMap) throws Exception {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();

        Writer writer = null;
        final String templateName = outputType.getDefaultTemplateLocation();
        try {
            VelocityContext context = new VelocityContext();
            contextMap.forEach((k, v) -> context.put(k, v));
            Template template = ve.getTemplate(templateName, "UTF-8");
            writer = new BufferedWriter(createFileWriter(outputType, templateName));

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
            LOG.error("Cause: " + e.getCause());
            Arrays.stream(e.getStackTrace()).forEach(LOG::error);
            throw e;
        } finally {
            if (writer != null) writer.close();
        }
    }

    /**
     * Creates a file writer with a timestamp from the current output type and template. Write
     * a file in the current directory. Striped the folders off the name if the given template.
     *
     * @param outputType   the output type
     * @param templateName the name of the template
     * @return the OutputStreamWriter
     * @throws IOException if the file could not be created.
     */
    private static OutputStreamWriter createFileWriter(final OutputType outputType, final String templateName) throws IOException {
        final String fileName;
        int lastIndexOf = templateName.lastIndexOf(System.getProperty("file.separator"));
        if (lastIndexOf == -1) {
            fileName = templateName.replace(outputType.getTemplateExtension(), "") + outputType.getFileExtension();
        } else {
            fileName = templateName.substring(lastIndexOf + 1).replace(outputType.getTemplateExtension(), "") + outputType.getFileExtension();
        }
        String datedFileName = Defaults.OUTFILE_DATE_FORMAT.format(ZonedDateTime.now()) + "_" + fileName;

        LOG.info("Writing to file '" + datedFileName + "'");
        return new OutputStreamWriter(new FileOutputStream(datedFileName), Charset.forName("UTF-8"));
    }
}
