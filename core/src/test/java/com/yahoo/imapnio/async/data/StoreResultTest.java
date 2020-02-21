package com.yahoo.imapnio.async.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.IMAPResponse;

/**
 * Unit test for {@code StoreResult}.
 */
public class StoreResultTest {
    /**
     * Tests StoreResult constructor and getters.
     */
    @Test
    public void testStoreResult() throws IOException, ProtocolException {
        final List<IMAPResponse> imapResponses = new ArrayList<>(2);
        imapResponses.add(new IMAPResponse("* 1 FETCH (UID 4 MODSEQ (12121231000))"));
        imapResponses.add(new IMAPResponse("* VANISHED (EARLIER) 41,43:116,118,120:211,214:540"));
        final MessageNumberSet[] modifiedMsgSet = { new MessageNumberSet(1L, 1L) };
        final StoreResult storeResult = new StoreResult(1L, imapResponses, modifiedMsgSet);
        final Long highestModSeq = storeResult.getHighestModSeq();
        final List<IMAPResponse> responses = storeResult.getIMAPResponses();

        Assert.assertEquals(responses.size(), 2, "getIMAPResponses() mismatched.");
        Assert.assertNotNull(highestModSeq, "getHighestModSeq() should not return null");
        Assert.assertEquals(highestModSeq, Long.valueOf(1L), "getHighestModSeq() mismatched.");
        Assert.assertTrue(responses.get(0).keyEquals("FETCH"), "getIMAPResponses() mismatched.");
        Assert.assertTrue(responses.get(1).keyEquals("VANISHED"), "getIMAPResponses() mismatched.");
        final MessageNumberSet[] modifiedMsgsets = storeResult.getModifiedMsgSets();
        Assert.assertNotNull(modifiedMsgsets, "getModifiedMsgSets() should not return null.");
        Assert.assertEquals(modifiedMsgsets.length, 1, "getModifiedMsgSets() size mismatched.");
        Assert.assertEquals(modifiedMsgsets[0], modifiedMsgSet[0], "getModifiedMsgSets() mismatched.");
    }
}
