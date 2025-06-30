# PPVKG Specifications and OMOP CDM to FHIR Mapping with role based policies

## Overview

This project defines and implements **Role Based Policy Protected Virtual Knowledge Graph (PPVKG)** using the **OMOP Common Data Model (CDM)** and the **FHIR Ontology**. A key focus is integrating **role based denial policies** into the OBDA framework through **policy JSON files**, ensuring data access is governed by predefined rules and restrictions.

---

## RBAC-PPVKG Specifications

### 1. **FHIR Ontology and Mappings**  
The **OMOP CDM** has been mapped to the **FHIR Ontology**, enabling OMOP CDM-modeled healthcare data to be accessed and queried in a FHIR-compliant manner. The mapping was facilitated using resources such as [MIMIC-OMOP](https://github.com/MIT-LCP/mimic-omop).  
- Relevant fragments of the FHIR Ontology are included in `fhir.ttl`.
- Refer to [FHIROntopOMOP](https://github.com/fhircat/FHIROntopOMOP/tree/main) for mappings (`fhir.obda`).

### 2. **Module Extraction**  
To streamline the mapping process, module extraction techniques were applied to selectively extract necessary parts of the FHIR Ontology.  
- Relevant files: `FHIRModularExtractor.java` and `fhir-module.owl`.

### 3. **Metadata from MIMIC-III**  
Metadata from the **MIMIC-III dataset** was extracted to support the mapping and provide context for the OBDA framework.  
- The MIMIC-III dataset can be accessed via this [course](https://wiki.knox.cs.aau.dk/mimic-iii_extraction/MIMIC-III).  
  *Note: Access is subject to privacy regulations.*

### 4. **Policies and Access Restrictions**  
A collection of **policy JSON files** (`PolicyFile.json`) defines denial policies to enforce access restrictions and ensure compliance with data protection regulations. These policies are explained in PolicyExplanation.txt.

- Example: One of the policies (marked as p_1 in PolicyExplanation.txt) restricts access to combinations of gender and address to prevent potential re-identification risks.
### 5. **Role-Based Access Policies (RBAC Integration)**  
In addition to general denial policies, **role-based access control (RBAC)** has been integrated to enable fine-grained filtering based on user roles.

- Java utilities (MappingCombinerRBAC.java and SparqlQueryTimer.java) are used to implement RBAC-aware mappings and query execution.
- **MappingCombinerRBAC.java** adds role-based conditions (e.g., WHERE ontop_contains_role('nurse')) to each mapping, ensuring that only users with a specific role can access the relevant data sources.
- **SparqlQueryTimer.java** allows users to send SPARQL queries along with their role and username via HTTP headers (x-user, x-roles), enabling the SPARQL endpoint to return filtered results based on role-based logic.

#### 🔍 Example Role Policy:
If a mapping file is intended for "doctor" access, the SQL source is automatically modified to:
SELECT * FROM patient_records WHERE ontop_contains_role('doctor')

---

## Goal

The primary objective is to enable **Role-Based Access Control for Virtual Knowledge Graph (VKG)** for **OMOP CDM** integrated with **FHIR Ontology**, while ensuring compliance with defined PPVKG denial policies.

--

## How to Use

### 1. Clone the Repository  
Clone the repository from GitHub:
[PPOBDA with Ontop](https://github.com/divyabaura/PPOBDA-with-Ontop).

### 2. Follow These Steps  
- Use the FHIR Ontology and mappings provided in the specifications.
- Integrate the MIMIC-III dataset and JSON policy files.
- Run the implementation of PPVKG with (MappingCombinerRBAC.java and SparqlQueryTimer.java) to generate a new mapping file (`.obda`) with role based embedded denial policies.

### 3. Evaluate with SPARQL Queries  
- Predefined SPARQL queries from [this resource](https://github.com/fhircat/FHIROntopOMOP/blob/main/evaluation/jbi-2022-queries.md) are available in the file `SparqlQueries.md`.

### 4. Example  
The repository includes a sample modified mapping file (`RBACMapping.obda`) where a role based policy for a 'pharmacist' restricting the combination of gender and address is applied. You can follow a similar approach to apply additional role based policies. 


---

## Project Structure

- **FHIR Ontology Fragments**  
  Extracted FHIR classes and relationships covering data types, identifiers, practitioners, locations, etc.

- **OMOP to FHIR Mappings**  
  Defines relationships between OMOP CDM and FHIR Ontology, enabling data integration.

- **PPVKG role based policy files**  
  JSON files specifying denial policies to regulate access and ensure compliance.

- **Metadata Extraction Scripts**  
  Scripts for extracting metadata from the MIMIC-III dataset to support OBDA.

- **SPARQL Queries**  
  Predefined queries aligned with FHIR Ontology and mappings, ensuring data retrieval follows defined denial policies.

---

By following these steps and utilizing the provided resources, you can successfully implement OBDA with integrated denial policies, ensuring secure and compliant data access.

