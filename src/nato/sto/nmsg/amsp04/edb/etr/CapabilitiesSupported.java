package nato.sto.nmsg.amsp04.edb.etr;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.encoding.*;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

class CapabilitiesSupported extends NullFederateAmbassador implements Runnable
{
   RTIambassador _rtiAmbassador;
   String _federateType = "CapabilitiesSupported";
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
      System.out.println("This federate responds to QuerySupportedCapabilities with a CapabilitiesSupported reply.");
      System.out.println("1.- Connect, Join, Publish, Subscribe.");
      System.out.println("2.- Register NETN_Aggregate object.");
      System.out.println("3.- Update required attributes including UUID.");
      System.out.println("4.- Wait for QuerySupportedCapabilities object corresponding UUID.");
      System.out.println("5.- Reply with CapabilitiesSupported [MagicMove]");
     // System.out.println("6.- Wait for ETR task");
     // System.out.println("7.- Respond to task request");
     // System.out.println("8.- If task is MagicMove then update spatial attribute and send task complete");
     // System.out.println("9.- Repeat from 6");

      if (args.length != 4) {
         System.out.println("Arguments required: rtiHost rtiPort FederationName UUID");
      } else {
         new CapabilitiesSupported(args).run();
      }
   }

   CapabilitiesSupported(String[] args)
   {
      _rtiHost = args[0];
      _rtiPort = args[1];
      _federationName = args[2];
      _uuid = args[3];
      _localSettingsDesignator = "crcHost=" + _rtiHost + "\n" + "crcPort=" + _rtiPort; // Pitch pRTI

      System.out.println("\n----- Application Provided Arguments -----");
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

         URL _rprSwitches = new File("./lib/fom/RPR-Switches_v2.0.xml").toURI().toURL();
         URL _rprBase = new File("./lib/fom/RPR-Base_v2.0.xml").toURI().toURL();
         URL _netnETR = new File("./lib/fom/NETN-ETR.xml").toURI().toURL();
         URL _rprAgg = new File("./lib/fom/RPR-Aggregate_v2.0.xml").toURI().toURL();
         URL _netnMRM = new File("./lib/fom/NETN-MRM.xml").toURI().toURL();
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

         EncoderFactory _encoderFactory = rtiFactory.getEncoderFactory();
         HLAoctet spatialDiscriminant = _encoderFactory.createHLAoctet((byte)1); // create discriminant
         HLAvariantRecord<HLAoctet> spatial = _encoderFactory.createHLAvariantRecord(spatialDiscriminant);
         HLAfixedRecord staticSpatial = _encoderFactory.createHLAfixedRecord();
         HLAfixedRecord location = _encoderFactory.createHLAfixedRecord(); // X, Y, Z
         location.add(_encoderFactory.createHLAfloat64BE(1));
         location.add(_encoderFactory.createHLAfloat64BE(2));
         location.add(_encoderFactory.createHLAfloat64BE(3));
         HLAboolean isFrozen = _encoderFactory.createHLAboolean(false);
         HLAfixedRecord orientation = _encoderFactory.createHLAfixedRecord(); // Psi, Theta, Phi
         orientation.add(_encoderFactory.createHLAfloat32BE(4));
         orientation.add(_encoderFactory.createHLAfloat32BE(5));
         orientation.add(_encoderFactory.createHLAfloat32BE(6));
         /*
         HLAvariantRecord otherDRparameters = _encoderFactory.createHLAvariantRecord(_encoderFactory.createHLAbyte((byte)0)); // None
         */
         staticSpatial.add(location);
         staticSpatial.add(isFrozen);
         staticSpatial.add(orientation);
         //staticSpatial.add(otherDRparameters);

         spatial.setVariant(spatialDiscriminant, staticSpatial);

         AttributeHandleValueMap _ahvp = _rtiAmbassador.getAttributeHandleValueMapFactory().create(4);
         _ahvp.put(_UniqueId, _encoderFactory.createHLAunicodeString(_uuid).toByteArray());
         _ahvp.put(_Callsign, _encoderFactory.createHLAunicodeString(_uuid).toByteArray());
         _ahvp.put(_Status, _encoderFactory.createHLAoctet((byte)1).toByteArray());
         _ahvp.put(_Spatial, spatial.toByteArray());



         System.out.print("updateAttributeValues("+_theObject+ ")");
         _rtiAmbassador.updateAttributeValues(_theObject, _ahvp, new byte[]{});
         System.out.println(" -> OK");

         System.out.println("\n----- Ready to respond to ETR requests -----");

         int i = 0;
         while (true) {
            Thread.sleep(500);
            switch (i % 4) {
             case 0: System.out.print("|"); break;
             case 1: System.out.print("/"); break;
             case 2: System.out.print("—"); break;
             case 3: System.out.print("\\"); break;
            }
            System.out.print("\r");
            i++;
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
