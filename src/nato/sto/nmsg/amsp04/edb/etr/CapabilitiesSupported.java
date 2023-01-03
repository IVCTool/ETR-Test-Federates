package nato.sto.nmsg.amsp04.edb.etr;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.encoding.*;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

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
   InteractionClassHandle _MagicMove;
   ParameterHandle _Location; // Required magic move
   ParameterHandle _Heading; //Required magic move

   Vector<Float> locationCoords = new Vector<Float>(); //X Y Z
   ObjectInstanceHandle _theObject;

   EncoderFactory _encoderFactory;
   //Factories
   DataElementFactory<HLAbyte> _byteEncoderFactory = new DataElementFactory<HLAbyte>() {
      public HLAbyte createElement(int index){
         return _encoderFactory.createHLAbyte();
      }
   };
   DataElementFactory<HLAASCIIstring> _stringEncoderFactory = new DataElementFactory<HLAASCIIstring>() {
      public HLAASCIIstring createElement(int index){
         return _encoderFactory.createHLAASCIIstring();
      }
   };

   public static void main(String[] args)
   {
      System.out.println("This federate responds to QuerySupportedCapabilities with a CapabilitiesSupported reply.");
      System.out.println("1.- Connect, Join, Publish, Subscribe.");
      System.out.println("2.- Register NETN_Aggregate object.");
      System.out.println("3.- Update required attributes including UUID.");
      System.out.println("4.- Wait for QuerySupportedCapabilities object corresponding UUID.");
      System.out.println("5.- Reply with CapabilitiesSupported [QuerySupportedCapabilities and MagicMove]");
      System.out.println("6.- Wait for ETR task");
      System.out.println("7.- Respond to task request");
      System.out.println("8.- If task is MagicMove then update spatial attribute and send task complete");
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
         _encoderFactory = rtiFactory.getEncoderFactory();

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

         System.out.print("subscribeInteractionClass(ETR_Root.ETR_SimCon.MagicMove)");
         _MagicMove = _rtiAmbassador.getInteractionClassHandle(
            "ETR_Root.ETR_SimCon.MagicMove");
         _Location = _rtiAmbassador.getParameterHandle(_MagicMove, "Location");
         _Heading = _rtiAmbassador.getParameterHandle(_MagicMove, "Heading");
         _rtiAmbassador.subscribeInteractionClass(_MagicMove);
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
         _theObject = _rtiAmbassador.registerObjectInstance(_NETN_Aggregate);
         System.out.println(" -> " + _theObject); //TODO: Clean up here

         System.out.println("\n----- Update NETN_Aggregate attributes -----");

         locationCoords.add((float)1.0);
         locationCoords.add((float)2.0);
         locationCoords.add((float)3.0);
         
         HLAoctet spatialDiscriminant = _encoderFactory.createHLAoctet((byte)1); // create discriminant
         HLAvariantRecord<HLAoctet> spatial = _encoderFactory.createHLAvariantRecord(spatialDiscriminant);
         HLAfixedRecord staticSpatial = _encoderFactory.createHLAfixedRecord();
         HLAfixedRecord location = _encoderFactory.createHLAfixedRecord(); // X, Y, Z
         location.add(_encoderFactory.createHLAfloat64BE(locationCoords.get(0))); //1
         location.add(_encoderFactory.createHLAfloat64BE(locationCoords.get(1))); //2
         location.add(_encoderFactory.createHLAfloat64BE(locationCoords.get(2))); //3
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
         _ahvp.put(_UniqueId, _encoderFactory.createHLAunicodeString(_uuid).toByteArray()); //TODO: Correct this UUID is not a string.
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
             case 2: System.out.print("-"); break;
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
         System.out.println("receiveInteraction: QuerySupportedCapabilities(TaskId:" + taskId + ", Taskee:" + taskee + ")");
   
         sendCapabilitiesInteraction(taskId, taskee);

      }else if(interactionClass.equals(_MagicMove)){
         //String taskee = "";
         //String taskId = "";

         HLAfixedArray<HLAbyte> taskee = _encoderFactory.createHLAfixedArray(_byteEncoderFactory, 16);
         HLAfixedArray<HLAbyte> taskId = _encoderFactory.createHLAfixedArray(_byteEncoderFactory, 16);
         
         HLAfixedRecord location = _encoderFactory.createHLAfixedRecord(); // X, Y, Z
         HLAfloat32BE X = _encoderFactory.createHLAfloat32BE();
         HLAfloat32BE Y = _encoderFactory.createHLAfloat32BE();
         HLAfloat32BE Z = _encoderFactory.createHLAfloat32BE();
         
         location.add(X);
         location.add(Y);
         location.add(Z);

         HLAfloat32BE heading = _encoderFactory.createHLAfloat32BE();
         try {
            for (Iterator<ParameterHandle> i = parameterValues.keySet().iterator(); i.hasNext(); ) {
               ParameterHandle parameterHandle = (ParameterHandle)i.next();
               if (parameterHandle.equals(_Taskee)) {
                  taskee.decode(parameterValues.get(parameterHandle));
                  //new String(parameterValues.get(parameterHandle));
               } else if (parameterHandle.equals(_TaskId)) {
                  taskId.decode(parameterValues.get(parameterHandle));
               } else if (parameterHandle.equals(_Location)){
                  location.decode(parameterValues.get(parameterHandle));
               } else if (parameterHandle.equals(_Heading)){
                  heading.decode(parameterValues.get(parameterHandle));
               } else {
                  System.out.println("Received non-required parameter, this will be ignored");
               } 
            }
      } catch (DecoderException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
         
         UUID taskIdUUID = convertBytesToUUID(taskId.toByteArray());
         UUID taskeeUUID = convertBytesToUUID(taskee.toByteArray());

         System.out.println();
         System.out.println("receiveInteraction: MagicMove(TaskId:" + taskIdUUID + ", Taskee:"+taskeeUUID+")");
         System.out.println("Move to location: x: " + X.getValue() + " y: " + Y.getValue() + " z: " + Z.getValue());
         System.out.println("Location: " + location);
         System.out.println("Heading: "+heading.getValue());
         
         //UUID uuid = UUID.fromString(taskee);
         System.out.print("Preform MagicMove task");
         //Fakes a magic move interaction

         locationCoords.set(0, X.getValue());
         locationCoords.set(1, Y.getValue());
         locationCoords.set(2, Z.getValue());
         
         updateOnRequest(_theObject);
         System.out.println(" -> Ok");
      }
   }

   public void sendCapabilitiesInteraction(String taskId, String taskee){
      System.out.println();
      System.out.print("Sending CapabilitiesSupported interaction");
      try {
         ParameterHandleValueMap parameters = _rtiAmbassador.getParameterHandleValueMapFactory().create(3);
         HLAvariableArray<HLAASCIIstring> capabilityNames = _encoderFactory.createHLAvariableArray(_stringEncoderFactory);
         capabilityNames.addElement(_encoderFactory.createHLAASCIIstring("ETR_Root.ETR_SimCon.QuerySupportedCapabilities"));
         capabilityNames.addElement(_encoderFactory.createHLAASCIIstring("ETR_Root.ETR_SimCon.MagicMove"));
         
         parameters.put(_TaskId, taskId.getBytes());
         parameters.put(_Taskee, taskee.getBytes());
         parameters.put(_CapabilityNames, capabilityNames.toByteArray()); //TODO: Add array of supported capabilities
         _rtiAmbassador.sendInteraction(_CapabilitiesSupported, parameters, null);

      } catch (FederateNotExecutionMember | NotConnected | InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | RTIinternalError e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      System.out.println(" -> OK");
   }

   

   public void provideAttributeValueUpdate(
      ObjectInstanceHandle theObject,
      AttributeHandleSet theAttributes,
      byte[] userSuppliedTag)
   {
      System.out.println("Received request for provideAttributeValueUpdate on " + theObject.toString());
      updateOnRequest(theObject);
   }

   public void updateOnRequest(ObjectInstanceHandle theObject){
      try {

         HLAoctet spatialDiscriminant = _encoderFactory.createHLAoctet((byte)1); // create discriminant
         HLAvariantRecord<HLAoctet> spatial = _encoderFactory.createHLAvariantRecord(spatialDiscriminant);
         HLAfixedRecord staticSpatial = _encoderFactory.createHLAfixedRecord();
         HLAfixedRecord location = _encoderFactory.createHLAfixedRecord(); // X, Y, Z
         location.add(_encoderFactory.createHLAfloat64BE(locationCoords.get(0))); //1
         location.add(_encoderFactory.createHLAfloat64BE(locationCoords.get(1))); //2
         location.add(_encoderFactory.createHLAfloat64BE(locationCoords.get(2))); //3
         HLAboolean isFrozen = _encoderFactory.createHLAboolean(false);
         HLAfixedRecord orientation = _encoderFactory.createHLAfixedRecord(); // Psi, Theta, Phi
         orientation.add(_encoderFactory.createHLAfloat32BE(4));
         orientation.add(_encoderFactory.createHLAfloat32BE(5));
         orientation.add(_encoderFactory.createHLAfloat32BE(6));
       
         staticSpatial.add(location);
         staticSpatial.add(isFrozen);
         staticSpatial.add(orientation);

         spatial.setVariant(spatialDiscriminant, staticSpatial);

         //Convert _uuid from string to byte array
         //string -> UUID -> Byte[16]
         UUID uuid = UUID.fromString(_uuid);
         byte[] byteArray = convertUUIDToBytes(uuid);

         HLAfixedArray<HLAbyte> arrayForUUID = _encoderFactory.createHLAfixedArray(_byteEncoderFactory, 16);

         for(int i = 0; i < 16; i++){
            arrayForUUID.get(i).setValue(byteArray[i]);
         }

         AttributeHandleValueMap _ahvp = _rtiAmbassador.getAttributeHandleValueMapFactory().create(4);
         _ahvp.put(_UniqueId, arrayForUUID.toByteArray());
         _ahvp.put(_Callsign, _encoderFactory.createHLAunicodeString(_uuid).toByteArray());
         _ahvp.put(_Status, _encoderFactory.createHLAoctet((byte)1).toByteArray());
         _ahvp.put(_Spatial, spatial.toByteArray());
         _rtiAmbassador.updateAttributeValues(theObject, _ahvp, new byte[]{});
      } catch (RTIinternalError | FederateNotExecutionMember | NotConnected | AttributeNotOwned | AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static UUID convertBytesToUUID(byte[] bytes) {
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      long high = byteBuffer.getLong();
      long low = byteBuffer.getLong();
      return new UUID(high, low);
  }

   public static byte[] convertUUIDToBytes(UUID uuid) {
      ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
      bb.putLong(uuid.getMostSignificantBits());
      bb.putLong(uuid.getLeastSignificantBits());
      return bb.array();
  }

}
