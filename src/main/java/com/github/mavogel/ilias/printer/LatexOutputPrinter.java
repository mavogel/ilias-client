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

import com.github.mavogel.ilias.model.GroupUserModelFull;

import java.util.List;

/**
 * Printer for a Latex output file.
 *
 * Created by mavogel on 9/20/16.
 */
public class LatexOutputPrinter extends OutputPrinter {

    private final List<GroupUserModelFull> groupUserModels;

    public LatexOutputPrinter(final String title, final List<GroupUserModelFull> groupUserModels) {
        super(title);
        this.groupUserModels = groupUserModels;
    }

    @Override
    protected String prefix() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("\\documentclass[11pt]{article}");
        sb.append("\\title{").append(this.title).append("}");
        sb.append("\\begin{document}");
        sb.append("\\maketitle");
        return sb.toString();
    }

    @Override
    protected String content() {
        return null; //TODO
    }

    @Override
    protected String suffix() {
        return "\\end{document}  ";
    }
}
