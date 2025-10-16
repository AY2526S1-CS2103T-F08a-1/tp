package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_COMPANY;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;

public class DeleteCommandParserTest {

    private final DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validSingleIndex_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(List.of(INDEX_FIRST_COMPANY)));
    }

    @Test
    public void parse_validMultipleIndices_returnsDeleteCommand() {
        assertParseSuccess(parser, "1,2", new DeleteCommand(List.of(
                INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY)));
    }

    @Test
    public void parse_validRange_returnsDeleteCommand() {
        assertParseSuccess(parser, "1, 5,               6", new DeleteCommand(List.of(
                INDEX_FIRST_COMPANY,
                Index.fromOneBased(5),
                Index.fromOneBased(6))));
    }

    @Test
    public void parse_mixedIndicesAndRange_returnsDeleteCommand() {
        // 1 2-2 should dedupe to [1,2]
        assertParseSuccess(parser, "1,1,1", new DeleteCommand(List.of(
                INDEX_FIRST_COMPANY)));
    }

    @Test
    public void parse_randomOrderingIndices_returnsDeleteCommand() {
        // 1 2-2 should dedupe to [1,2]
        assertParseSuccess(parser, "5,4,3", new DeleteCommand(List.of(
                Index.fromOneBased(3),
                Index.fromOneBased(4),
                Index.fromOneBased(5))));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "2-1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1 3 5",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1,3 533",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1,3, , 533",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }
}
