/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.Rev2mDistanceSensor.Port;


//import frc.robot.subsystems.Rev2Meter;
import edu.team997.first.wpilibj.*;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import frc.robot.subsystems.i2cSubclass;
import frc.robot.subsystems.TCS34725_I2C.TransferAbortedException;
//import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.I2C;
import frc.robot.subsystems.TCS34725_I2C.TCSColor;
import frc.robot.subsystems.TCS34725_I2C;

import frc.robot.subsystems.i2cSubclass;

//import frc.robot.subsystems.LidarProxy;
import com.revrobotics.*;
import com.revrobotics.Rev2mDistanceSensor.Port;
import com.revrobotics.jni.VL53L0XJNI;
//import edu.team997.first.wpilibj.*;
public class Robot extends TimedRobot {

  // private AHRS ahrs;
  AHRS ahrs;
  private MecanumDrive m_myRobot;
  private Joystick m_leftStick;
  public final int kGamepadButtonA = 1;
  private final int leftFrontDeviceID = 1;
  private final int leftBackDeviceID = 3;
  private final int rightFrontDeviceID = 5;
  private final int rightBackDeviceID = 2;
  private CANSparkMax m_leftFrontMotor;
  private CANSparkMax m_leftBackMotor;
  private CANSparkMax m_rightFrontMotor;
  private CANSparkMax m_rightBackMotor;
  private CANEncoder m_leftBackEncoder;
  private CANEncoder m_leftFrontEncoder;
  private CANEncoder m_rightBackEncoder;
  private CANEncoder m_rightFrontEncoder;

  public double yValue;
  public double xValue;
  public double zValue;
  public float leftPower;
  public float rightPower;
  public double roboGyro;

  private boolean m_LimelightHasValidTarget = false;
  private double yPower = 0.0;
  private double xPower = 0.0;

  Compressor m_compressor = new Compressor(0);
  DoubleSolenoid solenoidDouble = new DoubleSolenoid(1, 2);

  public boolean debugVar = false;
  public boolean dashboardFlag = false;

  int red;
  int blue;
  int green;
  int c;
  int totalColor;

  String[] smartdashBooleans;
  String[] smartdashNumber;

  Double[] smartdashPointer;

  //private i2cSubclass sensor;
  
  //private Rev2mDistanceSensor distOnboard;

    TCS34725_I2C colorSensor = new TCS34725_I2C();
   // TCSColor colorData = new TCSColor(red, blue, green, c);
  
    

  @Override
  public void robotInit() {

    /**
     * SPARK MAX controllers are intialized over CAN by constructing a CANSparkMax
     * object
     * 
     * The CAN ID, which can be configured using the SPARK MAX Client, is passed as
     * the first parameter
     * 
     * The motor type is passed as the second parameter. Motor type can either be:
     * com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless
     * com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushed
     * 
     * The example below initializes four brushless motors with CAN IDs 1 and 2.
     * Change these parameters to match your setup
     */
    m_leftFrontMotor = new CANSparkMax(leftFrontDeviceID, MotorType.kBrushless);
    m_leftBackMotor = new CANSparkMax(leftBackDeviceID, MotorType.kBrushless);
    m_rightFrontMotor = new CANSparkMax(rightFrontDeviceID, MotorType.kBrushless);
    m_rightBackMotor = new CANSparkMax(rightBackDeviceID, MotorType.kBrushless);

    m_myRobot = new MecanumDrive(m_leftFrontMotor, m_leftBackMotor, m_rightFrontMotor, m_rightBackMotor);

    m_leftBackEncoder = m_leftFrontMotor.getEncoder();
    m_leftBackEncoder = m_leftBackMotor.getEncoder();
    m_rightFrontEncoder = m_rightFrontMotor.getEncoder();
    m_rightBackEncoder = m_rightBackMotor.getEncoder();

    m_leftFrontMotor.setIdleMode(IdleMode.kBrake);
    m_rightFrontMotor.setIdleMode(IdleMode.kBrake);
    m_leftBackMotor.setIdleMode(IdleMode.kBrake);
    m_rightBackMotor.setIdleMode(IdleMode.kBrake);

    m_compressor.setClosedLoopControl(true);

   // i2cSubclass sensor = new i2cSubclass();

   // distOnboard = new Rev2mDistanceSensor(Port.kOnboard);
     try {
       colorSensor.initialize(0xFF, 0x00);
       colorSensor.enable();
       //colorSensor.setIntegrationTime(0xEB);
     }
      catch (TransferAbortedException | InterruptedException e) {
      }
      
    
    
     
    /**
     * The RestoreFactoryDefaults method can be used to reset the configuration parameters
     * in the SPARK MAX to their factory default state. If no argument is passed, these
     * parameters will not persist between power cycles
     */
    // m_leftMotor.restoreFactoryDefaults();
    // m_rightMotor.restoreFactoryDefaults();

    m_leftStick = new Joystick(1); 
    //m_rightStick = new Joystick(0);

    try {
      ahrs = new AHRS(SPI.Port.kMXP);
    } catch (RuntimeException ex ) {
      DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
    }

    Double gyroConnect = (double)(ahrs.isConnected() ? 1.0 : 0.0);
    Double gyroCalibrating = (double)(ahrs.isCalibrating() ? 1.0 : 0.0);
    Double compressorEnable = (double)(m_compressor.enabled() ? 1.0 : 0.0);
    Double pressureSwitch = (double)(m_compressor.getPressureSwitchValue() ? 1.0 : 0.0);
   
      smartdashBooleans = new String[] { //input the name that you want the box to have here.
        "IMU_Yaw",
        "IMU_Pitch",
        "IMU_Roll",
        "Angle",
        "IMU_Connected", 
        "IMU_IsCalibrating",
        "Compressor Enabled? ",
        "Pressure Switch Open? ",
        "Compressor Current Value: "
      };

      smartdashPointer = new Double[] { //input the value or location of the value here, if boolean or int needs to be cast to double (look above for how to do that)
        (double)ahrs.getYaw(),
        (double)ahrs.getPitch(),
        (double)ahrs.getRoll(),
        ahrs.getAngle(),
        gyroConnect,
        gyroCalibrating,
        compressorEnable,
        pressureSwitch,
        m_compressor.getCompressorCurrent()
      };
  }

  @Override
  public void teleopPeriodic() {

    yValue = m_leftStick.getY();
    xValue = m_leftStick.getX();
    zValue = m_leftStick.getZ();
    
    xValue = xValue/4;
    yValue = yValue/4;
    zValue = zValue/4;

    if(xValue < 0.05 && xValue > -0.1){
      xValue = 0;
    }
    if(yValue < 0.05 && yValue > -0.1){
      yValue = 0;
    }
    if(zValue < 0.05 && zValue > -0.1){
      zValue = 0;
    }

    roboGyro = ahrs.getAngle();

    if(m_leftStick.getRawButton(1)){ //trigger pressed
      Update_Limelight_Tracking();
      m_myRobot.driveCartesian(-xPower, -yPower, 0, -roboGyro);
    }
    else{//Trigger not pressed = normal drive
      m_myRobot.driveCartesian(-xValue, yValue, zValue, -roboGyro);
      Update_Limelight_Tracking();
    }
    
    if(m_leftStick.getRawButton(11)){
      solenoidDouble.set(DoubleSolenoid.Value.kForward);
    }
    else if(m_leftStick.getRawButton(12)){
      solenoidDouble.set(DoubleSolenoid.Value.kReverse);
    } 
    else{
      solenoidDouble.set(DoubleSolenoid.Value.kOff);
    }
    
  
 if (m_leftStick.getRawButton(3)){
                 ahrs.reset();
                 
             }
             
   if(m_leftStick.getRawButton(7)){
     debugVar = !debugVar;
     
   }

    //if (debugVar == true){ //Planning on adding a button on the smart dash to enable and disable the code(right now its button 7 on joystick), 
                          //changing to this array version so we can remove all lines on the smartdash board at once. Still have to implement/test. 
      //if(dashboardFlag == false){
      //for(int i = 0; i < smartdashBooleans.length; i++){ //This is the thing that executes it
        //SmartDashboard.putNumber(smartdashBooleans[i], smartdashPointer[i]);
        //SmartDashboard.putBoolean("test", debugVar);
        //SmartDashboard.updateValues(); //array values from arrays lines 128-150
       // dashboardFlag = true;
     // }
      
   // }
    
    try {
       red = colorSensor.getRawData().getR();
       blue = colorSensor.getRawData().getB();
       green = colorSensor.getRawData().getG();
       totalColor = (red + blue + green)/3;
     } catch (TransferAbortedException e) {
     }

       SmartDashboard.putNumber("Color Red: ", red);
       SmartDashboard.putNumber("Color Blue", blue);
       SmartDashboard.putNumber("Color Green", green);
       SmartDashboard.putNumber("Total Color: ", totalColor);
    //   SmartDashboard.getBoolean("Class true? ", debugVar);


      //i2cSubclass.getRange();
      // if (distOnboard.isRangeValid()) {
      //   SmartDashboard.putNumber("Range Onboard", distOnboard.getRange());
      //   SmartDashboard.putNumber("Timestamp Onboard", distOnboard.getTimestamp());
      // //   }

      // if(distOnboard.isRangeValid()) {
      //   SmartDashboard.putNumber("Range Onboard", distOnboard.getRange());
      //   SmartDashboard.putNumber("Timestamp Onboard", distOnboard.getTimestamp());
      // }
      
   
    SmartDashboard.updateValues();
  }
 // else{ //when debug mode is off this will delete all the stuff off your smartDashboard.
    //if(dashboardFlag == true){
    //dashboardFlag = false;
    //for(int i = 0; i < smartdashBooleans.length; i++){
    //SmartDashboard.delete(smartdashBooleans[i]);
    //SmartDashboard.delete("test");
    //SmartDashboard.updateValues();
    //}

    
 // }
//  }
//}

  public void Update_Limelight_Tracking()
  {
        // These numbers must be tuned for your Robot!  Be careful!
        final double xCoef = 0.015;                    // how hard to turn toward the target
        final double yCoef = 0.025;                    // how hard to drive fwd toward the target
        final double DESIRED_TARGET_AREA = 9.0;        // Area of the target when the robot reaches the wall
        final double MAX_DRIVE = 0.2;                   // Simple speed limit so we don't drive too fast

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
        double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);

        if (tv < 1.0)
        {
          m_LimelightHasValidTarget = false;
          yPower = 0.0;
          xPower = 0.0;
          return;
        }

        m_LimelightHasValidTarget = true;

        // Start with proportional steering
        double x = tx * xCoef; 
        if(x > MAX_DRIVE)
        {
          x = MAX_DRIVE;
        }
        xPower = x;

        // try to drive forward until the target area reaches our desired area
        double y = (DESIRED_TARGET_AREA - ta) * yCoef;
        if (y > MAX_DRIVE) // don't let the robot drive too fast into the goal
        {
          y = MAX_DRIVE;
        } 
        // else if(y < 0)
        // {
        //   y = 0;
        // }
        yPower = y;
        SmartDashboard.putNumber("ta", ta);

  }



public void sleep(int milliseconds){
  try {
    Thread.sleep(milliseconds);
} catch(InterruptedException e) {
    System.out.println("got interrupted!");
}
}
} 


