package se.umea.mapgen;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class MappingCombinerRBAC {

    private static final Pattern WHERE_PATTERN = Pattern.compile(
            "\\bWHERE\\b", Pattern.CASE_INSENSITIVE
    );

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter path to mapping configuration file: ");
        String configPath = scanner.nextLine().trim();
        
        List<MappingFile> mappingFiles = readConfigurationFile(configPath);
        
        if (mappingFiles.isEmpty()) {
            System.out.println("No valid mapping files found in configuration.");
            return;
        }

        String outputPath = "RBACmappings.obda";
        String combinedMappings = combineMappingsWithRoles(mappingFiles);
        Files.write(Paths.get(outputPath), combinedMappings.getBytes(StandardCharsets.UTF_8));
        System.out.println("\nCombined mapping file created at: " + outputPath);
    }

    private static String combineMappingsWithRoles(List<MappingFile> mappingFiles) throws IOException {
        StringBuilder output = new StringBuilder();
        boolean firstFile = true;
        List<String> allMappings = new ArrayList<>();

        for (MappingFile mf : mappingFiles) {
            String content = Files.readString(Paths.get(mf.filePath));

            // Handle prefix declaration (only from first file)
            if (firstFile) {
                String prefixSection = extractPrefixSection(content);
                if (!prefixSection.isEmpty()) {
                    output.append(prefixSection).append("\n\n");
                }
                firstFile = false;
            }

            // Process mappings
            List<String> mappings = extractMappings(content);
            for (String mapping : mappings) {
                allMappings.add(processMapping(mapping, mf.role));
            }
        }

        output.append("[MappingDeclaration] @collection [[\n");
        output.append(String.join("\n\n", allMappings));
        output.append("\n]]");

        return output.toString();
    }

    private static String extractPrefixSection(String content) {
        int startIdx = content.indexOf("[PrefixDeclaration]");
        if (startIdx == -1) return "";

        int endIdx = content.indexOf("[MappingDeclaration]");
        if (endIdx == -1) endIdx = content.length();

        return content.substring(startIdx, endIdx).trim();
    }

    private static List<String> extractMappings(String content) {
        List<String> mappings = new ArrayList<>();
        int mappingStart = content.indexOf("mappingId");
        int collectionStart = content.indexOf("@collection [[") + "@collection [[".length();

        if (mappingStart == -1 || collectionStart == -1)
            return mappings;

        String mappingSection = content.substring(collectionStart, content.lastIndexOf("]]"));
        String[] mappingBlocks = mappingSection.split("(?=mappingId\\s+)");

        for (String block : mappingBlocks) {
            if (block.trim().isEmpty()) continue;
            mappings.add(block.trim());
        }
        return mappings;
    }

    private static String processMapping(String mapping, String role) {
        String[] lines = mapping.split("\\r?\\n");
        StringBuilder result = new StringBuilder();
        StringBuilder sourceBuilder = new StringBuilder();
        boolean inSource = false;

        for (String line : lines) {
            if (line.startsWith("source")) {
                inSource = true;
                sourceBuilder.append(line.substring(6).trim()); // Remove "source" keyword
            } else if (inSource) {
                sourceBuilder.append(" ").append(line.trim());
            } else {
                result.append(line).append("\n");
            }
        }

        if (sourceBuilder.length() > 0) {
            String originalSource = sourceBuilder.toString();
            String modifiedSource = modifySourceQuery(originalSource, role);
            result.append("source     ").append(modifiedSource);
        }

        return result.toString();
    }

    private static String modifySourceQuery(String source, String role) {
        String roleCondition = "ontop_contains_role('" + role + "')";
        String trimmedSource = source.trim();

        if (WHERE_PATTERN.matcher(trimmedSource).find()) {
            // Add condition to existing WHERE clause
            return trimmedSource + " AND " + roleCondition;
        } else {
            // Add new WHERE clause
            return trimmedSource + " WHERE " + roleCondition;
        }
    }

    static class MappingFile {
        final String filePath;
        final String role;

        MappingFile(String filePath, String role) {
            this.filePath = filePath;
            this.role = role;
        }
    }
}
