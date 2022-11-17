# ETR-Test-Federates

Repository for test federates that support testing of compliance with aspects of the NATO FOM for Distributed Synthetic Training (aka. NETN FOM).
All federates will connect to an HLA RTI, e.g. Pitch pRTI or MÄK RTI, using the standard HLA IEEE 1516-2010 Java API. The RTI is not provided, contact vendors for commercial, evaluation or personal education/test licences. The RTI java libraries must be linked to compile and run the test federates.

## QuerySupportedCapabilities
Query and Responding to identify which ETR tasks are supported.
Verifies that your federate can request or respond to the NETN-ETR QuerySupportedCapabilities interaction.
 . A specific entity with UUID is identified and the test federates configured based on this UUID.
 . In the same federation, start your federate and one of the test federates to emulate sending of QuerySupportedCapabilities or responding with CapabilitiesSupported.
 . Wait for discovery of the entity with UUID then send QuerySupportedCapabilities. Wait for CapabilitiesSupported response

![QuerySupportedCapabilities](./QuerySupportedCapabilities.svg)
