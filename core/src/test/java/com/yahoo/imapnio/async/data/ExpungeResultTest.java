package com.yahoo.imapnio.async.data;

import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@link ExpungeResult}.
 */
public class ExpungeResultTest {
    /**
     * Tests ExpungeResult constructor and getters for vanished response.
     */
    @Test
    public void testExpungeResultVanishedResponse() {

        final MessageNumberSet knownUid = new MessageNumberSet(1L, 1L);
        final VanishedResponse vanishedResponse = new VanishedResponse(false, new MessageNumberSet[]{ knownUid });
        final ExpungeResult expungeResult = new ExpungeResult(vanishedResponse, 1L);
        final Long highestModSeq = expungeResult.getHighestModSeq();
        final VanishedResponse vr = expungeResult.getVanishedResponse();
        Assert.assertNotNull(vr, "getVanishedResponse() mismatched.");
        Assert.assertEquals(vr.getKnownUids()[0], knownUid, "getVanishedResponse() mismatched.");
        Assert.assertNotNull(highestModSeq, "getHighestModSeq() mismatched.");
        Assert.assertEquals(highestModSeq, Long.valueOf(1L), "getHighestModSeq() mismatched.");
        Assert.assertNull(expungeResult.getExpungeMessageNumberList(), "getExpungeMessageNumberList() mismatched.");
    }

    /**
     * Tests ExpungeResult constructor and getters for vanished response.
     */
    @Test
    public void testExpungeResultExpungeMessageNumberList() {

        final List<Integer> expungeMessageNumbers = Collections.singletonList(1);
        final ExpungeResult expungeResult = new ExpungeResult(expungeMessageNumbers, 1L);
        final Long highestModSeq = expungeResult.getHighestModSeq();
        final List<Integer> el = expungeResult.getExpungeMessageNumberList();
        Assert.assertNotNull(el, "getVanishedResponse() mismatched.");
        Assert.assertEquals(el.get(0), expungeMessageNumbers.get(0));
        Assert.assertNotNull(highestModSeq, "getHighestModSeq() mismatched.");
        Assert.assertEquals(highestModSeq, Long.valueOf(1L), "getHighestModSeq() mismatched.");
        Assert.assertNull(expungeResult.getVanishedResponse(), "getVanishedResponse() mismatched.");
    }

    /**
     * Tests ExpungeResult constructor and getters when passing null highest mod sequence, null vanished response.
     */
    @Test
    public void testExpungeResultNullVanishedResponse() {
        final ExpungeResult er = new ExpungeResult((VanishedResponse) null, null);
        Assert.assertNull(er.getHighestModSeq(), "getHighestModSeq() mismatched.");
        Assert.assertNull(er.getVanishedResponse(), "getVanishedResponse() mismatched.");
        Assert.assertNull((er.getExpungeMessageNumberList()), "getExpungeMessageNumberList() mismatched.");
    }

    /**
     * Tests ExpungeResult constructor and getters when passing null highest mod sequence and null message number collection.
     */
    @Test
    public void testExpungeResultNullExpungeMessageNumberList() {
        final ExpungeResult er = new ExpungeResult((List<Integer>) null, null);
        Assert.assertNull(er.getHighestModSeq(), "getHighestModSeq() mismatched.");
        Assert.assertNull(er.getVanishedResponse(), "getVanishedResponse() mismatched.");
        Assert.assertNull(er.getExpungeMessageNumberList(), "getExpungeMessageNumberList() mismatched.");
    }
}
