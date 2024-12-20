# PPOBDA Specifications and OMOP CDM to FHIR Mapping with Policies

## Overview

This project defines and implements **Ontology-Based Data Access (OBDA)** using the **OMOP Common Data Model (CDM)** and the **FHIR Ontology**. A key focus of this effort is integrating **denial policies** into the OBDA framework via **Policy JSON files** to govern data access rules and restrictions.

---

## Specifications

1. **FHIR Ontology Mapping**:  
   The **OMOP CDM** has been mapped to the **FHIR Ontology** to enable healthcare data modeled in OMOP CDM to be accessed and queried in a FHIR-compliant way. This mapping was achieved using resources such as [MIMIC-OMOP](https://github.com/MIT-LCP/mimic-omop).  
   - Relevant fragments of the FHIR Ontology are included (files: `fhir.ttl`).
   - Refer to [FHIROntopOMOP](https://github.com/fhircat/FHIROntopOMOP/tree/main) for the mappings used.

2. **Module Extraction**:  
   To streamline the mapping process, we applied module extraction techniques to selectively extract the necessary parts of the FHIR Ontology.  
   - Files: `FHIR Modular Extractor` and `FHIR-module.owl`.

3. **Metadata from MIMIC-III**:  
   Metadata from the **MIMIC-III dataset** was extracted to support the mapping and provide context for the OBDA framework.  
   - Access the MIMIC-III dataset via this [course](https://wiki.knox.cs.aau.dk/mimic-iii_extraction/MIMIC-III).  
   *Note: Access is subject to privacy regulations.*

4. **PPOBDA Specifications**:  
   A collection of **Policy JSON files** defines denial policies and access restrictions to ensure compliance with data access regulations.  
   - Example: Policy `p_1` denies access to combinations of gender and address. A sample modified mapping file with `p_1` embedded is included.

---

## Goal

The primary goal is to enable data experiments and retrieval using **Ontology-Based Data Access (OBDA)** systems that integrate **OMOP CDM** with **FHIR Ontology**, adhering to the defined denial policies.

---

## How to Use

1. Clone the repository:  
   [PPOBDA with Ontop](https://github.com/divyabaura/PPOBDA-with-Ontop).
   
2. Follow these steps:
   - Use the FHIR Ontology and mappings provided in the first specification point.
   - Integrate the MIMIC-III dataset and JSON policy files.
   - Run the implementation to produce the new mapping file (`.obda`) with embedded denial policies.

3. Example:  
   The repository includes an example of a modified mapping file where the first policy (`p_1`)—denying combinations of gender and address—is applied. Follow the same approach to apply other policies.

---

## Project Structure

- **FHIR Ontology Fragments**:  
  Necessary FHIR classes and relationships extracted via module extraction techniques, covering data types, identifiers, practitioners, locations, etc.

- **OMOP to FHIR Mappings**:  
  Files defining relationships between OMOP CDM and FHIR Ontology, essential for data integration.

- **PPOBDA Policy Files**:  
  JSON files defining denial policies to govern access and ensure compliance with regulations.

- **Metadata Extraction Scripts**:  
  Scripts to extract metadata from the MIMIC-III dataset, aiding the mapping and OBDA framework.

- **SPARQL Queries**:  
  Predefined queries aligned with the FHIR Ontology and mappings, enabling data retrieval within the OBDA framework while adhering to denial policies.

---

By following these steps and utilizing the provided resources, you can successfully implement OBDA with integrated denial policies and generate compliant policy embedded mappings.
