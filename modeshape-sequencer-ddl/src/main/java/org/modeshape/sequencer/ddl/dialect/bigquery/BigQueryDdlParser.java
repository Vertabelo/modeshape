package org.modeshape.sequencer.ddl.dialect.bigquery;

import org.modeshape.common.text.ParsingException;
import org.modeshape.common.text.Position;
import org.modeshape.sequencer.ddl.DdlParserProblem;
import org.modeshape.sequencer.ddl.DdlSequencerI18n;
import org.modeshape.sequencer.ddl.DdlTokenStream;
import org.modeshape.sequencer.ddl.StandardDdlParser;
import org.modeshape.sequencer.ddl.datatype.DataType;
import org.modeshape.sequencer.ddl.datatype.DataTypeParser;
import org.modeshape.sequencer.ddl.node.AstNode;

import java.util.ArrayList;
import java.util.List;

import static org.modeshape.sequencer.ddl.StandardDdlLexicon.CREATE_VIEW_QUERY_EXPRESSION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.DDL_PROBLEM;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.OR_REPLACE_CLAUSE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TEMPORARY;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_DEFINITION;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_COLUMN_REFERENCE;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_TABLE_STATEMENT;
import static org.modeshape.sequencer.ddl.StandardDdlLexicon.TYPE_CREATE_VIEW_STATEMENT;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlConstants.BigQueryStatementStartPhrases.CREATE_PHRASES;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlConstants.BigQueryStatementStartPhrases.CUSTOM_KEYWORDS;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlConstants.BigQueryStatementStartPhrases.STMT_CREATE_MATERIALIZED_VIEW;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlConstants.BigQueryStatementStartPhrases.STMT_CREATE_OR_REPLACE_TABLE;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlConstants.BigQueryStatementStartPhrases.STMT_CREATE_OR_REPLACE_TEMPORARY_TABLE;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlConstants.BigQueryStatementStartPhrases.STMT_CREATE_OR_REPLACE_TEMP_TABLE;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlConstants.BigQueryStatementStartPhrases.STMT_CREATE_TEMPORARY_TABLE;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlConstants.BigQueryStatementStartPhrases.STMT_CREATE_TEMP_TABLE;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.CLUSTER_BY;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.DATASET_NAME;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.ENABLE_REFRESH;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.EXPIRATION_TIMESTAMP;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.FRIENDLY_NAME;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.IF_NOT_EXISTS_CLAUSE;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.KMS_KEY_NAME;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.LABELS;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.MATERIALIZED;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.PARTITION_BY;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.PARTITION_EXPIRATION_DAYS;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.PROJECT_NAME;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.REFRESH_INTERVAL_MINUTES;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.REQUIRE_PARTITION_FILTER;
import static org.modeshape.sequencer.ddl.dialect.bigquery.BigQueryDdlLexicon.DESCRIPTION;

public class BigQueryDdlParser extends StandardDdlParser {

    public static final String ID = "BIGQUERY";
    private static final List<String[]> bigQueryDataTypeStrings = new ArrayList<String[]>();

    public BigQueryDdlParser() {
        setDatatypeParser(new BigQueryDataTypeParser());

        setDoUseTerminator(true);
        bigQueryDataTypeStrings.addAll(BigQueryDdlConstants.BigQueryDataTypes.CUSTOM_DATATYPE_START_PHRASES);
    }

    @Override
    protected void initializeTokenStream(DdlTokenStream tokens) {
        super.initializeTokenStream(tokens);
        tokens.registerKeyWords(CUSTOM_KEYWORDS);
        tokens.registerStatementStartPhrase(CREATE_PHRASES);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected AstNode parseCreateStatement(DdlTokenStream tokens, AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        AstNode stmtNode = null;
        if (tokens.matches(STMT_CREATE_TABLE)
            || tokens.matches(STMT_CREATE_OR_REPLACE_TABLE)
            || tokens.matches(STMT_CREATE_TEMPORARY_TABLE)
            || tokens.matches(STMT_CREATE_OR_REPLACE_TEMPORARY_TABLE)
            || tokens.matches(STMT_CREATE_TEMP_TABLE)
            || tokens.matches(STMT_CREATE_OR_REPLACE_TEMP_TABLE)) {
            stmtNode = parseCreateTableStatement(tokens, parentNode);
        } else if (tokens.matches(STMT_CREATE_VIEW)
                   || tokens.matches(STMT_CREATE_OR_REPLACE_VIEW)
                   || tokens.matches(STMT_CREATE_MATERIALIZED_VIEW)) {
            stmtNode = parseCreateViewStatement(tokens, parentNode);
        } else {
            markStartOfStatement(tokens);

            stmtNode = parseIgnorableStatement(tokens, "CREATE UNKNOWN", parentNode);
            Position position = getCurrentMarkedPosition();
            String msg = DdlSequencerI18n.unknownCreateStatement.text(position.getLine(), position.getColumn());
            DdlParserProblem problem = new DdlParserProblem(Problems.WARNING, position, msg);

            stmtNode.setProperty(DDL_PROBLEM, problem.toString());

            markEndOfStatement(tokens, stmtNode);
        }

        return stmtNode;
    }

    @Override
    protected AstNode parseCreateTableStatement(DdlTokenStream tokens, AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);

        tokens.consume(CREATE); // CREATE
        boolean hasOrReplace = tokens.canConsume("OR", "REPLACE");
        boolean hasTemporary = tokens.canConsume("TEMPORARY") || tokens.canConsume("TEMP");
        tokens.consume(TABLE);
        boolean hasNotExists = tokens.canConsume("IF", "NOT", "EXISTS");
        List<String> tableName = parseComposedName(tokens);

        AstNode tableNode = nodeFactory().node(tableName.get(tableName.size() - 1), parentNode, TYPE_CREATE_TABLE_STATEMENT);
        if (tableName.size() == 2) {
            tableNode.setProperty(DATASET_NAME, tableName.get(0));
        } else if (tableName.size() == 3) {
            tableNode.setProperty(DATASET_NAME, tableName.get(1));
            tableNode.setProperty(PROJECT_NAME, tableName.get(0));
        }
        tableNode.setProperty(OR_REPLACE_CLAUSE, hasOrReplace);
        tableNode.setProperty(TEMPORARY, hasTemporary);
        tableNode.setProperty(IF_NOT_EXISTS_CLAUSE, hasNotExists);

        parseColumnsAndConstraints(tokens, tableNode);

        parseCreateTableOptions(tokens, tableNode);

        markEndOfStatement(tokens, tableNode);

        return tableNode;
    }

    private List<String> parseComposedName(DdlTokenStream tokens) {
        // Basically we want to construct a name that could have the form:
        // projectName.datasetName.tableName
        List<String> names = new ArrayList<String>();

        do {
            StringBuilder name = new StringBuilder(consumeIdentifier(tokens));
            while (tokens.canConsume("-")) { // project name in kebab-case
                name.append("-").append(consumeIdentifier(tokens));
            }

            if (name.charAt(0) == '`') {
                name.deleteCharAt(0);
            }
            if (name.charAt(name.length() - 1) == '`') {
                name.deleteCharAt(name.length() - 1);
            }

            names.add(name.toString());
        } while (tokens.canConsume('.')); // .

        return names;
    }

    @Override
    protected boolean areNextTokensCreateTableOptions(DdlTokenStream tokens) throws ParsingException {
        assert tokens != null;

        return tokens.matches("PARTITION", "BY")
               || tokens.matches("CLUSTER", "BY")
               || tokens.matches("OPTIONS");
    }

    @Override
    protected void parseNextCreateTableOption(DdlTokenStream tokens, AstNode tableNode) throws ParsingException {
        assert tokens != null;
        assert tableNode != null;

        if (tokens.canConsume("PARTITION", "BY")) {
            if (tokens.canConsume("DATE")) {
                String dateColumn = parseContentBetweenParens(tokens);
                tableNode.setProperty(PARTITION_BY, "DATE(" + dateColumn + ")");
            } else if (tokens.canConsume("RANGE_BUCKET")) {
                String rangeBucket = parseContentBetweenParens(tokens);
                tableNode.setProperty(PARTITION_BY, "RANGE_BUCKET(" + rangeBucket + ")");
            } else {
                String column = parseName(tokens);
                tableNode.setProperty(PARTITION_BY, column);
            }
        }

        if (tokens.canConsume("CLUSTER", "BY")) {
            StringBuilder clusterByColumns = new StringBuilder();
            clusterByColumns.append(parseName(tokens));
            while (tokens.canConsume(',')) {
                clusterByColumns.append(", ");
                clusterByColumns.append(parseName(tokens));
            }

            tableNode.setProperty(CLUSTER_BY, clusterByColumns.toString());
        }

        if (tokens.canConsume("OPTIONS")) {
            if (tokens.matches(L_PAREN)) {
                String options = parseContentBetweenParens(tokens);
                parseTableOptionList(tableNode,
                                     new DdlTokenStream(options, DdlTokenStream.ddlTokenizer(false), false));
            }
        }
    }

    private void parseTableOptionList(AstNode tableNode, DdlTokenStream tokens) {
        StringBuilder unusedTokensSB = new StringBuilder();
        tokens.start();
        while (tokens.hasNext()) {
            if (tokens.canConsume("PARTITION_EXPIRATION_DAYS", "=")) {
                tableNode.setProperty(PARTITION_EXPIRATION_DAYS, parseName(tokens));
                tokens.canConsume(COMMA);
            } else if (tokens.canConsume("REQUIRE_PARTITION_FILTER", "=")) {
                tableNode.setProperty(REQUIRE_PARTITION_FILTER, parseName(tokens));
                tokens.canConsume(COMMA);
            } else if (tokens.canConsume("KMS_KEY_NAME", "=")) {
                tableNode.setProperty(KMS_KEY_NAME, parseName(tokens));
                tokens.canConsume(COMMA);
            } else if (canParseCommonOption(tableNode, tokens)) {
                // parsed
            } else {
                // THIS IS AN ERROR. NOTHING FOUND.
                // NEED TO absorb tokens
                unusedTokensSB.append(SPACE).append(tokens.consume());
            }
        }
        if (unusedTokensSB.length() > 0) {
            addProblems(tableNode,
                        unusedTokensSB,
                        DdlSequencerI18n.unusedTokensParsingTableOptionList.text(tableNode.getName()));
        }
    }

    private boolean canParseCommonOption(AstNode node, DdlTokenStream tokens) {
        if (tokens.canConsume("FRIENDLY_NAME", "=")) {
            node.setProperty(FRIENDLY_NAME, parseName(tokens));
            tokens.canConsume(COMMA);
        } else if (tokens.canConsume("DESCRIPTION", "=")) {
            node.setProperty(DESCRIPTION, parseName(tokens));
            tokens.canConsume(COMMA);
        } else if (tokens.canConsume("LABELS", "=", "[", "(")) {
            String labels = parseUntilCustomTokenOrTerminator(tokens, ")");
            tokens.consume(")", "]");
            node.setProperty(LABELS, "[(" + labels.trim() + ")]");
            tokens.canConsume(COMMA);
        } else if (tokens.canConsume("EXPIRATION_TIMESTAMP", "=")) {
            StringBuilder sb = new StringBuilder();
            int parents = 0;
            while (tokens.hasNext() && (!tokens.matches(COMMA) || parents > 0)) {
                boolean previousIsParen = tokens.matches(L_PAREN) || tokens.matches(R_PAREN);

                sb.append(tokens.consume());

                boolean currentIsParen = tokens.matches(L_PAREN) || tokens.matches(R_PAREN);
                if (!previousIsParen && !currentIsParen) {
                    sb.append(SPACE);
                }

                if (tokens.matches(L_PAREN)) {
                    parents++;
                } else if (tokens.matches(R_PAREN)) {
                    parents--;
                }
            }
            node.setProperty(EXPIRATION_TIMESTAMP, sb.toString().trim());
            tokens.canConsume(COMMA);
        } else {
            return false;
        }

        return true;
    }

    @Override
    protected void parseColumnDefinition(DdlTokenStream tokens, AstNode tableNode, boolean isAlterTable) throws
                                                                                                         ParsingException {
        String columnName = parseName(tokens);
        DataType datatype = getDatatypeParser().parse(tokens);

        AstNode columnNode = nodeFactory().node(columnName, tableNode, TYPE_COLUMN_DEFINITION);

        getDatatypeParser().setPropertiesOnNode(columnNode, datatype);

        // Now clauses and constraints can be defined in any order, so we need to keep parsing until we get to a comma
        StringBuilder unusedTokensSB = new StringBuilder();

        while (tokens.hasNext() && !tokens.matches(COMMA)) {
            boolean parsedConstraint = parseColumnConstraint(tokens, columnNode, isAlterTable);
            boolean parsedColumnOptionList = parseColumnOptionList(tokens, columnNode);

            if (!parsedConstraint && !parsedColumnOptionList) {
                // THIS IS AN ERROR. NOTHING FOUND.
                // NEED TO absorb tokens
                unusedTokensSB.append(SPACE).append(tokens.consume());
            }

            tokens.canConsume(DdlTokenStream.DdlTokenizer.COMMENT);
        }

        if (unusedTokensSB.length() > 0) {
            addProblems(tableNode, unusedTokensSB,
                        DdlSequencerI18n.unusedTokensParsingColumnDefinition.text(tableNode.getName()));
        }
    }

    private void addProblems(AstNode tableNode, StringBuilder unusedTokensSB, String msg) {
        DdlParserProblem problem = new DdlParserProblem(Problems.WARNING, Position.EMPTY_CONTENT_POSITION, msg);
        problem.setUnusedSource(unusedTokensSB.toString());
        addProblem(problem, tableNode);
    }

    private boolean parseColumnOptionList(DdlTokenStream tokens, AstNode columnNode) {
        if (tokens.canConsume("OPTIONS")) {
            if (tokens.matches(L_PAREN)) {
                String options = parseContentBetweenParens(tokens);
                DdlTokenStream subTokens = new DdlTokenStream(options, DdlTokenStream.ddlTokenizer(false), false);
                subTokens.start();
                if (subTokens.canConsume("DESCRIPTION", "=")) {
                    String description = parseName(subTokens);
                    columnNode.setProperty(DESCRIPTION, description);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected AstNode parseCreateViewStatement(DdlTokenStream tokens, AstNode parentNode) throws ParsingException {
        assert tokens != null;
        assert parentNode != null;

        markStartOfStatement(tokens);
        // <view definition> ::=
        // {CREATE VIEW | CREATE VIEW IF NOT EXISTS | CREATE OR REPLACE VIEW
        // {CREATE MATERIALIZED VIEW | CREATE MATERIALIZED VIEW IF NOT EXISTS }
        // [[project_name.]dataset_name.]view_name
        // [OPTIONS(view_option_list)]
        // AS query_expression

        tokens.consume("CREATE");
        boolean isMaterializedView = tokens.canConsume("MATERIALIZED");
        boolean hasOrReplace = tokens.canConsume("OR", "REPLACE");
        tokens.consume("VIEW");
        boolean hasNotExists = tokens.canConsume("IF", "NOT", "EXISTS");
        List<String> name = parseComposedName(tokens);

        AstNode viewNode = nodeFactory().node(name.get(name.size() - 1), parentNode, TYPE_CREATE_VIEW_STATEMENT);
        viewNode.setProperty(MATERIALIZED, isMaterializedView);
        viewNode.setProperty(OR_REPLACE_CLAUSE, hasOrReplace);
        viewNode.setProperty(IF_NOT_EXISTS_CLAUSE, hasNotExists);

        if (tokens.canConsume("OPTIONS")) {
            if (tokens.matches(L_PAREN)) {
                String options = parseContentBetweenParens(tokens);
                DdlTokenStream optionTokens = new DdlTokenStream(options, DdlTokenStream.ddlTokenizer(false), false);
                parseViewOptionList(viewNode, optionTokens, isMaterializedView);
            }
        }

        // CONSUME COLUMNS
        parseColumnNameList(tokens, viewNode, TYPE_COLUMN_REFERENCE);

        tokens.consume("AS");

        String queryExpression = parseUntilTerminator(tokens);

        viewNode.setProperty(CREATE_VIEW_QUERY_EXPRESSION, queryExpression);

        markEndOfStatement(tokens, viewNode);

        return viewNode;
    }

    private void parseViewOptionList(AstNode viewNode, DdlTokenStream tokens, boolean isMaterializedView) {
        StringBuilder unusedTokensSB = new StringBuilder();
        tokens.start();
        while (tokens.hasNext()) {
            if (isMaterializedView && tokens.canConsume("ENABLE_REFRESH", "=")) {
                viewNode.setProperty(ENABLE_REFRESH, parseName(tokens));
                tokens.canConsume(COMMA);
            } else if (isMaterializedView && tokens.canConsume("REFRESH_INTERVAL_MINUTES", "=")) {
                viewNode.setProperty(REFRESH_INTERVAL_MINUTES, parseName(tokens));
                tokens.canConsume(COMMA);
            } else if (canParseCommonOption(viewNode, tokens)) {
                // parsed
            } else {
                // THIS IS AN ERROR. NOTHING FOUND. NEED TO absorb tokens
                unusedTokensSB.append(SPACE).append(tokens.consume());
            }
        }
        if (unusedTokensSB.length() > 0) {
            addProblems(viewNode,
                        unusedTokensSB,
                        DdlSequencerI18n.unusedTokensParsingTableOptionList.text(viewNode.getName()));
        }
    }

    @Override
    protected List<String> getCustomDataTypeStartWords() {
        return BigQueryDdlConstants.BigQueryDataTypes.CUSTOM_DATATYPE_START_WORDS;
    }

    static class BigQueryDataTypeParser extends DataTypeParser {
        @Override
        protected boolean isCustomDataType(DdlTokenStream tokens) throws ParsingException {
            for (String[] stmts : bigQueryDataTypeStrings) {
                if (tokens.matches(stmts)) {
                    return true;
                }
            }
            return super.isCustomDataType(tokens);
        }

        @Override
        protected DataType parseCustomType(DdlTokenStream tokens) throws ParsingException {
            if (tokens.matches(BigQueryDdlConstants.BigQueryDataTypes.DTYPE_STRUCT)
                || tokens.matches(BigQueryDdlConstants.BigQueryDataTypes.DTYPE_ARRAY)) {
                String typeName = tokens.consume();
                String typeParam = "";
                if (tokens.matches(L_ANGLE_PAREN)) {
                    typeParam = L_ANGLE_PAREN + parseContentBetweenAngleBrackets(tokens) + R_ANGLE_PAREN;
                }
                return new DataType(typeName + typeParam);
            }

            for (String type : BigQueryDdlConstants.BigQueryDataTypes.CUSTOM_DATATYPE_START_WORDS) {
                if (tokens.matches(type)) {
                    return new DataType(tokens.consume());
                }
            }

            return super.parseCustomType(tokens);
        }

        private String parseContentBetweenAngleBrackets(final DdlTokenStream tokens) throws ParsingException {
            tokens.consume(L_ANGLE_PAREN); // don't include first paren in expression

            int numLeft = 1;
            int numRight = 0;
            Position position = null;
            String previousToken = null;

            final StringBuilder text = new StringBuilder();

            while (tokens.hasNext()) {
                position = tokens.nextPosition();
                if (tokens.matches(L_ANGLE_PAREN)) {
                    ++numLeft;
                } else if (tokens.matches(R_ANGLE_PAREN)) {
                    if (numLeft == ++numRight) {
                        tokens.consume(R_ANGLE_PAREN); // don't include last paren in expression
                        break;
                    }
                }

                final String token = tokens.consume();
                if (previousToken != null && !previousToken.equals(L_ANGLE_PAREN)
                    && !token.equals(L_ANGLE_PAREN) && !token.equals(R_ANGLE_PAREN) &&  !token.equals(COMMA)) {
                    text.append(SPACE);
                }
                text.append(token);
                previousToken = token;
            }

            if ((numLeft != numRight)) {
                throw new ParsingException(position);
            }

            return text.toString();
        }
    }
}
