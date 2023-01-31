package nato.sto.nmsg.amsp04.edb.etr;


import java.util.NoSuchElementException;
import java.util.Optional;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.encoding.*;

public class spatialDecoder {
    //private static hla.rti1516e.encoding.EncoderFactory _encoderFactory;

    enum DRAEnum {
        SpatialStatic,
        FPW,
        RPW,
        RVW,
        FVW,
        FPB,
        RPB,
        RVB,
        FVB
    }

    public spatialDecoder(hla.rti1516e.encoding.EncoderFactory encoderFactory) throws RTIinternalError{
        //_encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
    }
    

    public static HLAvariantRecord buildSpatialDecoder(hla.rti1516e.encoding.EncoderFactory _encoderFactory) throws RTIinternalError{
        
        //_encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        
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

    HLAvariantRecord<HLAoctet> createSpatial_versionOne_a(
        HLAfixedRecord location, 
        HLAboolean isFrozen, 
        HLAfixedRecord orientation,
        hla.rti1516e.encoding.EncoderFactory _encoderFactory){
        /*
         * Version 1-a of spatial encoder
         * 
         * Create a encoder for each variant, requires the complex data types to be sent and this only packs it together
         */

        HLAoctet spatialDiscriminant = _encoderFactory.createHLAoctet((byte)1); // create discriminant
        HLAvariantRecord<HLAoctet> spatial = _encoderFactory.createHLAvariantRecord(spatialDiscriminant);

        HLAfixedRecord staticSpatial = _encoderFactory.createHLAfixedRecord();

        staticSpatial.add(location);
        staticSpatial.add(isFrozen);
        staticSpatial.add(orientation);

        spatial.setVariant(spatialDiscriminant, staticSpatial);

        return spatial;
    }

    HLAvariantRecord<HLAoctet> createSpatial_versionOne_b(
        HLAfloat64BE X, 
        HLAfloat64BE Y, 
        HLAfloat64BE Z, 
        HLAboolean isFrozen, 
        HLAfloat32BE Psi,
        HLAfloat32BE Theta,
        HLAfloat32BE Phi,
        hla.rti1516e.encoding.EncoderFactory _encoderFactory){
        /*
         * Version 1-b
         * 
         * Same as 1-a but this takes in the raw data types
         */
        

        HLAoctet spatialDiscriminant = _encoderFactory.createHLAoctet((byte)1); // create discriminant
        HLAvariantRecord<HLAoctet> spatial = _encoderFactory.createHLAvariantRecord(spatialDiscriminant);

        HLAfixedRecord staticSpatial = _encoderFactory.createHLAfixedRecord();

        HLAfixedRecord location = _encoderFactory.createHLAfixedRecord(); // X, Y, Z
        location.add(X); //X
        location.add(Y); //Y
        location.add(Z); //Z

        HLAfixedRecord orientation = _encoderFactory.createHLAfixedRecord(); // Psi, Theta, Phi
        orientation.add(Psi);
        orientation.add(Theta);
        orientation.add(Phi);

        staticSpatial.add(location);
        staticSpatial.add(isFrozen);
        staticSpatial.add(orientation);

        spatial.setVariant(spatialDiscriminant, staticSpatial);

        return spatial;
    
    }
    
    HLAvariantRecord<HLAoctet> createSpatial_versionTwo(DRAEnum spatialType, 
                                                        HLAfixedRecord location, 
                                                        HLAboolean isFrozen, 
                                                        HLAfixedRecord orientation,
                                                        Optional<HLAfixedRecord> velocity,
                                                        Optional<HLAfixedRecord> angularVelocity,
                                                        Optional<HLAfixedRecord> accelerationVector,
                                                        hla.rti1516e.encoding.EncoderFactory _encoderFactory) throws NoSuchElementException{
        
        

        HLAoctet spatialDiscriminant;
        HLAfixedRecord spatialDataStruct = _encoderFactory.createHLAfixedRecord();
        
        spatialDataStruct.add(location);
        spatialDataStruct.add(isFrozen);
        spatialDataStruct.add(orientation);

        switch(spatialType){
            case FPW:
                spatialDiscriminant = _encoderFactory.createHLAoctet((byte)2);
                spatialDataStruct.add(velocity.get());            
                break;

            case RPW:
                spatialDiscriminant = _encoderFactory.createHLAoctet((byte)3);
                spatialDataStruct.add(velocity.get());  
                spatialDataStruct.add(angularVelocity.get());  
                break;

            case RVW:
                spatialDiscriminant = _encoderFactory.createHLAoctet((byte)4);
                spatialDataStruct.add(velocity.get());
                spatialDataStruct.add(accelerationVector.get());  
                spatialDataStruct.add(angularVelocity.get());  
                break;

            case FVW:
                spatialDiscriminant = _encoderFactory.createHLAoctet((byte)5);
                spatialDataStruct.add(velocity.get());
                spatialDataStruct.add(accelerationVector.get());  
                break;

            case FPB:
                spatialDiscriminant = _encoderFactory.createHLAoctet((byte)6);
                spatialDataStruct.add(velocity.get());
                break;

            case RPB:
                spatialDiscriminant = _encoderFactory.createHLAoctet((byte)7);
                spatialDataStruct.add(velocity.get()); 
                spatialDataStruct.add(angularVelocity.get());
                break;

            case RVB:
                spatialDiscriminant = _encoderFactory.createHLAoctet((byte)8);
                spatialDataStruct.add(velocity.get());
                spatialDataStruct.add(accelerationVector.get());  
                spatialDataStruct.add(angularVelocity.get());
                break;

            case FVB:
                spatialDiscriminant = _encoderFactory.createHLAoctet((byte)9);
                spatialDataStruct.add(velocity.get());
                spatialDataStruct.add(accelerationVector.get());  
                break;

            default:
                spatialDiscriminant = _encoderFactory.createHLAoctet((byte)1);
        }


        HLAvariantRecord<HLAoctet> spatial = _encoderFactory.createHLAvariantRecord(spatialDiscriminant);
        spatial.setVariant(spatialDiscriminant, spatialDataStruct);

        return spatial;
    }

}
