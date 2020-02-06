package com.yahoo.imapnio.async.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPResponse;
import com.sun.mail.imap.protocol.MODSEQ;

/**
 * Unit test for {@code StoreResult}.
 */
public class StoreResultTest {
    /**
     * Tests StoreResult constructor and getters.
     */
    @Test
    public void testStoreResult() throws IOException, ProtocolException {
        final IMAPResponse[] content = new IMAPResponse[3];
        content[0] = new IMAPResponse("* 1 FETCH (UID 4 MODSEQ (12121231000))");
        content[1] = new IMAPResponse("* VANISHED (EARLIER) 41,43:116,118,120:211,214:540");
        content[2] = new IMAPResponse("* 3 EXPUNGE");
        final List<IMAPResponse> imapResponses = Arrays.asList(content);
        final List<Long> modifiedMsgNums = Collections.singletonList(1L);
        final StoreResult storeResult = new StoreResult(1L, imapResponses, modifiedMsgNums);
        final long highestModSeq = storeResult.getHighestModSeq();
        final List<IMAPResponse> responses = storeResult.getIMAPResponses();
        final long fetchedModSeq = new FetchResponse(responses.get(0)).getItem(MODSEQ.class).modseq;
        final List<Long> modifiedMsgsets = storeResult.getModifiedMsgsets();

        Assert.assertEquals(responses.size(), 3, "Result mismatched.");
        Assert.assertEquals(highestModSeq, 1L, "Result mismatched.");
        Assert.assertTrue(responses.get(0).keyEquals("FETCH"), "Result mismatched.");
        Assert.assertEquals(fetchedModSeq, 12121231000L, "Result mismatched.");
        Assert.assertTrue(responses.get(1).keyEquals("VANISHED"), "Result mismatched.");
        Assert.assertTrue(responses.get(2).keyEquals("EXPUNGE"), "Result mismatched.");
        Assert.assertEquals(modifiedMsgsets.size(), 1, "Result mismatched.");
        Assert.assertEquals(modifiedMsgsets.get(0), Long.valueOf(1), "Result mismatched.");
    }

    /**
     * Tests StoreResult constructor and getters when passing null highest mod sequence, empty fetch responses collection,
     * and empty modified message number collection.
     */
    @Test
    public void testStoreResultNullHighestModSeq() {
        final StoreResult infos = new StoreResult(new ArrayList<>(), new ArrayList<>());
        final List<Long> modifiedMsgsets = infos.getModifiedMsgsets();
        final List<IMAPResponse>fetchResponsesResult = infos.getIMAPResponses();
        final Long highestModSeq = infos.getHighestModSeq();

        Assert.assertNull(highestModSeq, "Result mismatched.");
        Assert.assertEquals(fetchResponsesResult.size(), 0, "Result mismatched.");
        Assert.assertEquals(modifiedMsgsets.size(), 0, "Result mismatched.");
    }

    /**
     * Tests StoreResult constructor and getters when passing null highest mod sequence, empty fetch responses collection,
     * and null modified message number collection.
     */
    @Test
    public void testStoreResultNullModifiedMessage() {
        final StoreResult infos = new StoreResult(new ArrayList<>());
        final List<Long> modifiedMsgsets = infos.getModifiedMsgsets();
        final List<IMAPResponse>fetchResponsesResult = infos.getIMAPResponses();
        final Long highestModSeq = infos.getHighestModSeq();

        Assert.assertNull(highestModSeq, "Result mismatched.");
        Assert.assertEquals(fetchResponsesResult.size(), 0, "Result mismatched.");
        Assert.assertNull(modifiedMsgsets, "Result mismatched.");
    }
}
