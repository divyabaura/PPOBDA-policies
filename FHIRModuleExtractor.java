package se.umea.mapgen;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class FHIRModuleExtractor {

    public static void main(String[] args) throws Exception {
        // Create OWL ontology manager
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        // Load the FHIR ontology from the TTL file
        File file = new File("fhir.ttl");
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);

        // Create OWL data factory to construct class IRIs
        OWLDataFactory dataFactory = manager.getOWLDataFactory();

        // Define the IRI base for FHIR concepts
        IRI fhirIri = IRI.create("http://hl7.org/fhir/");

        // Create a reasoner
        OWLReasoner reasoner = new StructuralReasonerFactory().createReasoner(ontology);

        // Define a set to hold the seed signature for module extraction
        Set<OWLEntity> seedSignature = new HashSet<>();

        // Set up a scanner to read user input
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter FHIR classes to include in the seed signature (enter 'done' when finished):");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) {
                break;
            }
            seedSignature.add(dataFactory.getOWLClass(fhirIri.resolve(input)));
        }

        System.out.println("Enter FHIR object properties to include in the seed signature (enter 'done' when finished):");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) {
                break;
            }
            seedSignature.add(dataFactory.getOWLObjectProperty(fhirIri.resolve(input)));
        }

        System.out.println("Enter FHIR data properties to include in the seed signature (enter 'done' when finished):");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) {
                break;
            }
            seedSignature.add(dataFactory.getOWLDataProperty(fhirIri.resolve(input)));
        }

        // Add subclasses of the key FHIR concepts to the seed signature
        Set<OWLEntity> subclasses = new HashSet<>();
        for (OWLEntity entity : seedSignature) {
            if (entity.isOWLClass()) {
                subclasses.addAll(reasoner.getSubClasses(entity.asOWLClass(), false).getFlattened());
            }
        }
        seedSignature.addAll(subclasses);

        // Extract the module using the Syntactic Locality Module Extractor
        SyntacticLocalityModuleExtractor moduleExtractor = new SyntacticLocalityModuleExtractor(manager, ontology, ModuleType.STAR);
        Set<OWLAxiom> module = moduleExtractor.extract(seedSignature);

        // Print out module size
        System.out.println("Extracted module size: " + module.size());

        // Create a new ontology for the extracted module
        IRI moduleIri = IRI.create("http://hl7.org/fhir");
        OWLOntology moduleOntology = manager.createOntology(module, moduleIri);

        // Save the extracted module
        try (OutputStream os = new FileOutputStream("fhir-module.owl")) {
            manager.saveOntology(moduleOntology, os);
        }

        // Output message indicating the file was saved successfully
        System.out.println("The extracted ontology module has been successfully saved to: fhir-module.owl");

        // Close the scanner
        scanner.close();
    }
}
