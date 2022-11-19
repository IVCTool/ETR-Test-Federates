package nato.sto.nmsg.amsp04.edb.etr;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

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

         System.out.print("connect(" + _localSettingsDesignator + ")");
         _rtiAmbassador.connect(this, CallbackModel.HLA_IMMEDIATE, _localSettingsDesignator);
         System.out.println(" -> OK");

         URL _rprSwitches = new File("./lib/fom/RPR-Switches_v2.0.xml").toURI().toURL();
         URL _netnETR = new File("./lib/fom/NETN-ETR.xml").toURI().toURL();
         URL[] urls = new URL[]{_netnETR};

         try {
            System.out.print("createFederationExecution(" + _federationName + ")");
            _rtiAmbassador.createFederationExecution("ETR", _rprSwitches);
            System.out.println(" -> OK");
         } catch (FederationExecutionAlreadyExists ignored) {
            System.out.println(" -> FederationExecutionAlreadyExists");
         }
         System.out.print("joinFederationExecution(" + _federateType + ", " + _federationName + ")");
         FederateHandle federateHandle = _rtiAmbassador.joinFederationExecution(_federateType, "ETR", urls);
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

         System.out.println("\n----- Wait until discovery of NETN_Aggregate with UniqueId = " + _uuid + " -----");


         System.out.println("\n----- Query Supported Capabilities -----");

         System.out.print("sendInteraction(ETR_Root.ETR_SimCon.QuerySupportedCapabilities, {TaskId=" + _requestTaskId + ", Taskee=" + _uuid + "})");
         ParameterHandleValueMap parameters = _rtiAmbassador.getParameterHandleValueMapFactory().create(2);

         parameters.put(_TaskId, _requestTaskId.getBytes());
         parameters.put(_Taskee, _uuid.getBytes());

         _rtiAmbassador.sendInteraction(_QuerySupportedCapabilities, parameters, null);
         System.out.println(" -> OK");

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
         String capabilityNames = "";
         String taskId = "";

         for (Iterator<ParameterHandle> i = parameterValues.keySet().iterator(); i.hasNext(); ) {
            ParameterHandle parameterHandle = (ParameterHandle)i.next();
            if (parameterHandle.equals(_CapabilityNames)) {
               capabilityNames = new String(parameterValues.get(parameterHandle));
            } else if (parameterHandle.equals(_TaskId)) {
               taskId = new String(parameterValues.get(parameterHandle));
            }
         }
         System.out.println("receiveInteraction: CapabilitiesSupported(TaskId:" + taskId + ", CapabilityNames:" + capabilityNames + ")");
   
         if (_requestTaskId.equals(taskId)) {
            System.out.println("Successful test of NETN-ETR QueryCapabilties!");
         }
      }
   }
}
