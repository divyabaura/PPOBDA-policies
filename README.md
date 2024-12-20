# PP-OBDA Specifications and OMOP CDM to FHIR Mapping with policies

## Overview

The objective of this project is to define and implement **Ontology-Based Data Access (OBDA)** using the **OMOP Common Data Model (CDM)** and the **FHIR Ontology**. As part of this effort, we focus on the integration of **denial policies** within the OBDA framework through **Policy JSON files**. These policies govern data access rules and restrictions.

### Steps to use:
1. **FHIR Ontology Mapping**: We have mapped the **OMOP CDM** to the **FHIR Ontology**, ensuring that the healthcare data modeled in OMOP CDM can be accessed and queried in a FHIR-compliant way. The mappings include relevant fragments of the FHIR Ontology.(Given in files)
   
2. **Module Extraction**: We applied module extraction techniques to selectively extract the necessary parts of the FHIR Ontology that are relevant to the mapping, thus streamlining the process and ensuring we focus only on the essential fragments.(Given in files)
   
3. **Metadata from MIMIC-III**: Extracted metadata from the **MIMIC-III dataset** to support the mapping process and provide additional context for the OBDA framework.(Can be obtained from completing a course https://wiki.knox.cs.aau.dk/mimic-iii_extraction/MIMIC-III)

4. **PP-OBDA Specifications**: The project includes a set of **Policy JSON files**, which define **denial policies** and **access restrictions** for data access. These policies ensure compliance with access rules and regulations. (Json file with policies and expected new mapping with embessing of polices are in files)

### Goal:
The ultimate goal of this work is to enable experiments and data retrieval through **Ontology-Based Data Access (OBDA)** systems that integrate OMOP CDM with FHIR and respect the provided **denial policies**.

---

## Project Structure

- **FHIR Ontology Fragments**: The necessary FHIR classes and relationships extracted using module extraction techniques. These include data types, identifiers, practitioners, locations, and more, necessary for the mapping to OMOP CDM.
  
- **OMOP to FHIR Mappings**: These files provide the relationships between the **OMOP CDM** and **FHIR Ontology**, which are essential for the integration of healthcare data.

- **PP-OBDA Policy Files**: The **denial policies** in JSON format that govern which data can be accessed by different users or systems. These policies ensure compliance with access rules and regulations.

- **Metadata Extraction Scripts**: These scripts extract the relevant metadata from the **MIMIC-III dataset** that is used in the mapping and OBDA system.

- **SPARQL Queries**: Predefined SPARQL queries that are aligned with the **FHIR Ontology** and the mappings. These queries allow users to retrieve data based on the OBDA framework and the denial policies.

---

