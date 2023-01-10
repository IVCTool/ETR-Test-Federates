package nato.sto.nmsg.amsp04.edb.etr;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.encoding.*;

import java.io.File;
import java.net.URL;
import java.util.Iterator;



import java.io.File;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;

import java.nio.ByteBuffer;
import java.util.*;

class QuerySupportedCapabilities extends NullFederateAmbassador implements Runnable
{
   RTIambassador _rtiAmbassador;
   String _federateType = "SupportedCapabilities";
   String _rtiHost;
   String _rtiPort;
   String _federationName;
   String _localSettingsDesignator;
   String _uuid;

   long _timeout;
   String _requestTaskId = "a7318fed-2744-4987-81c8-11f5c1877057";

   InteractionClassHandle _ETR_SimCon;
   ParameterHandle _TaskId; // Required
   ParameterHandle _Taskee; // Required
   ParameterHandle _Tasker; // Optional
   InteractionClassHandle _QuerySupportedCapabilities;
   InteractionClassHandle _CapabilitiesSupported;
   ParameterHandle _CapabilityNames; // Required
   InteractionClassHandle _MagicMove;
   ParameterHandle _Location; // Required magic move
   ParameterHandle _Heading; //Required magic move

   ObjectClassHandle _NETN_Aggregate;
   AttributeHandle _EntityType; //Required
   AttributeHandle _EntityIdentifier; //Required
   AttributeHandle _Spatial; //Required
   AttributeHandle _UniqueId; //Required
   AttributeHandle _Status; //Required
   AttributeHandle _Callsign; //Required
   AttributeHandleSet _attributes;

   hla.rti1516e.encoding.EncoderFactory _encoderFactory;

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
      if (args.length != 5) {
         System.out.println("Arguments required: rtiHost rtiPort FederationName UUID timeout");
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
      _timeout = Integer.parseInt( args[4] );
      _localSettingsDesignator = "crcHost=" + _rtiHost + "\n" + "crcPort=" + _rtiPort; // Pitch pRTI
      System.out.println("----- Application Arguments -----");
      System.out.println("rtiHost = " + _rtiHost);
      System.out.println("rtiPort = " + _rtiPort);
      System.out.println("FederationName = " + _federationName);
      System.out.println("uuid = " + _uuid);
      System.out.println("timeout = " + _timeout);
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
         URL _netnETR = new File("./lib/fom/NETN-ETR.xml").toURI().toURL();
         URL[] urls = new URL[]{_netnETR};

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

         System.out.print("publishInteractionClass(ETR_Root.ETR_SimCon.QuerySupportedCapabilities)");
         _ETR_SimCon = _rtiAmbassador.getInteractionClassHandle(
                 "ETR_Root.ETR_SimCon");
         _TaskId = _rtiAmbassador.getParameterHandle(
                 _ETR_SimCon, "TaskId");
         _Taskee = _rtiAmbassador.getParameterHandle(
                 _ETR_SimCon, "Taskee");
         _Tasker = _rtiAmbassador.getParameterHandle(
                 _ETR_SimCon, "Tasker");
         _QuerySupportedCapabilities = _rtiAmbassador.getInteractionClassHandle(
                 "ETR_Root.ETR_SimCon.QuerySupportedCapabilities");
         _rtiAmbassador.publishInteractionClass(_QuerySupportedCapabilities);
         System.out.println(" -> OK");

         System.out.print("subscribeInteractionClass(ETR_Root.ETR_SimCon.CapabilitiesSupported)");
         _CapabilitiesSupported = _rtiAmbassador.getInteractionClassHandle(
                 "ETR_Root.ETR_SimCon.CapabilitiesSupported");
         _CapabilityNames = _rtiAmbassador.getParameterHandle(
                 _CapabilitiesSupported, "CapabilityNames");
         _rtiAmbassador.subscribeInteractionClass(_CapabilitiesSupported);
         System.out.println(" -> OK");
         
         System.out.print("publishInteractionClass(ETR_Root.ETR_SimCon.MagicMove)");
         _MagicMove = _rtiAmbassador.getInteractionClassHandle(
            "ETR_Root.ETR_SimCon.MagicMove");
         _Location = _rtiAmbassador.getParameterHandle(_MagicMove, "Location");
         _Heading = _rtiAmbassador.getParameterHandle(_MagicMove, "Heading");
         _rtiAmbassador.publishInteractionClass(_MagicMove);
         System.out.println(" -> OK");

         System.out.println("\n----- Wait until discovery of NETN_Aggregate with UniqueId = " + _uuid + " -----");
         
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
         _rtiAmbassador.subscribeObjectClassAttributes(_NETN_Aggregate, _attributes);


         Thread.sleep(2000);

         System.out.println("\n----- Query Supported Capabilities -----");

         System.out.print("sendInteraction(ETR_Root.ETR_SimCon.QuerySupportedCapabilities, {TaskId=" + _requestTaskId + ", Taskee=" + _uuid + "})");

         ParameterHandleValueMap parameters = _rtiAmbassador.getParameterHandleValueMapFactory().create(2);

         parameters.put(_TaskId, _requestTaskId.getBytes());
         parameters.put(_Taskee, _uuid.getBytes());

         _rtiAmbassador.sendInteraction(_QuerySupportedCapabilities, parameters, null);
         System.out.println("\nSendInteraction  -> OK");

         //System.out.print("Sending MagicMove interaction");
         //sendMagicMove(); //If federate does not support query this works also.
         //System.out.println(" -> Ok");

         System.out.println("\n----- Wait " + _timeout/1000 +"s for CapabilitiesSupported interaction -----");
         // Wait for Capabilities Supported or timeout

         long currentTime = System.currentTimeMillis() ;
         long endtime = currentTime + _timeout;

         do {
               Thread.sleep(500);
               currentTime = System.currentTimeMillis() ;
               System.out.print("\r" + (endtime-currentTime+1000)/1000+"s");
         }  while (currentTime < endtime);

                 _rtiAmbassador.resignFederationExecution(
            ResignAction.DELETE_OBJECTS_THEN_DIVEST);
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
      if (interactionClass.equals(_CapabilitiesSupported)) {
         
         String taskId = "";
         HLAvariableArray<HLAASCIIstring> capabilityNamesArray = _encoderFactory.createHLAvariableArray(_stringEncoderFactory);
         for (Iterator<ParameterHandle> i = parameterValues.keySet().iterator(); i.hasNext(); ) {
            ParameterHandle parameterHandle = (ParameterHandle)i.next();
            if (parameterHandle.equals(_CapabilityNames)) {
               try {
                  capabilityNamesArray.decode(parameterValues.get(parameterHandle));
               } catch (DecoderException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            } else if (parameterHandle.equals(_TaskId)) {
               taskId = new String(parameterValues.get(parameterHandle));
            }
         }
         System.out.println("receiveInteraction: CapabilitiesSupported from TaskId:" + taskId +" Containing the following capabilities:");
         for( int i = 0; i < capabilityNamesArray.size(); i++){
            System.out.println("\t" + capabilityNamesArray.get(i).getValue());
         }

         if (_requestTaskId.equals(taskId)) {
            System.out.println("Successful test of NETN-ETR QueryCapabilties!");
         }
         System.out.print("Sending MagicMove interaction");
         sendMagicMove();
         System.out.println(" -> Ok");
      }
   }

   public void sendMagicMove(){

      try {
         ParameterHandleValueMap parameters = _rtiAmbassador.getParameterHandleValueMapFactory().create(4);
      
         HLAfixedRecord location = _encoderFactory.createHLAfixedRecord();
         location.add(_encoderFactory.createHLAfloat64BE(2981));
         location.add(_encoderFactory.createHLAfloat64BE(8000));
         location.add(_encoderFactory.createHLAfloat64BE(900));
         

         HLAfloat32BE heading = _encoderFactory.createHLAfloat32BE(30);
         
         UUID uuid = UUID.fromString(_uuid);
         UUID taskId = UUID.fromString(_requestTaskId);
         byte[] byteArrayUUID = convertUUIDToBytes(uuid);
         byte[] byteArrayTaskID = convertUUIDToBytes(taskId);
         
         HLAfixedArray<HLAbyte> arrayForUUID = _encoderFactory.createHLAfixedArray(_byteEncoderFactory, 16);
         HLAfixedArray<HLAbyte> arrayForTaskId = _encoderFactory.createHLAfixedArray(_byteEncoderFactory, 16);

         for(int i = 0; i < 16; i++){
            arrayForUUID.get(i).setValue(byteArrayUUID[i]);
            arrayForTaskId.get(i).setValue(byteArrayTaskID[i]);
         }

         parameters.put(_Location, location.toByteArray());
         parameters.put(_Heading, heading.toByteArray());
         parameters.put(_TaskId, arrayForTaskId.toByteArray());
         parameters.put(_Taskee, arrayForUUID.toByteArray());
         
         try {
            _rtiAmbassador.sendInteraction(_MagicMove, parameters, null);
         } catch (InteractionClassNotPublished | InteractionParameterNotDefined | InteractionClassNotDefined
               | SaveInProgress | RestoreInProgress | RTIinternalError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

      } catch (FederateNotExecutionMember | NotConnected e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public void discoverObjectInstance(
      ObjectInstanceHandle theObject,
      ObjectClassHandle theObjectClass,
      java.lang.String objectName)
   {
      
      try {
         _NETN_Aggregate = _rtiAmbassador.getObjectClassHandle("BaseEntity.AggregateEntity.NETN_Aggregate");
         System.out.print("\nVerifying "+theObject.toString()+" is of class NETN_Aggregate");
         if( theObjectClass.equals(_NETN_Aggregate)){
            System.out.println(" -> OK");
            AttributeHandleSet _attributes = _rtiAmbassador.getAttributeHandleSetFactory().create();
            _attributes.add(_EntityType);
            _attributes.add(_EntityIdentifier);
            _attributes.add(_Spatial);
            _attributes.add(_UniqueId);
            _attributes.add(_Status);
            _attributes.add(_Callsign);
            
            
            _rtiAmbassador.requestAttributeValueUpdate(theObjectClass, _attributes, null);
            System.out.println("Request Attribute value update -> OK");
         }
         
      } catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress
            | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
         
         e.printStackTrace();
      } catch (NameNotFound e) {
         
         e.printStackTrace();
      }
   }

   public void reflectAttributeValues(
      ObjectInstanceHandle theObject,
      AttributeHandleValueMap theAttributes,
      byte[] userSuppliedTag,
      OrderType sentOrdering,
      TransportationTypeHandle theTransport,
      FederateAmbassador.SupplementalReflectInfo reflectInfo)
      {
         


         //Decoders
         HLAunicodeString unicodeDecoder = _encoderFactory.createHLAunicodeString();
         HLAfixedArray<HLAbyte> arrayDecoder = _encoderFactory.createHLAfixedArray(_byteEncoderFactory, 16);
         HLAoctet octetDecoder = _encoderFactory.createHLAoctet();
         
         

         try {
            System.out.print("\nReceived Reflection, Verifying UUID is same");
            
            arrayDecoder.decode(theAttributes.get(_UniqueId));
            UUID uuid = convertBytesToUUID(arrayDecoder.toByteArray());
            if(uuid.toString().equals(_uuid)){
               System.out.println(" -> OK");
               System.out.print("Received reflection from: ");
            
               System.out.println(uuid);
               //unicodeDecoder.decode(theAttributes.get(_UniqueId));
               //System.out.println("Received reflection from: " + unicodeDecoder.getValue());
               
               unicodeDecoder.decode(theAttributes.get(_Callsign));
               System.out.println("Callsign: " + unicodeDecoder.getValue());
   
               octetDecoder.decode(theAttributes.get(_Status));
               System.out.println("Status: " + octetDecoder.getValue());
   
               HLAvariantRecord decoder = buildSpatialDecoder();
               decoder.decode(theAttributes.get(_Spatial));
   
               decoder.setDiscriminant(decoder.getDiscriminant());
               HLAfixedRecord values = (HLAfixedRecord)decoder.getValue();
   
               System.out.println("Spatial discriminant: " + decoder.getDiscriminant());
               System.out.println("Spatial location: " + values.get(0));
               System.out.println("Discover object -> OK\n");
            }


         } catch (DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         
      }

   public HLAvariantRecord buildSpatialDecoder(){
      HLAoctet discriminant = _encoderFactory.createHLAoctet();
      
      HLAfixedRecord location = _encoderFactory.createHLAfixedRecord(); //X, Y, Z
      HLAfixedRecord orientation = _encoderFactory.createHLAfixedRecord(); //Psi, Theta, Phi
      HLAfixedRecord velocity = _encoderFactory.createHLAfixedRecord(); //XYZ velocity
      HLAfixedRecord angularVelocity = _encoderFactory.createHLAfixedRecord();
      HLAfixedRecord accelerationVector = _encoderFactory.createHLAfixedRecord();
      hla.rti1516e.encoding.HLAboolean isFrozen = _encoderFactory.createHLAboolean();

      HLAfixedRecord staticSpatial = _encoderFactory.createHLAfixedRecord(); //Container location, isFrozen and orientation
      HLAfixedRecord FPWSpatial = _encoderFactory.createHLAfixedRecord();
      HLAfixedRecord RPWSpatial = _encoderFactory.createHLAfixedRecord();
      HLAfixedRecord RVWSpatial = _encoderFactory.createHLAfixedRecord();
      HLAfixedRecord FVWSpatial = _encoderFactory.createHLAfixedRecord();
      HLAfixedRecord FPBSpatial = _encoderFactory.createHLAfixedRecord();
      HLAfixedRecord RPBSpatial = _encoderFactory.createHLAfixedRecord();
      HLAfixedRecord RVBSpatial = _encoderFactory.createHLAfixedRecord();
      HLAfixedRecord FVBSpatial = _encoderFactory.createHLAfixedRecord();

      HLAvariantRecord decoder = _encoderFactory.createHLAvariantRecord(discriminant);
      location.add(_encoderFactory.createHLAfloat64BE());
      location.add(_encoderFactory.createHLAfloat64BE());
      location.add(_encoderFactory.createHLAfloat64BE());

      orientation.add(_encoderFactory.createHLAfloat32BE());
      orientation.add(_encoderFactory.createHLAfloat32BE());
      orientation.add(_encoderFactory.createHLAfloat32BE());

      velocity.add(_encoderFactory.createHLAfloat32BE());
      velocity.add(_encoderFactory.createHLAfloat32BE());
      velocity.add(_encoderFactory.createHLAfloat32BE());

      angularVelocity.add(_encoderFactory.createHLAfloat32BE());
      angularVelocity.add(_encoderFactory.createHLAfloat32BE());
      angularVelocity.add(_encoderFactory.createHLAfloat32BE());

      accelerationVector.add(_encoderFactory.createHLAfloat32BE());
      accelerationVector.add(_encoderFactory.createHLAfloat32BE());
      accelerationVector.add(_encoderFactory.createHLAfloat32BE());

      staticSpatial.add(location);
      staticSpatial.add(isFrozen);
      staticSpatial.add(orientation);

      FPWSpatial.add(location);
      FPWSpatial.add(isFrozen);
      FPWSpatial.add(orientation);
      FPWSpatial.add(velocity);

      RPWSpatial.add(location);
      RPWSpatial.add(isFrozen);
      RPWSpatial.add(orientation);
      RPWSpatial.add(velocity);
      RPWSpatial.add(angularVelocity);

      RVWSpatial.add(location);
      RVWSpatial.add(isFrozen);
      RVWSpatial.add(orientation);
      RVWSpatial.add(velocity);
      RVWSpatial.add(angularVelocity);
      RVWSpatial.add(accelerationVector);

      FVWSpatial.add(location);
      FVWSpatial.add(isFrozen);
      FVWSpatial.add(orientation);
      FVWSpatial.add(velocity);
      FVWSpatial.add(accelerationVector);

      FPBSpatial.add(location);
      FPBSpatial.add(isFrozen);
      FPBSpatial.add(orientation);
      FPBSpatial.add(velocity);

      RPBSpatial.add(location);
      RPBSpatial.add(isFrozen);
      RPBSpatial.add(orientation);
      RPBSpatial.add(velocity);
      RPBSpatial.add(angularVelocity);

      RVBSpatial.add(location);
      RVBSpatial.add(isFrozen);
      RVBSpatial.add(orientation);
      RVBSpatial.add(velocity);
      RVBSpatial.add(accelerationVector);
      RVBSpatial.add(angularVelocity);

      FVBSpatial.add(location);
      FVBSpatial.add(isFrozen);
      FVBSpatial.add(orientation);
      FVBSpatial.add(velocity);
      FVBSpatial.add(accelerationVector);

      decoder.setVariant(_encoderFactory.createHLAoctet((byte)1), staticSpatial);
      decoder.setVariant(_encoderFactory.createHLAoctet((byte)2), FPWSpatial);
      decoder.setVariant(_encoderFactory.createHLAoctet((byte)3), RPWSpatial);
      decoder.setVariant(_encoderFactory.createHLAoctet((byte)4), RVWSpatial);
      decoder.setVariant(_encoderFactory.createHLAoctet((byte)5), FVWSpatial);
      decoder.setVariant(_encoderFactory.createHLAoctet((byte)6), FPBSpatial);
      decoder.setVariant(_encoderFactory.createHLAoctet((byte)7), RPBSpatial);
      decoder.setVariant(_encoderFactory.createHLAoctet((byte)8), RVBSpatial);
      decoder.setVariant(_encoderFactory.createHLAoctet((byte)9), FVBSpatial);

      return decoder;
   }

   //Util func to convert byte -> UUID
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
