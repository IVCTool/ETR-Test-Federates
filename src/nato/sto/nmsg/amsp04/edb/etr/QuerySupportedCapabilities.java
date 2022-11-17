package nato.sto.nmsg.amsp04.edb.etr;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.encoding.*;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

class QuerySupportedCapabilities extends NullFederateAmbassador implements Runnable
{
   RTIambassador _rtiAmbassador;
   String _federateType = "ReceiveTask";
   String _rtiHost;
   String _rtiPort;
   String _federationName;
   String _localSettingsDesignator;
   String _uuid;

   ObjectClassHandle _NETN_Aggregate;
   AttributeHandle _EntityType; //Required
   AttributeHandle _EntityIdentifier; //Required
   AttributeHandle _Spatial; //Required
   AttributeHandle _UniqueId; //Required
   AttributeHandle _Status; //Required
   AttributeHandle _Callsign; //Required

   InteractionClassHandle _ETR_SimCon;
   ParameterHandle _TaskId; // Required
   ParameterHandle _Taskee; // Required
   InteractionClassHandle _QuerySupportedCapabilities;
   InteractionClassHandle _CapabilitiesSupported;
   ParameterHandle _CapabilityNames; // Required

   public static void main(String[] args)
   {
      if (args.length != 4) {
         System.out.println("Arguments required: rtiHost rtiPort FederationName UUID");
      } else {
         new QuerySupportedCapabilities(args).run();
      }
   }

   QuerySupportedCapabilities(String[] args)
   {
      _rtiHost = args[0];
      _rtiPort = args[1];
      _federationName = args[2];
      _uuid = args[3];
      _localSettingsDesignator = "crcHost=" + _rtiHost + "\n" + "crcPort=" + _rtiPort; // Pitch pRTI

      System.out.println("----- Application Arguments -----");
      System.out.println("rtiHost = " + _rtiHost);
      System.out.println("rtiPort = " + _rtiPort);
      System.out.println("FederationName = " + _federationName);
      System.out.println("uuid = " + _uuid);
   }
   
   public void run()
   {
      try {
         System.out.println("\n----- Connect, Create, Join, Publish and Subscribe -----");
         RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory();

         _rtiAmbassador = rtiFactory.getRtiAmbassador();

         System.out.print("connect(" + _localSettingsDesignator + ")");
         _rtiAmbassador.connect(this, CallbackModel.HLA_IMMEDIATE, _localSettingsDesignator);
         System.out.println(" -> OK");

         URL _rprSwitches = new File("RPR-Switches_v2.0.xml").toURI().toURL();
         URL _rprBase = new File("RPR-Base_v2.0.xml").toURI().toURL();
         URL _netnETR = new File("NETN-ETR.xml").toURI().toURL();
         URL _rprAgg = new File("RPR-Aggregate_v2.0.xml").toURI().toURL();
         URL _netnMRM = new File("NETN-MRM.xml").toURI().toURL();
         URL[] urls = new URL[]{_rprBase, _netnETR, _netnMRM, _rprAgg};

         try {
            System.out.print("createFederationExecution(" + _federationName + ")");
            _rtiAmbassador.createFederationExecution(_federationName, _rprSwitches);
            System.out.println(" -> OK");
         } catch (FederationExecutionAlreadyExists ignored) {
            System.out.println(" -> FederationExecutionAlreadyExists");
         }
         System.out.print("joinFederationExecution(" + _federateType + ", " + _federationName + ")");
         FederateHandle federateHandle = _rtiAmbassador.joinFederationExecution(_federateType, _federationName, urls);
         System.out.println(" -> Joined as " + federateHandle);

         System.out.print("publishObjectClassAttributes(BaseEntity.AggregateEntity.NETN_Aggregate, {EntityType, EntityIdentifier, Spatial, UniqueId, Status, Callsign})");
         _NETN_Aggregate = _rtiAmbassador.getObjectClassHandle("BaseEntity.AggregateEntity.NETN_Aggregate");
         _EntityType =_rtiAmbassador.getAttributeHandle(_NETN_Aggregate, "EntityType");
         _EntityIdentifier =_rtiAmbassador.getAttributeHandle(_NETN_Aggregate, "EntityIdentifier");
         _Spatial =_rtiAmbassador.getAttributeHandle(_NETN_Aggregate, "Spatial");
         _UniqueId =_rtiAmbassador.getAttributeHandle(_NETN_Aggregate, "UniqueId");
         _Status =_rtiAmbassador.getAttributeHandle(_NETN_Aggregate, "Status");
         _Callsign =_rtiAmbassador.getAttributeHandle(_NETN_Aggregate, "Callsign");
         AttributeHandleSet _attributes = _rtiAmbassador.getAttributeHandleSetFactory().create();
         _attributes.add(_EntityType);
         _attributes.add(_EntityIdentifier);
         _attributes.add(_Spatial);
         _attributes.add(_UniqueId);
         _attributes.add(_Status);
         _attributes.add(_Callsign);
         _rtiAmbassador.publishObjectClassAttributes(_NETN_Aggregate, _attributes);
         System.out.println(" -> OK");

         System.out.print("publishInteractionClass(ETR_Root.ETR_SimCon.CapabilitiesSupported)");
         _ETR_SimCon = _rtiAmbassador.getInteractionClassHandle(
                 "ETR_Root.ETR_SimCon");
         _TaskId = _rtiAmbassador.getParameterHandle(
                 _ETR_SimCon, "TaskId");
         _Taskee = _rtiAmbassador.getParameterHandle(
                 _ETR_SimCon, "Taskee");
         _CapabilitiesSupported = _rtiAmbassador.getInteractionClassHandle(
                 "ETR_Root.ETR_SimCon.CapabilitiesSupported");
         _rtiAmbassador.publishInteractionClass(_CapabilitiesSupported);
         System.out.println(" -> OK");

         System.out.print("subscribeInteractionClass(ETR_Root.ETR_SimCon.QuerySupportedCapabilities)");
         _QuerySupportedCapabilities = _rtiAmbassador.getInteractionClassHandle(
                 "ETR_Root.ETR_SimCon.QuerySupportedCapabilities");
         _CapabilityNames = _rtiAmbassador.getParameterHandle(
                 _CapabilitiesSupported, "CapabilityNames");
         _rtiAmbassador.subscribeInteractionClass(_QuerySupportedCapabilities);
         System.out.println(" -> OK");

         System.out.println("\n----- Register NETN_Aggregate -----");

         System.out.print("registerObjectInstance(BaseEntity.AggregateEntity.NETN_Aggregate)");
         ObjectInstanceHandle _theObject = _rtiAmbassador.registerObjectInstance(_NETN_Aggregate);
         System.out.println(" -> " + _theObject);

         System.out.println("\n----- Update NETN_Aggregate attributes -----");
         EncoderFactory encoder = rtiFactory.getEncoderFactory();
         AttributeHandleValueMap _ahvp = _rtiAmbassador.getAttributeHandleValueMapFactory().create(3);
         _ahvp.put(_UniqueId, encoder.createHLAunicodeString(_uuid).toByteArray());
         _ahvp.put(_Callsign, encoder.createHLAunicodeString(_uuid).toByteArray());
         _ahvp.put(_Status, encoder.createHLAoctet((byte)1).toByteArray());
         System.out.print("updateAttributeValues("+_theObject+ ")");
         _rtiAmbassador.updateAttributeValues(_theObject, _ahvp, new byte[]{});
         System.out.println(" -> OK");

         System.out.println("\n----- Ready to respond to ETR requests -----");
         // Loop until exit
         while (true) {
            Thread.sleep(1000);
            System.out.print(".");
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   public void receiveInteraction(
      InteractionClassHandle interactionClass, 
      ParameterHandleValueMap parameterValues, 
      byte[] userSuppliedTag, 
      OrderType orderType, 
      TransportationTypeHandle theTransport,
      SupplementalReceiveInfo receiveInfo)
   {
      if (interactionClass.equals(_QuerySupportedCapabilities)) {
         String taskee = "";
         String taskId = "";

         for (Iterator<ParameterHandle> i = parameterValues.keySet().iterator(); i.hasNext(); ) {
            ParameterHandle parameterHandle = (ParameterHandle)i.next();
            if (parameterHandle.equals(_Taskee)) {
               taskee = new String(parameterValues.get(parameterHandle));
            } else if (parameterHandle.equals(_TaskId)) {
               taskId = new String(parameterValues.get(parameterHandle));
            }
         }
         System.out.println();
         System.out.println("receiveInteraction: CapabilitiesSupported(TaskId:" + taskId + ", Taskee:" + taskee + ")");
   

      }
   }
}
