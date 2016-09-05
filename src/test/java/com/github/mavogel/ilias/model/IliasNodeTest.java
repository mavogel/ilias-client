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
