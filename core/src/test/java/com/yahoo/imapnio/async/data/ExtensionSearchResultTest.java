package com.yahoo.imapnio.async.data;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@code ExtensionSearchResult}.
 */
public class ExtensionSearchResultTest {
    /**
     * Tests ExtensionSearchResult constructor and getters.
     */
    @Test
    public void testExtensionSearchResult() {
        final String tag = "A1";
        final Long min = 1L;
        final Long max = 2L;
        final MessageNumberSet[] all = new MessageNumberSet[] { new MessageNumberSet(1L, 2L) };
        final Long count = 2L;
        final Long modSeq = 10L;
        final ExtensionSearchResult extensionSearchResult = new ExtensionSearchResult(tag, min, max, all, count, modSeq);
        Assert.assertEquals(extensionSearchResult.getTag(), tag, "getTag() mismatched.");
        Assert.assertEquals(extensionSearchResult.getMin(), min, "getMin() mismatched.");
        Assert.assertEquals(extensionSearchResult.getMax(), max, "getMax() mismatched.");
        Assert.assertEquals(extensionSearchResult.getAll()[0], all[0], "getAll() mismatched.");
        Assert.assertEquals(extensionSearchResult.getCount(), count, "getCount() mismatched.");
    }

    /**
     * Tests ExtensionSearchResult constructor and getters for null.
     */
    @Test
    public void testExtensionSearchResultNull() {
        final ExtensionSearchResult extensionSearchResult = new ExtensionSearchResult(null, null, null, null, null, null);
        Assert.assertNull(extensionSearchResult.getTag(), "getTag() should be null.");
        Assert.assertNull(extensionSearchResult.getMin(), "getMin() should be null.");
        Assert.assertNull(extensionSearchResult.getMax(), "getMax() should be null.");
        Assert.assertNull(extensionSearchResult.getAll(), "getAll() should be null.");
        Assert.assertNull(extensionSearchResult.getCount(), "getCount() should be null.");
        Assert.assertNull(extensionSearchResult.getModSeq(), "getModSeq() should be null.");
    }
}
