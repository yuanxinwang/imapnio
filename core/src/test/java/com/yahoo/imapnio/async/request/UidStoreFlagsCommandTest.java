package com.yahoo.imapnio.async.request;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Flags;
import javax.mail.search.SearchException;

import com.yahoo.imapnio.async.data.UnchangedSince;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yahoo.imapnio.async.data.MessageNumberSet;
import com.yahoo.imapnio.async.exception.ImapAsyncClientException;

/**
 * Unit test for {@code UidStoreFlagsCommand}.
 */
public class UidStoreFlagsCommandTest {

    /** Fields to check for cleanup. */
    private Set<Field> fieldsToCheck;

    /** Unchanged since the known modification sequence */
    private static final UnchangedSince MOD_SEQ = new UnchangedSince(1L);

    /**
     * Setup reflection.
     */
    @BeforeClass
    public void setUp() {
        // Use reflection to get all declared non-primitive non-static fields (We do not care about inherited fields)
        final Class<?> classUnderTest = UidStoreFlagsCommand.class;
        fieldsToCheck = new HashSet<>();
        for (Class<?> c = classUnderTest; c != null; c = c.getSuperclass()) {
            for (final Field declaredField : c.getDeclaredFields()) {
                if (!declaredField.getType().isPrimitive() && !Modifier.isStatic(declaredField.getModifiers())) {
                    declaredField.setAccessible(true);
                    fieldsToCheck.add(declaredField);
                }
            }
        }
    }

    /**
     * Tests getCommandLine method using message sequences, flags, adding flags and not silent.
     *
     * @throws IOException will not throw
     * @throws IllegalAccessException will not throw
     * @throws IllegalArgumentException will not throw
     * @throws ImapAsyncClientException will not throw
     * @throws SearchException will not throw
     */
    @Test
    public void testGetCommandLineWithFlagsAddedNotSilent()
            throws IOException, IllegalArgumentException, IllegalAccessException, SearchException, ImapAsyncClientException {

        final int[] msgs = { 1, 2, 3 };
        final MessageNumberSet[] msgsets = MessageNumberSet.createMessageNumberSets(msgs);
        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final ImapRequest cmd = new UidStoreFlagsCommand(msgsets, flags, FlagsAction.ADD);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:3 +FLAGS (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandLine method using message sequences, flags, replacing flags and not silent.
     *
     * @throws IOException will not throw
     * @throws IllegalAccessException will not throw
     * @throws IllegalArgumentException will not throw
     * @throws ImapAsyncClientException will not throw
     * @throws SearchException will not throw
     */
    @Test
    public void testGetCommandLineWithFlagsReplacedNotSilent()
            throws IOException, IllegalArgumentException, IllegalAccessException, SearchException, ImapAsyncClientException {

        final int[] msgs = { 1, 2, 3 };
        final MessageNumberSet[] msgsets = MessageNumberSet.createMessageNumberSets(msgs);
        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final ImapRequest cmd = new UidStoreFlagsCommand(msgsets, flags, FlagsAction.REPLACE);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:3 FLAGS (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandLine method using message sequences, flags, adding flags and silent.
     *
     * @throws IOException will not throw
     * @throws IllegalAccessException will not throw
     * @throws IllegalArgumentException will not throw
     * @throws ImapAsyncClientException will not throw
     * @throws SearchException will not throw
     */
    @Test
    public void testGetCommandLineWithFlagsAddedSilent()
            throws IOException, IllegalArgumentException, IllegalAccessException, SearchException, ImapAsyncClientException {

        final int[] msgs = { 1, 2, 3 };
        final MessageNumberSet[] msgsets = MessageNumberSet.createMessageNumberSets(msgs);
        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final boolean isSilent = true;
        final ImapRequest cmd = new UidStoreFlagsCommand(msgsets, flags, FlagsAction.ADD, isSilent);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:3 +FLAGS.SILENT (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandLine method using message sequences, flags, replacing flags and silent.
     *
     * @throws IOException will not throw
     * @throws IllegalAccessException will not throw
     * @throws IllegalArgumentException will not throw
     * @throws ImapAsyncClientException will not throw
     * @throws SearchException will not throw
     */
    @Test
    public void testGetCommandLineWithFlagsReplacedSilent()
            throws IOException, IllegalArgumentException, IllegalAccessException, SearchException, ImapAsyncClientException {

        final int[] msgs = { 1, 2, 3 };
        final MessageNumberSet[] msgsets = MessageNumberSet.createMessageNumberSets(msgs);
        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final boolean isSilent = true;
        final ImapRequest cmd = new UidStoreFlagsCommand(msgsets, flags, FlagsAction.REPLACE, isSilent);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:3 FLAGS.SILENT (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandLine method with message sequences, flags, removing flags and not silent.
     *
     * @throws IOException will not throw
     * @throws IllegalAccessException will not throw
     * @throws ImapAsyncClientException will not throw
     * @throws SearchException will not throw
     */
    @Test
    public void testGetCommandLineWithFlagsRemovedNotSilent() throws IOException, IllegalAccessException, SearchException, ImapAsyncClientException {

        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final ImapRequest cmd = new UidStoreFlagsCommand(new MessageNumberSet[] { new MessageNumberSet(1, 10000) }, flags, FlagsAction.REMOVE);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:10000 -FLAGS (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandLine method with message sequences, removing flags and silent.
     *
     * @throws IOException will not throw
     * @throws IllegalAccessException will not throw
     * @throws ImapAsyncClientException will not throw
     * @throws SearchException will not throw
     */
    @Test
    public void testGetCommandLineWithFlagsRemovedAndSilent() throws IOException, IllegalAccessException, SearchException, ImapAsyncClientException {

        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final boolean isSilent = true;
        final ImapRequest cmd = new UidStoreFlagsCommand(new MessageNumberSet[] { new MessageNumberSet(1, 10000) }, flags, FlagsAction.REMOVE,
                isSilent);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:10000 -FLAGS.SILENT (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandLine method with message sequences, adding flags and silent.
     *
     * @throws IOException will not throw
     * @throws IllegalAccessException will not throw
     * @throws ImapAsyncClientException will not throw
     * @throws SearchException will not throw
     */
    @Test
    public void testGetCommandLineWithMessageSeqStringFlagsAddedAndSilent()
            throws IOException, IllegalAccessException, SearchException, ImapAsyncClientException {

        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final boolean isSilent = true;
        final ImapRequest cmd = new UidStoreFlagsCommand("1:*", flags, FlagsAction.ADD, isSilent);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:* +FLAGS.SILENT (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandLine method using message sequences, flags, adding flags and not silent.
     *
     * @throws IllegalAccessException will not throw
     * @throws IllegalArgumentException will not throw
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testGetCommandLineWithFlagsAddedNotSilentUnchangedSince()
            throws IllegalArgumentException, IllegalAccessException, ImapAsyncClientException {

        final int[] msgs = { 1, 2, 3 };
        final MessageNumberSet[] msgsets = MessageNumberSet.createMessageNumberSets(msgs);
        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final ImapRequest cmd = new UidStoreFlagsCommand(msgsets, flags, FlagsAction.ADD, MOD_SEQ);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:3 (UNCHANGEDSINCE 1) +FLAGS (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandLine method using message sequences, flags, adding flags and silent.
     *
     * @throws IllegalAccessException will not throw
     * @throws IllegalArgumentException will not throw
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testGetCommandLineWithFlagsAddedSilentUnchangedSince()
            throws IllegalArgumentException, IllegalAccessException, ImapAsyncClientException {

        final int[] msgs = { 1, 2, 3 };
        final MessageNumberSet[] msgsets = MessageNumberSet.createMessageNumberSets(msgs);
        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final boolean isSilent = true;
        final ImapRequest cmd = new UidStoreFlagsCommand(msgsets, flags, FlagsAction.ADD, isSilent, MOD_SEQ);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:3 (UNCHANGEDSINCE 1) +FLAGS.SILENT (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandLine method with message sequences, adding flags and silent.
     *
     * @throws IllegalAccessException will not throw
     * @throws ImapAsyncClientException will not throw
     */
    @Test
    public void testGetCommandLineWithMessageSeqStringFlagsAddedAndSilentUnchangeSince()
            throws IllegalAccessException, ImapAsyncClientException {

        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final boolean isSilent = true;
        final ImapRequest cmd = new UidStoreFlagsCommand("1:*", flags, FlagsAction.ADD, isSilent, MOD_SEQ);
        Assert.assertEquals(cmd.getCommandLine(), "UID STORE 1:* (UNCHANGEDSINCE 1) +FLAGS.SILENT (\\Deleted \\Seen)\r\n", "Expected result mismatched.");

        cmd.cleanup();
        // Verify if cleanup happened correctly.
        for (final Field field : fieldsToCheck) {
            Assert.assertNull(field.get(cmd), "Cleanup should set " + field.getName() + " as null");
        }
    }

    /**
     * Tests getCommandType method.
     */
    @Test
    public void testGetCommandType() {
        final Flags flags = new Flags();
        flags.add(Flags.Flag.SEEN);
        flags.add(Flags.Flag.DELETED);
        final ImapRequest cmd = new UidStoreFlagsCommand(new MessageNumberSet[] { new MessageNumberSet(1, 10000) }, flags, FlagsAction.ADD);
        Assert.assertSame(cmd.getCommandType(), ImapCommandType.UID_STORE_FLAGS);
    }
}