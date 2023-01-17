package nato.sto.nmsg.amsp04.edb.etr;


import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.encoding.*;

public class spatialDecoder {
    private static hla.rti1516e.encoding.EncoderFactory _encoderFactory;

    

    public static HLAvariantRecord buildSpatialDecoder() throws RTIinternalError{
        
        _encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        
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
        RVWSpatial.add(accelerationVector);
        RVWSpatial.add(angularVelocity);
  
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
}
