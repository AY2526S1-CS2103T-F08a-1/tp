---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the AddressBook-Level3 (AB3) project created by the [SE-EDU initiative](https://se-education.org).
* This project uses the [Apache Commons Validator](https://commons.apache.org/proper/commons-validator/) library for email validation, licensed under the Apache License 2.0.
* Claude AI was used to format the Release Notes into Markdown.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------
<div style="page-break-after: always;"></div>
## **Design**
### Architecture

<img src="images/ArchitectureDiagram.png" width="560" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103T-F08a-1/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103T-F08a-1/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is the application entry point that is in charge of the app launch and shut down.
* At app launch, it initializes the other components (UI, Logic, Storage, and Model) in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

**`User`** interacts with the application through the CLI interface.
* The user inputs text commands via the command line interface.
* The UI displays output and feedback back to the user.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App that handles user interaction.
* [**`Logic`**](#logic-component): The command executor that invokes commands and processes business logic.
* [**`Model`**](#model-component): Holds the data of the App in memory and is updated by Logic and read by UI.
* [**`Storage`**](#storage-component): Persists data to and reads data from the hard disk in JSON format (Cerebro.json).

[**`Commons`**](#common-classes) represents a collection of shared utility classes used by multiple other components.

The relationships between components are as follows:

* **User ↔ UI**: The user inputs commands through the CLI, and the UI displays output and feedback.
* **UI → Logic**: The UI invokes command execution through the Logic component.
* **UI → Model**: The UI reads data from the Model to display information to the user.
* **Logic → Storage**: Logic persists data by requesting the Storage component to save/load data.
* **Logic → Model**: Logic updates the Model with processed data from command execution.
* **Storage ↔ Model**: Storage serializes Model objects to JSON format and deserializes JSON data back to Model objects.
* **Storage ↔ File**: Storage writes to and reads from Cerebro.json, the JSON file that stores all application data.
* **Main → All Components**: Main initializes all four main components (UI, Logic, Storage, Model) at application startup.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

<div style="page-break-after: always;"></div>

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103T-F08a-1/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `CompanyListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

Additionally, the UI includes separate window components:
* `HelpWindow` - Displays help information to the user.
* `MetricsWindow` - Displays statistics and metrics about the companies in the address book.
* `ClosableWindow` - Abstract base class for windows that can be closed with keyboard shortcuts.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103T-F08a-1/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103T-F08a-1/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Company` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103T-F08a-1/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a company).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

<div style="page-break-after: always;"></div>
The following commands are currently supported:
* `AddCommand` - Adds a company to the address book.
* `EditCommand` - Edits an existing company's details.
* `DeleteCommand` - Deletes a company from the address book.
* `FindCommand` - Finds companies by name keywords.
* `FilterCommand` - Filters companies by status and/or tags.
* `ListCommand` - Lists all companies.
* `ClearCommand` - Clears all companies from the address book.
* `MetricsCommand` - Displays statistics about the companies.
* `HelpCommand` - Shows help information.
* `ExitCommand` - Exits the application.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103T-F08a-1/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Company` objects (which are contained in a `UniqueCompanyList` object).
* stores the currently 'selected' `Company` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Company>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user's preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

Each `Company` object contains the following fields:
* `Name` (required) - The company name.
* `Phone` (required wrapper; value may be null) - Contact phone number (absent phone is represented as `new Phone(null)`).
* `Email` (required wrapper; value may be null) - Contact email address (absent email is represented as `new Email(null)`).
* `Address` (required wrapper; value may be null) - Company address (absent address is represented as `new Address(null)`).
* `Tags` (required, but can be empty) - Set of tags for categorization.
* `Remark` (required wrapper; value may be null or empty) - Additional notes about the company (absent remark is represented as `new Remark(null)`).
* `Status` (required) - Application status (e.g., Applied, Interview, Offered, Rejected).

Why always-present wrappers? Optional user input (e.g., phone) is modeled as a non-null wrapper object whose internal value may be null. This keeps model associations at multiplicity “1” and allows commands (especially `EditCommand`) to distinguish between “leave unchanged” and “clear value”. See the design note below for details.

#### Design considerations for nullable fields

**Aspect: How optional fields (Phone, Email, Address, Remark) are represented:**

The current implementation uses wrapper objects (e.g., `Phone`, `Email`, `Address`, `Remark`) that can hold null values internally, but the `Company` object never stores null references to these wrapper objects.

**Key design principles:**
* All fields passed to the `Company` constructor must be non-null (enforced by `requireAllNonNull`)
* Empty/absent values are represented by wrapper objects containing null internally (e.g., `new Phone(null)`)
* The `Company` object always has non-null references to all wrapper objects, even if the values inside are null

**Rationale:**

This design is crucial for commands like `EditCommand` to function correctly. In `EditCommand.EditCompanyDescriptor`, null is used to represent "do not edit this field", while a wrapper object with null inside (e.g., `new Phone(null)`) represents "clear this field to empty".

For example:
* `editCompanyDescriptor.getPhone()` returns `Optional.empty()` → means "don't change the phone number"
* `editCompanyDescriptor.getPhone()` returns `Optional.of(new Phone(null))` → means "clear the phone number"

If `Company` stored null directly in its fields, `EditCommand` would be unable to distinguish between "unedited field" and "cleared field", as both would be represented as null.

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative approach would be to store null directly in `Company` fields, which is simpler but makes it impossible for `EditCommand` to distinguish between fields that should remain unchanged versus fields that should be cleared.
</div>

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Company` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Company` needing their own `Tag` objects. Similarly, `Status` objects are stored centrally, but each `Company` can reference at most one `Status`. <br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>

<div style="page-break-after: always;"></div>
### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103T-F08a-1/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

<div style="page-break-after: always;"></div>
### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

**Purpose**: The Commons package contains utility classes, core configurations, and shared exceptions that are used across multiple components of the application. This promotes code reuse, reduces duplication, and provides a centralized location for common functionality.

**Package Structure**:

The Commons package is organized into three main sub-packages:

#### `seedu.address.commons.core`

Contains core classes that define fundamental application settings and behaviors:

* **`Config`** - Stores configurable values used throughout the app, such as logging level and user preferences file path. Loaded from `config.json` at startup.
* **`GuiSettings`** - Encapsulates GUI-specific settings like window size and position.
* **`LogsCenter`** - Configures and manages all loggers in the application. Provides centralized logging to both console and file (`addressbook.log`).
* **`Version`** - Represents the application version number in semantic versioning format (major.minor.patch).
* **`Index`** - Represents a zero-based or one-based index, used to avoid confusion when passing indices between components that may use different indexing conventions.

#### `seedu.address.commons.exceptions`

Contains custom exception classes used across the application:

* **`DataLoadingException`** - Thrown when there is an error loading data from a file (e.g., corrupted JSON in storage).
* **`IllegalValueException`** - Thrown when a data value does not conform to expected constraints (e.g., invalid email format).

These exceptions allow components to handle errors in a consistent and meaningful way.

#### `seedu.address.commons.util`

Contains utility classes that provide helper methods for common operations:

* **`StringUtil`** - String manipulation utilities (e.g., checking if a sentence contains a word, validating integer strings).
* **`FileUtil`** - File operation utilities (e.g., checking if a file exists, reading/writing files).
* **`JsonUtil`** - JSON serialization and deserialization utilities using Jackson library.
* **`CollectionUtil`** - Utilities for null-checking and validating collections.
* **`ConfigUtil`** - Utilities for reading and saving `Config` objects.
* **`AppUtil`** - General application utilities (e.g., argument validation with `checkArgument`).
* **`ToStringBuilder`** - Builder pattern implementation for generating `toString()` output in a consistent format.
* **`ScreenBoundsValidator`** - Validates and adjusts GUI window bounds to ensure they fit within screen dimensions.

**Usage Examples**:

1. **Index conversion**: When the UI displays a one-based list to users but the Model uses zero-based indexing internally, `Index` handles the conversion:
   ```java
   Index index = ParserUtil.parseIndex("1"); // User sees "1"
   int zeroBasedIndex = index.getZeroBased(); // Model uses 0
   ```

2. **Input validation**: `StringUtil` is used by parsers to validate user input:
   ```java
   if (!StringUtil.isNonZeroUnsignedInteger(args)) {
       throw new ParseException("Index must be a positive integer");
   }
   ```

3. **Exception handling**: Storage component throws `DataLoadingException` when JSON files are corrupted, which is caught by MainApp to initialize with sample data instead.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Filter feature 

The filter feature allows users to filter companies by their application status and/or tags. This is implemented through the `FilterCommand` class.

#### Implementation

The `FilterCommand` works as follows:

1. The user executes `filter <s/STATUS|t/TAG> [t/TAG]...` (e.g., `filter s/in-process`, `filter t/remote-friendly`, or `filter s/applied t/tech`)
2. The `FilterCommandParser` parses the status and/or tag parameters and creates a `FilterCommand` object
3. The `FilterCommand` updates the filtered company list in the model using a predicate that matches companies with:
   - The specified status (if provided), AND
   - Any of the specified tags as substrings (if provided)
4. The UI automatically updates to display only companies matching the filter

The sequence diagram below illustrates the interactions within the `Logic` and `Model` components when executing the command `filter s/applied t/tech`:

![Filter Sequence Diagram](images/FilterSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `FilterCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the filtering mechanism works:
1. The `FilterCommandParser` tokenizes the input arguments to extract the status and tag parameters
2. It validates the status value (if provided) using `ParserUtil.parseStatus()` and parses any tag keywords using `ParserUtil.parseTags()`
3. A `FilterCommand` object is created with the parsed status and tag keywords
4. When executed, the `FilterCommand` creates a `FilterPredicate` with the filtering criteria
5. The predicate is passed to `Model#updateFilteredCompanyList()` which applies it to the company list
6. The filtered list size is retrieved and used to generate a success message
7. A `CommandResult` containing the success message is returned to the UI

The supported status values are:
* `to-apply` - Companies the user plans to apply to.
* `applied` - Applications that have been submitted.
* `oa` - Online assessment stage.
* `tech-interview` - Technical interview stage.
* `hr-interview` - HR interview stage.
* `in-process` - Applications currently in process.
* `offered` - Received an offer.
* `accepted` - Accepted an offer.
* `rejected` - Application rejected.

#### Design considerations

**Aspect: How to implement filtering:**

* **Alternative 1 (current choice):** Use a predicate to filter the ObservableList.
  * Pros: Simple to implement, leverages existing filtered list functionality.
  * Cons: Limited to single status filtering at a time.

* **Alternative 2:** Allow multiple status filters.
  * Pros: More flexible for users who want to see multiple statuses.
  * Cons: More complex parsing and predicate logic required.

### Metrics feature

The metrics feature provides users with statistics of the status of all their applications. This is implemented through the `MetricsCommand`, `MetricsWindow`, and `MetricsCalculator` classes, with window lifecycle management handled by the `MainWindow`.

#### Implementation

The metrics feature operates through a command-window interaction pattern with separation of concerns. The implementation involves four main components:

1. **Command Execution**: `MetricsCommand` processes the user input and signals the UI.
2. **Window Management**: `MainWindow` handles the lifecycle of the `MetricsWindow`.
3. **UI Display**: `MetricsWindow` manages the window state and data refresh.
4. **Data Processing**: `MetricsCalculator` performs calculations and UI rendering.

**Detailed Implementation Flow:**

**Step 1: Command Processing**
1. User executes `metrics` command.
2. `MetricsCommandParser` validates no parameters are provided.
3. `MetricsCommand#execute()` returns `CommandResult` with `showMetrics` flag set to `true`.

**Step 2: Window Lifecycle Management (MainWindow)**
4. `MainWindow#executeCommand()` detects the `showMetrics` flag in `CommandResult`.
5. `MainWindow#handleMetrics()` is called, which:
   - Checks if `MetricsWindow` instance exists and is showing.
   - If not showing: calls `metricsWindow.setData(logic.getAddressBook())` and `metricsWindow.show()`.
   - If already showing: restores from minimized state if needed, updates data, and focuses window.
   - Auto-updates metrics display after any command execution when window is visible.

**Step 3: Data Processing and UI Rendering**
6. `MetricsWindow#setData()` triggers `refreshMetrics()`, which delegates to `MetricsCalculator` to:
   - Calculate status distribution using Java streams grouping.
   - Generate `MetricsData` with counts, percentages, and display order.
   - Render JavaFX labels in the UI container with styling.

**Real-time Update Mechanism:**

The metrics window automatically refreshes after every command execution if visible, eliminating the need for users to re-run the `metrics` command. This is implemented via a hook in `MainWindow#executeCommand()` that calls `metricsWindow.setData()` when `isShowing()` returns true, providing live statistics during data modification sessions.

**Data Architecture:**

The `MetricsCalculator.MetricsData` inner class encapsulates calculated metrics:
- `totalCompanies`: Total count for percentage calculations.
- `statusCounts`: Map of status strings to occurrence counts.
- `statusOrder`: Display order derived from `Arrays.stream(Status.Stage.values())` ensuring single source of truth.
- Helper methods: `getStatusCount()`, `getStatusPercentage()`, `hasData()`.

**Status Enum Integration:**

The metrics feature maintains consistency by using `Status.Stage` enum as the single source of truth:
- **Status ordering**: Display order derived from `Status.Stage.values()` array sequence.
- **Status extraction**: Companies grouped using canonical `toUserInputString()` method.
- **Display consistency**: Metrics always reflect authoritative status definitions from domain model.

#### Design considerations

**Aspect: Data presentation method:**

* **Alternative 1 (current choice):** Separate popup window.
  * Pros: Doesn't interfere with main workflow; can be kept open for reference; dedicated space for detailed statistics.
  * Cons: Additional window management complexity; potential for users to lose/forget the window.

* **Alternative 2:** Inline display in main window.
  * Pros: Simpler implementation; always visible; no window management needed.
  * Cons: Takes space away from company list; less detailed view possible; temporary display only.

**Aspect: Data refresh strategy:**

* **Alternative 1 (current choice):** Multi-trigger refresh - updates after command execution (if visible), when window gains focus, and when restored from minimized state.
  * Pros: Always current data; immediate updates during active sessions; handles all window state changes; simple hook implementation.
  * Cons: Recalculation overhead on every command; unnecessary updates for non-data commands.

* **Alternative 2:** Refresh only when window gains focus or is shown.
  * Pros: Reduced computation overhead; updates only when needed.
  * Cons: Potential stale data if window remains open; users might not see latest changes.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th company in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/Meta …​` to add a new company. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the company was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/Meta …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the company being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Computer Science students mass applying for internships.
* Well-accustomed to CLI-interfaces and prefers keyboard shortcuts over GUI interfaces.
* Fast typist who prefers typing to mouse interactions.
* Makes occasional data entry mistakes.
* Needs to manage a significant number of internship applications simultaneously.
* Is reasonably comfortable using CLI apps.

**Value proposition**: Helps students keep track of prospective and current internship companies and their application status in a centralized location. Manages applications faster than a typical mouse/GUI driven app.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                     | So that I can…​                                                        |
| -------- | ------------------------------------------ | ------------------------------ | ---------------------------------------------------------------------- |
| `* * *`  | first-time applicant                       | track potential companies I might apply for in the future | apply for them after I have made substantial progress |
| `* * *`  | applicant who loves structure and organisation | add my interested companies | store and organize my internship applications |
| `* * *`  | user who dislikes clutter                  | delete an existing internship application | remove outdated or unnecessary entries from my tracker |
| `* *`    | mass internship applicant                  | record the OA questions that I have done before | revise them for future interviews |
| `* *`    | overzealous student applying to a ridiculous number of companies | assign scores to each aspect of a company | compare the companies based on my preferences |
| `* *`    | person who holds regard in contacting HR   | store HR contact details in the tracker | reach out to them easily |
| `* *`    | student that is able to land multiple internships | sort the companies by pay | renegotiate my salaries and make decisions between offers more easily |
| `* *`    | person who is concerned with work life balance | sort the companies by internship benefits | maximize my work life balance when deciding on an internship |
| `* *`    | busy applicant                             | sort my internship applications by the stages | keep track of where I am in each process |
| `* *`    | prospective user                           | differentiate between applications that have referral and those who don't | prioritize the applications that I want to focus on |
| `* *`    | intern with specific work preferences      | sort by the mode of work that I most prefer | prioritize the interns with the type of work arrangement that I prefer |
| `* *`    | person who wants to save time              | sort by travel duration | prioritize those that are closer to me |
| `* *`    | applicant applying for different roles     | have easy access to different versions of my resume | apply to different role openings quickly without needing to edit my resume regularly |
| `* *`    | user who often forgets deadlines           | set reminders for application deadlines | not miss important application deadlines |
| `* *`    | user who often forgets deadlines           | set reminders for OA deadlines | not miss important OA deadlines |
| `* *`    | user who often forgets deadlines           | set reminders for interview deadlines | never miss important interview deadlines |
| `*`      | user who prefers keyboard shortcuts over mouse clicks | always be typing while minimizing having to switch to my mouse/trackpad | save time and work efficiently |
| `*`      | organised person                           | filter applications by status (Applied, Interviewing, Offer, Rejected) | focus on pending or priority applications |
| `*`      | forgetful person                           | see my notes that I took about each company | prepare for my interviews |
| `*`      | busy student with packed schedules         | see which internship periods will clash with my school term | take note of them and decide accordingly if I can afford the clash |
| `*`      | busy applicant                             | track my current open applications that I might have left halfway | get back to my open applications that I have done halfway |
| `*`      | forgetful Computer Science student         | sort my internship applications by the next deadline | remember what interview or assessment to prepare for next |
| `*`      | user who makes frequent careless mistakes  | update the details of an existing company | keep the information accurate and up-to-date |
| `*`      | user                                       | view all companies at once | see the overall status of my applications |
| `*`      | impatient user                             | search applications by name, company, role, or application status | quickly locate a specific application without scrolling through the entire list |
| `*`      | student that loves to be organised         | sort applications alphabetically or by other fields (dates, ranking) | browse applications more efficiently |
| `*`      | error-prone person                         | undo or redo my recent changes | correct mistakes easily |
| `*`      | careless student                           | detect potential duplicate applications | keep my application book clean and not resubmit applications |
| `*`      | user who may not always have all the necessary information ready at once | create a partial entry with whatever details I have at the moment | fill in the rest later when I acquire it |

### Use cases

(For all use cases below, the **System** is `Cerebro` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC01 - Delete company/companies**

**Preconditions**: a company list is currently displayed and the user can see the target indices

**MSS**

1.  User requests to delete one or more company/companies in the list by index/indices
2.  Cerebro asks the user to confirm the deletion
3.  User Confirms
4.  Cerebro deletes the specified company/companies

    Use case ends.

**Extensions**

* 1a. The given index/indices are invalid (non-numeric, zero, negative, or out of range).

  * 1a1. Cerebro shows an error message indicating invalid index/indices.

    Use case resumes at step 1.

* 1b. The index/indices is missing.

  * 1b1. Cerebro shows the correct command formatting.

    Use case resumes at step 1.

* 2a. User cancels the confirmation.

  * 2a1. Cerebro reports that deletion was cancelled.

    Use case ends.

---

**Use case: UC02 - List all companies**

**MSS**

1. User requests to list all companies
2. Cerebro displays a list of all companies.
   
    Use case ends.

**Extensions**

* 1a. Extra parameters are provided with the list command.

  * 1a1. Cerebro ignores the extra parameter

    Use case ends.

---

**Use case: UC03 - Add a company**

**MSS**

1. User requests to add a company with specified field(s).
2. Cerebro validates the input and creates the company entry.
3. Cerebro records the new company.

   Use case ends.

**Extensions**

* 1a. Missing name/invalid field input(s) (e.g., malformed phone numbers, tag too long).

    * 1a1. Cerebro reports the missing name/invalid field(s).

    Use case ends.
  
* 1b. Duplicate company name detected

    * 1b1. Cerebro reports a duplicate-company error.
  
    Use case resumes at step 1.
---

**Use case: UC04 - Clear all companies**

**MSS**

1. User requests to clear all companies.
2. Cerebro ask to confirm the action.
3. User confirms.
4. Cerebro removes all companies.

   Use case ends.

**Extensions**

* 1a. Extra parameters are supplied to a command that takes none.

    * 1a1. Cerebro ignores the extra parameters.

  Use case resumes at step 2.

* 2a. User cancels the confirmation.

    * 2a1. Cerebro reports that clearing was cancelled.

  Use case ends.

---

**Use case: UC05 - Edit company/companies details**

**Preconditions**: a company list is currently displayed and the user can see the target indices

**MSS**

1.  User requests to edit one or more companies by index/indices, supplying new field values (e.g., status, remark, tags).
2.  Cerebro validates the input and applies the updates.
3.  Cerebro records the update(s).

   Use case ends.

**Extensions**

* 1a. No editable field is provided.

    * 1a1. Cerebro shows an error message the correct command format.

  Use case resumes at step 1.

* 1b. Some field values are invalid (e.g., unsupported status value, malformed phone number).

    * 1b1. Cerebro shows an error indicating the invalid field(s).

  Use case resumes at step 1.

* 1c. Any provided index/indices is invalid.

    * 1c1. Cerebro shows an error indicating invalid index/indices.
  
  Use case resumes at step 1.

* 1d. For a (single-company) edit, the new NAME duplicates an existing company’s name.

    * 1d1. Cerebro rejects the command and shows a duplicate-company error.

    Use case resumes at step 1.

* 1e. The request attempts a batch edit (multiple indices) that includes the NAME field.

    * 1e1. Cerebro rejects the command and shows an error stating that batch editing the NAME field is not allowed.

    Use case resumes at step 1.

---

**Use case: UC06 - Filter companies by status**

**MSS**

1.	User requests to filter companies, supplying one or more criteria (status and/or tag).
2.	Cerebro displays the list of companies that match the criteria.

Use case ends.

**Extensions**

* 1a. A required value is missing or malformed (e.g., invalid STATUS, empty t/).

    * 1a1. Cerebro shows the correct command format and the set of valid values for each criterion.

  Use case resumes at step 1.

---

**Use case: UC07 - Find companies by keyword(s)**

**MSS**

1.	User requests to find companies by keyword(s).
2.  Cerebro shows companies whose names match the keyword(s).

Use case ends.

**Extensions**

* 1a. The keyword list is empty or blank.

    * 1a1. Cerebro shows the correct command format.

  Use case resumes at step 1.

---

**Use case: UC08 - View application metric**

**MSS**

1.	User requests to view application metrics.
2.  Cerebro computes metrics (e.g., counts per application status) and makes the available to user.

Use case ends.

**Extensions**

* 1a. Extra parameters are supplied to a command that takes none.

  * 1a1. Cerebro ignores the extra parameters.

  Use case resumes at step 2.

* 1b. There are no companies.

    * 1b1. Cerebro displays a message indicating there are no companies to show

  Use case ends.

---

**Use case: UC09 - Help**

**MSS**

1.	User requests help.
2.	Cerebro provide usage information.

Use case ends.

**Extensions**

* 1a. Extra parameters are supplied to a command that takes none.

    *  1a1. Cerebro ignores the extra parameters.
  
    Use case ends.

---

**Use case: UC10 - Exit application**

**MSS**

1.	User requests to exit the application (e.g., exit).
2.  Cerebro performs shutdown tasks (flushes pending writes if any) and terminates.

Use case ends.

**Extensions**

* 1a. Extra parameters are supplied with exit.

    *  1a1. Cerebro ignores the extra parameters and proceeds to exit.

  Use case resumes at step 2.

### Non-Functional Requirements

#### Performance
* The system shall respond to any command operation within 3 seconds when managing up to 100 internship applications.
* A user with above average typing speed, typically around 50–60 words per minute (WPM), for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks (e.g. adding, deleting, finding) faster using commands than using the mouse.
* The system shall launch within 3 seconds on _standard hardware_.

#### Reliability & Availability
* Should work on any _mainstream OS_ as long as it has Java '17' or above installed.
* The app shall operate offline with full feature availability.

#### Security & Privacy
* User data must only be stored locally and accessible through the host OS file system — no external server transmission.
* No unintended data exposure between multiple OS user accounts.

#### Scalability & Capacity
* The system shall support at least 100 application entries without significant degradation of performance.
* Find, filter and list operations shall scale linearly with data size up to this limit.

#### Usability & Accessibility
* New users shall be able to complete core tasks (add, list, find) after < 10 minutes of onboarding using the User Guide.
* The interface shall support CLI-first workflow, and screen reader compatibility.

#### Error Handling & Robustness
* The application shall remain operational without unexpected termination for both valid and invalid user inputs.
* The application shall display clear, actionable error messages to help users understand and correct invalid inputs.
* All command parsing errors shall be caught and presented to the user with the correct command format.
* Invalid data in storage files (corrupted JSON, invalid field values) shall not cause the application to crash; the application shall start with an empty data set or sample data instead.

### Glossary

* **Application**: The entire process of securing a potential internship with the company, starting from the first contact with the company (via email or otherwise) to the point of securing the internship. Each application has an associated Status that tracks its current stage.
* **CLI-first interface**: an interface that prioritises keyboard-only interactions in order to optimise for speed of usage.
* **Command Prefix**: Prefixes like n/, s/, t/ used to specify field types in CLI commands.
* **Company**: Any entity, legally registered or otherwise, that the user can undertake an internship at. Company names must be unique (case-insensitive).
* **GUI**: Graphical User Interface - The visual interface components built with JavaFX, as opposed to the command-line interface.
* **Mainstream OS**: Windows, Linux, MacOS.
* **Standard Hardware**: Hardware meeting minimum requirements: 4GB RAM, Intel i3 equivalent processor (2015 or newer), 500MB available disk space, Java 17+ JVM.
* **Status**: Application stage enum defined in `Status.Stage` (TO_APPLY, APPLIED, OA, TECH_INTERVIEW, HR_INTERVIEW, IN_PROCESS, OFFERED, ACCEPTED, REJECTED).
* **Tag**: Alphanumeric labels with single hyphens to separate words (max 30 chars) used for categorizing companies, stored in lowercase.
* **OA**: Online Assessment - A digital evaluation typically containing coding challenges, technical questions, or aptitude tests that companies use to screen candidates early in the application process.


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample companies. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Deleting a company

1. Deleting a company while all companies are being shown

   1. Prerequisites: List all companies using the `list` command. Multiple companies in the list.

   1. Test case: `delete 1`<br>
      Expected: First company is deleted from the list. Details of the deleted company shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No company is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.


### Filtering companies by status and/or tags

1. Filtering companies by application status

   1. Prerequisites: Have companies with various statuses and tags in the list. Use `list` to see all companies.

   1. Test case: `filter s/applied`<br>
      Expected: Only companies with status "applied" are shown. Number of companies displayed shown in the status message.

   1. Test case: `filter s/in-process`<br>
      Expected: Only companies with status "in-process" are shown.

   1. Test case: `filter s/invalid-status`<br>
      Expected: Error message shown indicating invalid status. List remains unchanged.

2. Filtering companies by tags

   1. Prerequisites: Have companies with various tags in the list. Use `list` to see all companies.

   1. Test case: `filter t/remote`<br>
      Expected: Only companies with tags containing "remote" (e.g., "remote-friendly", "remote-work") are shown.

   1. Test case: `filter t/remote t/tech`<br>
      Expected: Companies with tags containing "remote" OR "tech" are shown.

3. Filtering companies by both status and tags

   1. Prerequisites: Have companies with various statuses and tags in the list. Use `list` to see all companies.

   1. Test case: `filter s/applied t/tech`<br>
      Expected: Only companies with status "applied" AND tags containing "tech" are shown.

   1. Test case: `filter s/in-process t/remote t/good`<br>
      Expected: Companies with status "in-process" AND tags containing "remote" OR "good" are shown.

4. Error cases

   1. Test case: `filter` (missing parameters)<br>
      Expected: Error message showing correct command format.

### Viewing metrics

1. Opening the metrics window

   1. Test case: `metrics`<br>
      Expected: Metrics window opens showing statistics about application statuses. If window is already open, it is brought to focus and data is refreshed.

   1. Test case: Close the metrics window and run `metrics` again<br>
      Expected: Metrics window reopens with current data.

### Saving data

1. Dealing with missing/corrupted data files

   1. **Test case: Missing data file on startup**<br>
      Prerequisites: Delete or rename the file `data/Cerebro.json` before launching the application.<br>
      Expected: Application starts successfully with 8 sample companies pre-loaded (Acme Corporation, TechVision Solutions, Global Logistics Pte Ltd, Sunrise Manufacturing, Digital Innovations Hub, Pacific Trading Co, Nexus Robotics, and Orion Analytics). A new `data/Cerebro.json` file is created with the sample data.
   1. **Test case: Corrupted data file with invalid JSON syntax**<br>
      Prerequisites: Replace the contents of `data/Cerebro.json` with plain text (e.g., "not json format!" or any non-JSON content).<br>
      Expected: Application starts successfully with an empty address book (no companies loaded). The corrupted file is not overwritten or deleted unless new companies are added in the app, in which case the corrupted file is overwritten with the new addition(s).

   1. **Test case: Valid JSON but invalid company data**<br>
      Prerequisites: Edit `data/Cerebro.json` to contain valid JSON structure but with invalid data values. For example, set a company's phone field to `"invalidPhone123!@#"` or set a company's name to empty/whitespace-only string.<br>
      Expected: Application starts successfully with an empty address book. The data file is not overwritten or deleted unless new companies are added in the app, in which case the data file is overwritten with the new addition(s).

1. Saving data automatically after commands

   1. **Test case: Data persists after adding a company**<br>
      Prerequisites: Launch application with existing data.<br>
      Steps: Execute `add n/NewCompany p/91234567 e/new@company.com a/123 Street` and wait for success message. Close the application and relaunch it.<br>
      Expected: The newly added company "NewCompany" appears in the list upon relaunch. The data has been automatically saved to `data/Cerebro.json` upon execution of the command.

   1. **Test case: Data persists after deleting companies**<br>
      Prerequisites: List has at least 3 companies.<br>
      Steps: Execute `delete 1,3` to delete companies at index 1 and 3. Close and relaunch the application.<br>
      Expected: The deleted companies are deleted from data/Cerebro.json upon execution of command. The remaining companies are preserved with their data intact.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Planned Enhancements**

Team size: 5

1. **Filter by multiple statuses at once with same OR logic as tags.** Currently, our filter method only accepts 1 status input to filter by. However, users might want to see companies of certain statuses at the same time, and hence would like to filter by multiple statuses. Through this enhancement, users would be able to type in multiple statuses, like `filter s/offered s/rejected` and see all companies with one of the mentioned statuses.

2. **Allow filtering within find results or finding within filter results.** Currently, users cannot apply a `filter` command after executing a `find` command, or vice versa. For example, running `find test` followed by `filter s/applied` does not filter within the found companies that match "test" - instead, it filters across all companies in the address book, ignoring the previous `find` results. This limitation prevents users from narrowing down their search progressively. Through this enhancement, users would be able to chain `find` and `filter` commands to progressively narrow their search results, allowing for more flexible and powerful querying of their internship applications.

3. **Show changed fields in edit success message.** Currently, `edit` only reports that an update happened (e.g."Edited 2 companies successfully") without indicating what changed, forcing users to scan entries. With this enhancement, the success message will include modified fields (e.g., `status` `APPLIED → INTERVIEW`, `+backend`, `-frontend`, `remark` `set/cleared`, `name` changed). For batch edits, a single consolidated summary is shown (e.g., `Edited` 4 companies (`fields` changed: `status` `APPLIED → REJECTED`)); if nothing effectively changes, display No `fields` changed. This keeps verification fast and consistent for keyboard-first workflows.

4. **Enable arrow key navigation in help window.** Currently, the help window only supports mouse scrolling and keyboard shortcuts for closing (ESC, Ctrl/Cmd+W, Alt+F4), but users cannot navigate through the help content using UP/DOWN arrow keys. This is inconvenient for CLI users and fast typists who prefer keyboard-only workflows and want to avoid reaching for the mouse. Through this enhancement, users will be able to use UP/DOWN arrow keys to scroll through the help content, with the TextArea automatically receiving focus when the help window opens. This aligns with Cerebro's target audience of users who value keyboard efficiency over GUI interactions.

5. **Add tag appending/removing syntax alongside current replacement behavior.** Currently, when editing tags, all existing tags are completely replaced. For example, if a company has tags `backend` and `remote`, and a user wants to add `fulltime`, they must retype all previous tags: `edit 1 t/backend t/remote t/fulltime`. This is inefficient for fast typists who want to quickly add or remove individual tags without retyping everything. Through this enhancement, we will add append/remove syntax (e.g., `edit 1 t/+fulltime` to add a tag, `edit 1 t/-remote` to remove a tag) while keeping the current complete replacement behavior as the default when using standard syntax. This reduces typing overhead and maintains workflow efficiency for CLI-focused users who frequently manage tags during their internship application tracking.

6. **Add dedicated deadline field with enhanced GUI visibility.** Currently, important deadlines for OA, tech interviews, and HR interviews are tracked in the free-text remarks field, making time-critical information buried in unstructured text. Fast typists managing 30-50+ internship applications must manually scan through varied remark formats ("Interview on 15th Dec", "OA due Dec 15", "Tech interview: 2024-12-15") to extract deadline information, breaking their keyboard-focused workflow by forcing visual parsing of inconsistent text formats. This prevents efficient sorting, filtering, or batch operations on deadline data, and makes it impossible to implement automated deadline reminders or visual urgency indicators. Through this enhancement, we will add a dedicated `d/DEADLINE` field to the Company entity with prominent GUI display (e.g., highlighted date, color coding for urgent deadlines), enabling structured deadline management that aligns with CLI users' preference for consistent, parseable data formats rather than free-form text scanning.

7. **Add status abbreviations support for faster status updates.** Currently, the existing status validation requires typing long status names like `TECH-INTERVIEW`, `HR-INTERVIEW`, `IN-PROCESS` which require many keystrokes and slow down fast typists during frequent status updates. Through this enhancement, we will enhance the status parsing system to accept common abbreviations alongside full status names: `ta` for `to-apply`, `ap` for `applied`, `ti` for `tech-interview`, `hi` for `hr-interview`, `ip` for `in-process`, `of` for `offered`, `ac` for `accepted`, `re` for `rejected`. This significantly reduces keystrokes from 13+ characters to just 2-3 characters per status change, maintaining backward compatibility with full status names while making the system much more efficient for CLI users who frequently update application statuses.

--------------------------------------------------------------------------------------------------------------------

## Appendix: Effort

### Difficulty Level

While AB3 deals with only one entity type (`Person`), Cerebro is significantly more complex as it transforms the simple contact management paradigm into a specialized internship application tracking system. The project required not just renaming fields, but reimagining the entire domain model, adding domain-specific features (9-stage status pipeline, batch operations, advanced filtering), and implementing strict validation rules appropriate for professional internship tracking.

### Challenges and Effort Required

**1. Batch Operations Architecture (~15-20% additional effort)**

AB3 only supports single-entity operations (`edit 1`, `delete 2`). We implemented batch editing and deletion to handle high-volume internship applications (students typically manage 30-50+ companies):
- **Challenge:** Engineering flexible index notation supporting comma-separated indices (`edit 1,3,5`), ranges (`edit 2-4`), and combinations (`edit 1,3,6-8,10`)
- **Implementation:** Enhanced parser to handle multiple index formats, validate ranges, detect duplicate indices, and prevent name conflicts during batch edits
- **Testing complexity:** Extensive edge case testing for invalid ranges, out-of-bounds indices, and referential integrity

**2. Domain-Specific Entity Transformation (~20-25% additional effort)**

- **Person → Company:** Completely reoriented the core entity from contact management to opportunity tracking. Added critical new fields: `Status` (9-stage application pipeline: TO-APPLY → APPLIED → OA → TECH-INTERVIEW → HR-INTERVIEW → IN-PROCESS → OFFERED → ACCEPTED/REJECTED) and `Remark` (for interview feedback, referral notes)
- **Challenge:** Implementing case-insensitive company name handling, creating comprehensive validation logic for 9 distinct statuses, and ensuring data integrity
- **Tag repurposing:** Reimagined tags from generic labels ("friend", "colleague") to internship-specific descriptors ("frontend", "backend", "remote-work") with strict validation (max 30 chars, alphanumeric with hyphens only, case-insensitive storage)

**3. Advanced Filtering System (~10-15% additional effort)**

AB3's `find` command only supports simple name-based searches with exact word matching. We implemented:
- **Filter command:** `filter <s/STATUS|t/TAG> [t/TAG]…` supporting status filtering (exact match), tag filtering (substring match), and combined filtering with AND/OR logic
- **Enhanced find:** Substring matching (`find Go` matches "Google"), case-insensitive search, and OR logic across multiple substrings
- **Challenge:** Creating custom predicate classes, implementing substring matching logic, combining predicates with complex boolean logic, and maintaining filter state across UI updates

**4. Metrics and Analytics (~10% additional effort)**

AB3 has no analytics features. We implemented a `metrics` command that displays real-time distribution of applications across all 9 statuses in a separate window, helping users understand their application funnel at a glance.

**5. Enhanced Field Validation (~10% additional effort)**

Implemented stricter, domain-specific validation appropriate for professional internship tracking:
- Upgraded email validation to Apache Commons Validator (RFC 822 standard)
- Enhanced phone validation (min 3 digits, international format support with `+`, single spaces between digits)
- Strict tag validation (max 30 chars, alphanumeric with single hyphens, case-insensitive storage)
- Case-insensitive duplicate company name detection

**6. Escape Character System (~5% additional effort)**

Implemented backslash escaping (`add n/Company r/Meet with Ollie's \s/o`) to allow slash characters in fields without triggering parameter prefix parsing—a unique challenge requiring parser modifications and complex edge case handling.

### Achievements

- Successfully transformed AB3 from a generic contact manager into a specialized, production-ready internship tracking system tailored for CS students
- Implemented batch operations that enable users to manage dozens of applications efficiently in a single command
- Created a sophisticated 9-stage status pipeline that provides clear visibility into application progress
- Designed an intuitive filtering system with flexible matching logic (substring, AND/OR combinations)
- Achieved comprehensive test coverage despite significantly increased complexity

### Reuse from AB3

Significant effort (~15-20%) was saved by reusing AB3's foundation:
- **Storage system:** JSON serialization/deserialization logic was largely reusable with minor adaptations for new fields (`Status`, `Remark`)—our work on adapting the storage system is contained in `JsonAdaptedCompany.java` and `JsonSerializableAddressBook.java`
- **UI framework:** JavaFX structure and base components were reused, though heavily customized for displaying companies, status, and metrics window
- **Command pattern:** AB3's command architecture provided a solid foundation for our enhanced commands with batch operations

### Estimated Total Effort

| Component | Effort Compared to AB3 Baseline |
|-----------|---------------------------------|
| Basic AB3 functionality | ~100%                           |
| Domain transformation + Status pipeline | +25%                            |
| Batch operations | +20%                            |
| Advanced filtering | +15%                            |
| Metrics + Enhanced validation + Escape system | +25%                            |
| **Subtotal** | **~185%**                       |
| **Reuse benefit** | **20%**                         |
| **Total Estimated Effort** | **~165% of AB3 baseline**       |

Cerebro required approximately **65% more effort** than baseline AB3, with the most significant challenges being batch operations, domain-specific entity transformation, and advanced filtering—all critical features for CS students managing high-volume internship applications.
