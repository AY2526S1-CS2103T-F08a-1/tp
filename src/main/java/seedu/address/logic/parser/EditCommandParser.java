package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditCompanyDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.exceptions.ParseIndicesException;
import seedu.address.model.company.Address;
import seedu.address.model.company.Email;
import seedu.address.model.company.Phone;
import seedu.address.model.company.Remark;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * Supports both single index editing (e.g., "1") and batch editing (e.g., "1,2,3").
     *
     * @param args the user input arguments
     * @return EditCommand configured for single or batch editing
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE,
                PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG, PREFIX_REMARK, PREFIX_STATUS);

        List<Index> indices;

        if (args.isEmpty() || argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        if (argMultimap.getPreamble().contains("/")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        try {
            // Parse indices - supports both single and comma-separated multiple indices
            indices = ParserUtil.parseIndices(argMultimap.getPreamble());
        } catch (ParseIndicesException pie) {
            // Always preserve specific indices parsing errors
            throw pie;
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_REMARK, PREFIX_STATUS);

        EditCompanyDescriptor editCompanyDescriptor = new EditCompanyDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editCompanyDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            String val = argMultimap.getValue(PREFIX_PHONE).get();
            Phone newVal = val.isBlank() ? new Phone(null) : ParserUtil.parsePhone(val);
            editCompanyDescriptor.setPhone(newVal);
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            String val = argMultimap.getValue(PREFIX_EMAIL).get();
            Email newVal = val.isBlank() ? new Email(null) : ParserUtil.parseEmail(val);
            editCompanyDescriptor.setEmail(newVal);
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            String val = argMultimap.getValue(PREFIX_ADDRESS).get();
            Address newVal = val.isBlank() ? new Address(null) : ParserUtil.parseAddress(val);
            editCompanyDescriptor.setAddress(newVal);
        }
        if (argMultimap.getValue(PREFIX_REMARK).isPresent()) {
            String val = argMultimap.getValue(PREFIX_REMARK).get();
            Remark newVal = val.isBlank() ? new Remark(null) : ParserUtil.parseRemark(val);
            editCompanyDescriptor.setRemark(newVal);
        }
        if (argMultimap.getValue(PREFIX_STATUS).isPresent()) {
            editCompanyDescriptor.setStatus(ParserUtil.parseStatus(argMultimap.getValue(PREFIX_STATUS).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editCompanyDescriptor::setTags);

        if (!editCompanyDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        // Use method overloading: single index or multiple indices
        if (indices.size() == 1) {
            return new EditCommand(indices.get(0), editCompanyDescriptor);
        } else {
            return new EditCommand(indices, editCompanyDescriptor);
        }
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
