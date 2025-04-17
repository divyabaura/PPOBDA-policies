# SPARQL Query Collection

Here are other set of SPARQL queries used for querying FHIR Ontology.

---

## Query 1: Count of Opioid Prescriptions by Location

```sparql
PREFIX fhir: <http://hl7.org/fhir/>

SELECT ?location (COUNT(?patient) AS ?opioid_rxs) WHERE {
  ?med a fhir:MedicationStatement ;
       fhir:MedicationStatement.medicationCodeableConcept [
         fhir:CodeableConcept.coding [
           fhir:Coding.code [ fhir:value "11289" ]  # RxNorm Oxycodone
         ]
       ] ;
       fhir:MedicationStatement.context [ fhir:link ?enc ] ;
       fhir:MedicationStatement.subject [ fhir:link ?patient ] .
  
  ?enc a fhir:Encounter ;
       fhir:Encounter.location [ fhir:link ?location ] .
  
  ?location a fhir:Location ;
            fhir:Location.name [ fhir:value ?loc_name ] .
}
GROUP BY ?location
ORDER BY DESC(?opioid_rxs)

##Query 2: Practitioner Specialties

```sparql
PREFIX : <http://hl7.org/fhir/>

SELECT ?role ?specialtyName
WHERE {
  ?role a :PractitionerRole ;
        :PractitionerRole.specialty [
          :CodeableConcept.coding [ :Coding.display ?specialtyName ]
        ] .
}

##Query 3: Top 10 Conditions by Patient Count

```sparql
PREFIX fhir: <http://hl7.org/fhir/>
PREFIX cs: <http://hl7.org/orim/codesystem/>

SELECT ?condition (COUNT(?patient) AS ?cases)
WHERE {
  ?cond a fhir:Condition ;
        fhir:Condition.code [ 
          fhir:CodeableConcept.coding [ 
            fhir:Coding.display ?condition 
          ] 
        ] ;
        fhir:Condition.subject ?patient .
}
GROUP BY ?condition
ORDER BY DESC(?cases)
LIMIT 10

##Query 4: Monthly Count of Prescriptions

```sparql
PREFIX fhir: <http://hl7.org/fhir/>

SELECT ?yearMonth (COUNT(*) AS ?prescriptions)
WHERE {
  ?med a fhir:MedicationStatement ;
       fhir:MedicationStatement.effectiveDateTime [ 
         fhir:value ?date 
       ] .
  
  BIND(SUBSTR(STR(?date), 1, 7) AS ?yearMonth)
}
GROUP BY ?yearMonth
ORDER BY ?yearMonth

