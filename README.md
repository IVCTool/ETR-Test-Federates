# ETR-Test-Federates

Repository for test federates that support testing of compliance with aspects of the NATO FOM for Distributed Synthetic Training (aka. NETN FOM).

## Entity Tasking and Reporting (NETN-FOM) related tests

* Query and Responding to identify which ETR tasks are suported
 * QuerySupportedCapabilities
 * CapabilitiesSupported

<!--
title ETR

participant "SendTask" as A
participant "RTI" as F
participant "ReceiveTask" as B

par
A->F:Connect
A->F:CreateFederationExecution
A->F:JoinFederationExecution
A->F:PublishInteractionClass("QuerySupportedCapabilities")
A->F:SubscribeInteractionClass("CapabilitiesSupported")
A->F:SubscribeObjectClassAttributes("...")
thread 
B->F:Connect
B->F:CreateFederationExecution
B->F:JoinFederationExecution
B->F:PublishInteractionClass("CapabilitiesSupported")
B->F:SubscribeInteractionClass("QuerySupportedCapabilities")
B->F:PublishObjectClassAttributes("NETN_Aggregate", {"UUID"})
B->F:RegisterObjectInstance("NETN_Aggregate")
end


group Wait for NETN_Aggregate with correct UUID

F->A:DiscoverObjectInstance("NETN_Aggregate")
A->F:RequestAttributeValueUpdate("NETN_Aggregate", {"UUID"})
F->B:ProvideAttributeValueUpdate("NETN_Aggregate", {"UUID"})
B->F:UpdateAttributeValues("NETN_Aggregate", {"UUID"})


F->A:ReflectAttributeValues("NETN_Aggregate", {"UUID"})
end

group Wait for Response to Query
A->F:SendInteraction("QuerySupportedCapabilities", {"UUID"})

F->B:ReceiveInteraction("QuerySupportedCapabilities", {"UUID"})
B->F:SendInteraction("CapabilitiesSupported", {"UUID"})
F->A:ReceiveInteraction("CapabilitiesSupported", {"UUID"})

end
B->F:ResignFederationExecution


A->F:ResignFederationExecution
-->
