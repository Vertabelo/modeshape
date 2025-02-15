package org.modeshape.sequencer.ddl.dialect.oracle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class for the generation of constants representing all possible SQL combinations that make up a view - their number
 * is so large that it is difficult to do it manually.
 *
 * Used it the generation of constants in {@link OracleDdlConstants.OracleStatementStartPhrases#CREATE_PHRASES}
 * and {@link OracleDdlConstants.OracleStatementStartPhrases#CREATE_VIEW_PHRASES}.
 *
 * This method is run manually by “main” and its result should be pasted into the code in the above places.
 *
 * See the documentation for details:
 * https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/CREATE-VIEW.html#GUID-61D2D2B4-DACC-4C7C-89EB-7E50D9594D30
 */
public class OracleCreateViewStatementConstantsHelper {

    public static void main(String[] args) {
        getAllCreateViewPermutations()
                .forEach(OracleCreateViewStatementConstantsHelper::printConstantDeclaration);
    }

    static private void printConstantDeclaration(CreateViewPart createViewPart) {
        if (createViewPart.part.isEmpty()) {
            System.out.println("\t\t\t\t{CREATE, VIEW},");
        } else {
            System.out.print("\t\t\t\t{CREATE, \"");
            System.out.print(printTokens(createViewPart));
            System.out.println("\", VIEW},");
        }
    }

    private static String printTokens(CreateViewPart createViewPart) {
        return createViewPart.part
                .stream()
                .map(String::toUpperCase)
                .collect(Collectors.joining("\", \""));
    }

    static List<CreateViewPart> getAllCreateViewPermutations() {

        List<CreateViewPart> orReplaceAlternatives = Arrays.asList(CreateViewPart.of(),
                                                                   CreateViewPart.of("OR", "REPLACE"));
        List<CreateViewPart> noForceAlternatives = Arrays.asList(CreateViewPart.of(),
                                                                 CreateViewPart.of("NO", "FORCE"),
                                                                 CreateViewPart.of("FORCE"));
        List<CreateViewPart> editioningAlternatives = Arrays.asList(CreateViewPart.of(),
                                                                    CreateViewPart.of("EDITIONING"),
                                                                    CreateViewPart.of("EDITIONABLE", "EDITIONING"),
                                                                    CreateViewPart.of("EDITIONABLE"),
                                                                    CreateViewPart.of("NONEDITIONABLE"));

        List<CreateViewPart> allPermutations = new ArrayList<>();
        generatePermutations(Arrays.asList(orReplaceAlternatives, noForceAlternatives, editioningAlternatives),
                             allPermutations, 0, CreateViewPart.of());

        return allPermutations;
    }

    /**
     * Algorithm based on https://stackoverflow.com/a/17193002/3049486
     */
    static void generatePermutations(List<List<CreateViewPart>> lists, List<CreateViewPart> result, int depth, CreateViewPart current) {
        if (depth == lists.size()) {
            result.add(current);
            return;
        }

        for (int i = 0; i < lists.get(depth).size(); i++) {
            generatePermutations(lists, result, depth + 1, current.merge(lists.get(depth).get(i)));
        }
    }

    public static class CreateViewPart {

        private final List<String> part;

        private CreateViewPart(String... part) {
            this.part = Arrays.asList(part);
        }
        private CreateViewPart(List<String> part) {
            this.part = part;
        }

        static CreateViewPart of(String... part) {
            return new CreateViewPart(part);
        }

        CreateViewPart merge(CreateViewPart another) {
            ArrayList<String> clone = new ArrayList<>(part);
            clone.addAll(another.part);
            return new CreateViewPart(clone);
        }
    }
}
