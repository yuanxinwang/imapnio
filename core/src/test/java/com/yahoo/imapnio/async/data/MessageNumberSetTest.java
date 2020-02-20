package com.yahoo.imapnio.async.data;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yahoo.imapnio.async.data.MessageNumberSet.LastMessage;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException.FailureType;

/**
 * Unit test for {@code MessageNumberSet}.
 */
public class MessageNumberSetTest {

    /**
     * Tests constructor and converting it to string.
     */
    @Test
    public void testConstructorWithStartEnd() {
        final MessageNumberSet msgSet = new MessageNumberSet(1, 100);
        Assert.assertNotNull(msgSet, "Should not be null");
        Assert.assertEquals(MessageNumberSet.buildString(new MessageNumberSet[] { msgSet }), "1:100", "Result mismatched.");
    }

    /**
     * Tests constructor where starts with specific message and ends with last message and converting it to string.
     */
    @Test
    public void testConstructorWithStartEndWithLast() {
        final MessageNumberSet msgSet = new MessageNumberSet(1, LastMessage.LAST_MESSAGE);
        Assert.assertNotNull(msgSet, "Should not be null");
        Assert.assertEquals(MessageNumberSet.buildString(new MessageNumberSet[] { msgSet }), "1:*", "Result mismatched.");
    }

    /**
     * Tests constructor where it has only one message and converting it to string.
     */
    @Test
    public void testConstructorWithStartAndEndSame() {
        final MessageNumberSet msgSet = new MessageNumberSet(1, 1);
        Assert.assertNotNull(msgSet, "Should not be null");
        Assert.assertEquals(MessageNumberSet.buildString(new MessageNumberSet[] { msgSet }), "1", "Result mismatched.");
    }

    /**
     * Tests constructor and converting it to string.
     *
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testConstructorLastMessageOnlyTrue() throws ImapAsyncClientException {
        final MessageNumberSet msgSet = new MessageNumberSet(LastMessage.LAST_MESSAGE);
        Assert.assertNotNull(msgSet, "Should not be null");
        Assert.assertEquals(MessageNumberSet.buildString(new MessageNumberSet[] { msgSet }), "*", "Result mismatched.");
    }

    /**
     * Tests createMessageNumberSets(int[]) method.
     *
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testCreateMessageNumberSetsFromIntArray() throws ImapAsyncClientException {
        final int[] msgs = { 1, 2, 3, 4, 5, 7 };

        final MessageNumberSet[] sets = MessageNumberSet.createMessageNumberSets(msgs);
        Assert.assertNotNull(sets, "Should not be null");
        Assert.assertEquals(sets.length, 2, "lenth mismatched.");
        Assert.assertEquals(MessageNumberSet.buildString(sets), "1:5,7", "Expect result mismatched.");
    }

    /**
     * Tests createMessageNumberSets(int[]) method.
     *
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testCreateMessageNumberSetsFromLongArray() throws ImapAsyncClientException {
        final long[] msgs = { 1, 2, 3, 4, 5, 7, 1 };

        final MessageNumberSet[] sets = MessageNumberSet.createMessageNumberSets(msgs);
        Assert.assertNotNull(sets, "Should not be null");
        Assert.assertEquals(sets.length, 3, "lenth mismatched.");
        Assert.assertEquals(MessageNumberSet.buildString(sets), "1:5,7,1", "Expect result mismatched.");
    }

    /**
     * Tests createMessageNumberSets(int[]) method.
     *
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testRemovePointDuplicates() throws ImapAsyncClientException {
        final long[] msgs = { 1, 1, 1, 1, 1, 1, 1 };

        final MessageNumberSet[] sets = MessageNumberSet.createMessageNumberSets(msgs);
        Assert.assertNotNull(sets, "Should not be null");
        Assert.assertEquals(MessageNumberSet.buildString(sets), "1", "Expect result mismatched.");
    }

    /**
     * Tests createMessageNumberSets(int[]) method.
     *
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testRemoveRangesAndPointsDuplicates() throws ImapAsyncClientException {

        final MessageNumberSet[] sets = { new MessageNumberSet(1, 5), new MessageNumberSet(5, 1), new MessageNumberSet(1, LastMessage.LAST_MESSAGE),
                new MessageNumberSet(1, 5), new MessageNumberSet(2, 2), new MessageNumberSet(LastMessage.LAST_MESSAGE), new MessageNumberSet(2, 2) };
        Assert.assertNotNull(sets, "Should not be null");
        Assert.assertEquals(MessageNumberSet.buildString(sets), "1:5,1:*,2,*", "Expect result mismatched.");
    }

    /**
     * Tests buildString method.
     *
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testBuildString() throws ImapAsyncClientException {
        final MessageNumberSet[] sets = new MessageNumberSet[3];

        sets[0] = new MessageNumberSet(LastMessage.LAST_MESSAGE);
        sets[1] = new MessageNumberSet(1, 5);
        sets[2] = new MessageNumberSet(3, 3);
        Assert.assertNotNull(sets, "Should not be null");
        Assert.assertEquals(MessageNumberSet.buildString(sets), "*,1:5,3", "Result mismatched.");
    }

    /**
     * Tests constructor and converting it to string.
     */
    @Test
    public void testBuildStringWithNullMessageNumberSets() {
        Assert.assertNull(MessageNumberSet.buildString(null), "Result mismatched.");
    }

    /**
     * Tests constructor and converting it to string.
     */
    @Test
    public void testBuildStringWith0LengthMessageNumberSets() {
        Assert.assertNull(MessageNumberSet.buildString(new MessageNumberSet[0]), "Result mismatched.");
    }

    /**
     * Tests buildMessageNumberSets(String) method with only one number.
     */
    @Test
    public void testBuildMessageNumberSetsWithOneString() throws ImapAsyncClientException {
        final MessageNumberSet[] expectedMsgSets = { new MessageNumberSet(1, 1) };
        final String msgSetStr = MessageNumberSet.buildString(expectedMsgSets);
        final MessageNumberSet[] actualMsgSets = MessageNumberSet.buildMessageNumberSets(msgSetStr);
        Assert.assertNotNull(actualMsgSets, "buildMessageNumberSets() should not return null.");
        Assert.assertEquals(actualMsgSets.length, 1, "buildMessageNumberSets() size mismatched.");
        Assert.assertEquals(actualMsgSets[0], expectedMsgSets[0], "buildMessageNumberSets() mismatched.");
    }

    /**
     * Tests buildMessageNumberSets(String) method having both start and end as number.
     */
    @Test
    public void testBuildMessageNumberSetsWithStartEnd() throws ImapAsyncClientException {
        final MessageNumberSet[] expectedMsgSets = { new MessageNumberSet(1, 100) };
        final String msgSetStr = MessageNumberSet.buildString(expectedMsgSets);
        final MessageNumberSet[] actualMsgSets = MessageNumberSet.buildMessageNumberSets(msgSetStr);
        Assert.assertNotNull(actualMsgSets, "buildMessageNumberSets() should not return null.");
        Assert.assertEquals(actualMsgSets.length, 1, "buildMessageNumberSets() size mismatched.");
        Assert.assertEquals(actualMsgSets[0], expectedMsgSets[0], "buildMessageNumberSets() mismatched.");
    }

    /**
     * Tests buildMessageNumberSets(String) method having start with a number and end with last.
     */
    @Test
    public void testBuildMessageNumberSetsWithStartEndWithLast() throws ImapAsyncClientException {
        final MessageNumberSet[] expectedMsgSets = { new MessageNumberSet(1, LastMessage.LAST_MESSAGE) };
        final String msgSetStr = MessageNumberSet.buildString(expectedMsgSets);
        final MessageNumberSet[] actualMsgSets = MessageNumberSet.buildMessageNumberSets(msgSetStr);
        Assert.assertNotNull(actualMsgSets, "buildMessageNumberSets() should not return null.");
        Assert.assertEquals(actualMsgSets.length, 1, "buildMessageNumberSets() size mismatched.");
        Assert.assertEquals(actualMsgSets[0], expectedMsgSets[0], "buildMessageNumberSets() mismatched.");
    }

    /**
     * Tests buildMessageNumberSets(String) method with last message only.
     */
    @Test
    public void testBuildMessageNumberSetsWithLastMessageOnly() throws ImapAsyncClientException {
        final MessageNumberSet[] expectedMsgSets = { new MessageNumberSet(LastMessage.LAST_MESSAGE) };
        final String msgSetStr = MessageNumberSet.buildString(expectedMsgSets);
        final MessageNumberSet[] actualMsgSets = MessageNumberSet.buildMessageNumberSets(msgSetStr);
        Assert.assertNotNull(actualMsgSets, "buildMessageNumberSets() should not return null.");
        Assert.assertEquals(actualMsgSets.length, 1, "buildMessageNumberSets() size mismatched.");
        Assert.assertEquals(actualMsgSets[0], expectedMsgSets[0], "buildMessageNumberSets() mismatched.");
    }

    /**
     * Tests buildMessageNumberSets(String) method with last message only.
     */
    @Test
    public void testBuildMessageNumberSetsWithEndStartWithLast() throws ImapAsyncClientException {
        final MessageNumberSet[] expectedMsgSets = { new MessageNumberSet(1, LastMessage.LAST_MESSAGE) };
        final String msgSetStr = "*:1";
        final MessageNumberSet[] actualMsgSets = MessageNumberSet.buildMessageNumberSets(msgSetStr);
        Assert.assertNotNull(actualMsgSets, "buildMessageNumberSets() should not return null.");
        Assert.assertEquals(actualMsgSets.length, 1, "buildMessageNumberSets() size mismatched.");
        Assert.assertEquals(actualMsgSets[0], expectedMsgSets[0], "buildMessageNumberSets() mismatched.");
    }

    /**
     * Tests buildMessageNumberSets(String) method with multiple of message number sets.
     */
    @Test
    public void testBuildMessageNumberSetsWithMultipleMsgNumSets() throws ImapAsyncClientException {
        final int numMsgs = 4;
        final MessageNumberSet[] expectedMsgs = new MessageNumberSet[numMsgs];
        expectedMsgs[0] = new MessageNumberSet(1, 5);
        expectedMsgs[1] = new MessageNumberSet(1, LastMessage.LAST_MESSAGE);
        expectedMsgs[2] = new MessageNumberSet(2, 2);
        expectedMsgs[3] = new MessageNumberSet(LastMessage.LAST_MESSAGE);
        final String msgSetStr = MessageNumberSet.buildString(expectedMsgs);
        final MessageNumberSet[] messageNumberSets = MessageNumberSet.buildMessageNumberSets(msgSetStr);
        Assert.assertNotNull(messageNumberSets, "buildMessageNumberSets() should not return null.");
        Assert.assertEquals(messageNumberSets.length, numMsgs, "buildMessageNumberSets() size mismatched.");
        for (int i = 0; i < numMsgs; i++) {
            Assert.assertEquals(messageNumberSets[i], expectedMsgs[i], "buildMessageNumberSets() mismatched.");
        }
    }

    /**
     * Tests constructor and converting it to string.
     *
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testConstructorLastMessageOnlyFalse() throws ImapAsyncClientException {
        ImapAsyncClientException actual = null;
        try {
            new MessageNumberSet(null);
        } catch (final ImapAsyncClientException e) {
            actual = e;
        }
        Assert.assertNotNull(actual, "Exception should be thrown");
        Assert.assertEquals(actual.getFaiureType(), FailureType.INVALID_INPUT, "Result mismatched.");
    }

    /**
     * Tests equals with same type, start, and ending point.
     */
    @Test
    public void testEqualsTrue() {
        final MessageNumberSet msgSet1 = new MessageNumberSet(1, 100);
        final MessageNumberSet msgSet2 = new MessageNumberSet(1, 100);
        Assert.assertNotNull(msgSet1, "Should not be null");
        Assert.assertTrue(msgSet1.equals(msgSet2), "Result mismatched.");
    }

    /**
     * Tests equals with different class.
     */
    @Test
    public void testEqualsDiffClass() {
        final MessageNumberSet msgSet = new MessageNumberSet(1, 100);
        Assert.assertNotNull(msgSet, "Should not be null");
        final String u = new String("hello");
        Assert.assertFalse(msgSet.equals(u), "Result mismatched.");
    }

    /**
     * Tests equals with different sequence type.
     */
    @Test
    public void testEqualsDiffSeqType() {
        final MessageNumberSet msgSet1 = new MessageNumberSet(1, 100);
        final MessageNumberSet msgSet2 = new MessageNumberSet(1, LastMessage.LAST_MESSAGE);
        Assert.assertFalse(msgSet1.equals(msgSet2), "Result mismatched.");
    }

    /**
     * Tests equals with different ending point.
     */
    @Test
    public void testEqualsDiffEnd() {
        final MessageNumberSet msgSet1 = new MessageNumberSet(1, 100);
        final MessageNumberSet msgSet2 = new MessageNumberSet(1, 101);
        Assert.assertFalse(msgSet1.equals(msgSet2), "Result mismatched.");
    }

    /**
     * Tests equals with different starting point.
     */
    @Test
    public void testEqualsDiffStart() {
        final MessageNumberSet msgSet1 = new MessageNumberSet(1, 100);
        final MessageNumberSet msgSet2 = new MessageNumberSet(2, 100);
        Assert.assertFalse(msgSet1.equals(msgSet2), "Result mismatched.");
    }

    /**
     * Tests SequenceType enum.
     */
    @Test
    public void testCommandTypeEnum() {
        final LastMessage[] enumList = LastMessage.values();
        Assert.assertEquals(enumList.length, 1, "The enum count mismatched.");
        final LastMessage value = LastMessage.valueOf("LAST_MESSAGE");
        Assert.assertSame(value, LastMessage.LAST_MESSAGE, "Enum does not match.");
    }
}
