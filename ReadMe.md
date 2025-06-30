## Role-Based Query Execution and Mapping Generation Utilities

In addition to the policy-driven OBDA mappings and FHIR-OMOP integration described above, this project also includes Java-based utilities to support **role-based query execution** and **RBAC (Role-Based Access Control) mapping file generation**.

These tools enhance the security and privacy aspects of OBDA by ensuring that both data access and mappings themselves respect role-based constraints.

---

### 1. `SparqlQueryTimer.java` — Role-Based SPARQL Query Executor

A command-line tool to execute SPARQL queries against a SPARQL endpoint with role-based access headers.

- Prompts user to input a SPARQL query, username (`x-user`), and role (`x-roles`) at runtime.
- Sends the query as a POST request to a configured endpoint (e.g., Ontop).
- Measures and prints the execution time over multiple iterations.
- Parses and prints results from the JSON SPARQL response.

Useful for testing how different user roles affect query execution and results.

---

### 2. `MappingCombinerRBAC.java` — OBDA Mapping Merger with Role Constraints

A utility to merge multiple `.obda` mapping files, each tagged with a specific role, into a single RBAC-aware mapping file.

- Accepts any number of mapping files via user input.
- Automatically injects role checks like `WHERE ontop_contains_role('role')` into the SQL `source` section.
- Keeps prefixes from the first file intact.
- Outputs a combined file: `RBACmappings.obda`.

Enables seamless integration of RBAC into OBDA mapping definitions for use with Ontop or similar engines.

---

These utilities complement the core PPOBDA framework by enabling:

- Role-based **query execution** via HTTP headers.
- Role-aware **mapping generation** at the SQL level.

This supports a more secure and policy-compliant OBDA pipeline.
