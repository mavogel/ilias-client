/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 *  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */
package com.github.mavogel.ilias.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by mavogel on 9/5/16.
 */
public class IliasNodeTest {

    @Test
    public void shouldCreateEmptyArray() {
        // == go
        String[] composeObjectTypes = IliasNode.Type.compose();

        // == verify
        assertTrue(Arrays.equals(new String[]{}, composeObjectTypes));
    }

    @Test
    public void shouldCreateGroupAndFolderTypes() {
        // == go
        String[] composeObjectTypes = IliasNode.Type.compose(IliasNode.Type.FOLDER, IliasNode.Type.GROUP);

        // == verify
        assertTrue(Arrays.equals(new String[]{IliasNode.Type.FOLDER.getXmlShortName(),
                IliasNode.Type.GROUP.getXmlShortName()}, composeObjectTypes));
    }
}
