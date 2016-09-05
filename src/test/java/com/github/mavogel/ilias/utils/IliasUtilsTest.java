package com.github.mavogel.ilias.utils;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mavogel on 9/5/16.
 */
public class IliasUtilsTest {

    @Test
    public void shouldCreateEmptyArray() {
        // == go
        String[] composeObjectTypes = IliasUtils.ObjectTypes.compose();

        // == verify
        assertTrue(Arrays.equals(new String[]{}, composeObjectTypes));
    }

    @Test
    public void shouldCreateGroupAndFolderTypes() {
        // == go
        String[] composeObjectTypes = IliasUtils.ObjectTypes.compose(IliasUtils.ObjectTypes.FOLDER, IliasUtils.ObjectTypes.GROUP);

        // == verify
        assertTrue(Arrays.equals(new String[]{IliasUtils.ObjectTypes.FOLDER.getXmlShortName(),
                                              IliasUtils.ObjectTypes.GROUP.getXmlShortName()}, composeObjectTypes));
    }
}
