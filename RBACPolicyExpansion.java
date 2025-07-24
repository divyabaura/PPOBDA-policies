package se.umea.mapgen;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import com.google.common.io.MoreFiles;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import it.unibz.inf.ontop.iq.IQ;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.model.term.VariableOrGroundTerm;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import ppmappingcompiler.fol.*;
import ppmappingcompiler.parser.BCQParser;
import ppmappingcompiler.parser.DatalogBCQParser;
import ppmappingcompiler.parser.ParserException;
import ppmappingcompiler.policy.OntologyConjunctiveQuery;
import ppmappingcompiler.util.IOUtils;
import se.umea.mapgen.reformulation.OntopReformulationAPI;
import se.umea.mapgen.reformulation.OntopReformulationResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class PolicyExpansion {

    public static void main(String[] args) throws Throwable {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("========================================================");
        System.out.println(" Policy Expansion Tool with Role-based Processing");
        System.out.println("========================================================");
        System.out.println("Please provide the following inputs:");
        
        System.out.print("Enter path to the OBDA file (e.g., direct_mappings.obda): ");
        String obdaFile = scanner.nextLine().trim();
        
        System.out.print("Enter path to the OWL file (e.g., University.owl): ");
        String owlFile = scanner.nextLine().trim();
        
        System.out.print("Enter path to the JSON metadata file: ");
        String jsonFile = scanner.nextLine().trim();
        
        System.out.print("Enter path to policy configuration file (format: policy_file_path=role): ");
        String policyConfigFile = scanner.nextLine().trim();
        
        performReformulation(obdaFile, owlFile, jsonFile, policyConfigFile);
        scanner.close();
    }

    public static void performReformulation(String obdaFile, String owlFile, String jsonFile, String policyConfigFile) throws Throwable {
        // Load policy configuration
        List<PolicyConfig> policyConfigs = readPolicyConfig(policyConfigFile);
        if (policyConfigs.isEmpty()) {
            System.out.println("⚠️ No valid policy files found in configuration. Exiting.");
            return;
        }

        // Load the ontology
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        try (InputStream in = Files.newInputStream(Paths.get(owlFile))) {
            ontology = ontologyManager.loadOntologyFromOntologyDocument(in);
        }

        // Set up properties for Ontop
        Properties properties = new Properties();
        properties.setProperty("jdbc.url", "jdbc:postgresql://localhost:5432/dummy");
        properties.setProperty("ontop.existentialReasoning", "true");
        properties.setProperty("it.unibz.inf.ontop.iq.planner.QueryPlanner", "se.umea.mapgen.reformulation.OntopUnionLifterPlanner");

        // Initialize Ontop functionality
        CharSource mappingSource = MoreFiles.asCharSource(Paths.get(obdaFile), Charsets.UTF_8);
        CharSource ontologySource = MoreFiles.asCharSource(Paths.get(owlFile), Charsets.UTF_8);
        CharSource metadataSource = MoreFiles.asCharSource(Paths.get(jsonFile), Charsets.UTF_8);
        OntopReformulationAPI rewriter = new OntopReformulationAPI(properties, mappingSource, ontologySource, metadataSource);

        // Process each policy file
        for (PolicyConfig config : policyConfigs) {
            String policyFile = config.filePath;
            String role = config.role;
            
            System.out.println("\nProcessing policy for role: " + role);
            System.out.println("Policy file: " + policyFile);
            
            List<OntologyConjunctiveQuery> policyRules = loadPolicyFromJson(policyFile, ontology);
            List<OntologyConjunctiveQuery> expandedPolicyRules = new ArrayList<>();

            // Expand each policy rule
            for (OntologyConjunctiveQuery policyRule : policyRules) {
                String policyRuleAsSparqlQuery = policyRule.toSparql();
                System.out.println("  - Datalog rule: " + policyRule);
                
                OntopReformulationResult result = rewriter.reformulate(policyRuleAsSparqlQuery);
                IQ iq = result.getReformulatedQueryIQ();
                
                List<OntologyConjunctiveQuery> queries = Lists.newArrayList();
                generateOntologyConjunctiveQueries(iq.getTree(), ontology, queries);
                expandedPolicyRules.addAll(queries);
            }

            // Convert expanded rules to strings
            List<String> rulesAsStrings = expandedPolicyRules.stream()
                .map(OntologyConjunctiveQuery::toString)
                .collect(Collectors.toList());

            // Write to role-specific output file
            String outputFileName = "expanded_policy_" + sanitizeRoleName(role) + ".json";
            writeExpandedPolicyToJson(rulesAsStrings, outputFileName);
            
            System.out.println("✅ Successfully created expanded policy for role: " + role);
            System.out.println("   Output file: " + outputFileName);
            System.out.println("   Rules expanded: " + rulesAsStrings.size());
        }
    }

    private static List<PolicyConfig> readPolicyConfig(String configPath) throws IOException {
        List<PolicyConfig> configs = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(configPath));
        
        for (String line : lines) {
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }
            
            String[] parts = line.split("=");
            if (parts.length != 2) {
                System.out.println("Skipping invalid line: " + line);
                continue;
            }
            
            String filePath = parts[0].trim();
            String role = parts[1].trim();
            
            if (!Files.exists(Paths.get(filePath))) {
                System.out.println("Policy file not found: " + filePath);
                continue;
            }
            
            configs.add(new PolicyConfig(filePath, role));
        }
        return configs;
    }

    private static String sanitizeRoleName(String role) {
        // Replace spaces and special characters to make a valid filename
        return role.replaceAll("\\s+", "_")
                   .replaceAll("[^a-zA-Z0-9_]", "");
    }

    private static void writeExpandedPolicyToJson(List<String> rules, String outputFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        try {
            objectMapper.writer(prettyPrinter).writeValue(new File(outputFile), rules);
        } catch (IOException e) {
            System.err.println("Error writing expanded policy: " + e.getMessage());
        }
    }

    // Rest of the methods remain unchanged (generateOntologyConjunctiveQueries, loadPolicyFromJson, etc.)
    // [Include all other methods unchanged here]

    static class PolicyConfig {
        final String filePath;
        final String role;

        PolicyConfig(String filePath, String role) {
            this.filePath = filePath;
            this.role = role;
        }
    }
}
