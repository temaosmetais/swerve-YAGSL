// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.core.util.oi.SmartController;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.swervedrive.IntakeJointAscend;
import frc.robot.commands.swervedrive.IntakeJointDescend;
import frc.robot.commands.swervedrive.drivebase.AbsoluteDrive;
import frc.robot.commands.swervedrive.drivebase.AbsoluteDriveAdv;
import frc.robot.subsystems.IntakeJointSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import java.io.File;
import java.util.Map;
import java.util.Optional;

import com.pathplanner.lib.commands.PathPlannerAuto;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic
 * methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and
 * trigger mappings) should be declared here.
 */
public class RobotContainer {

  // Auto Chooser
  public static Optional<Alliance> ally = DriverStation.getAlliance();
  private static SendableChooser<Command> RedAllianceChooser;
  private static SendableChooser<Command> BlueAllianceChooser;
  public static GenericEntry allianceChooser;

  // The robot's subsystems and commands are defined here...
  private final IntakeJointSubsystem m_IntakeJointSubsystem;
  private final SwerveSubsystem drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
      "swerve"));

  // Replace with CommandPS4Controller or CommandJoystick if needed
  final SmartController driverXbox = new SmartController(0);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {

    m_IntakeJointSubsystem = new IntakeJointSubsystem();
    // Configure the trigger bindings
    configureBindings();
    //drivebase.setChassisSpeeds(new ChassisSpeeds(0, 0, 0));

    // Add 2 more choosers to distingue between RED and BLUE alliances' auto commands
    // Auto Chooser

    allianceChooser = Shuffleboard.getTab("Autonomous")
   .add("IsBlueAlliance", ally.get() == Alliance.Blue)
   .withWidget("Boolean Box")
   .withProperties(Map.of("colorWhenTrue", "blue", "colorWhenFalse", "red"))
   .getEntry();

    RedAllianceChooser = new SendableChooser<Command>();
    RedAllianceChooser.setDefaultOption("AutoArthur", drivebase.getAutonomousCommand("autoarthur"));
    RedAllianceChooser.addOption("Curva", drivebase.getAutonomousCommand("curvaa"));

    BlueAllianceChooser = new SendableChooser<Command>();
    BlueAllianceChooser.setDefaultOption("AutoArthur", drivebase.getAutonomousCommand("autoarthur"));
    BlueAllianceChooser.addOption("Curva", drivebase.getAutonomousCommand("curvaa"));

    Shuffleboard.getTab("Autonomous").add("RedAllianceChooser", RedAllianceChooser);
    Shuffleboard.getTab("Autonomous").add("BlueAllianceChooser", BlueAllianceChooser);

    Command driveFieldOrientedAnglularVelocity = drivebase.driveCommand(
        () -> -MathUtil.applyDeadband((driverXbox.getLeftY() * Constants.HIDConstants.LEFT_Y_AXIS_MODIFIER), OperatorConstants.LEFT_Y_DEADBAND),
        () -> -MathUtil.applyDeadband((driverXbox.getLeftX() * Constants.HIDConstants.LEFT_X_AXIS_MODIFIER), OperatorConstants.LEFT_X_DEADBAND),
        () -> -driverXbox.getRightX() * Constants.HIDConstants.RIGHT_X_AXIS_MODIFIER);
    
     AbsoluteDriveAdv closedAbsoluteDriveAdv = new AbsoluteDriveAdv(drivebase,
     () -> -MathUtil.applyDeadband(driverXbox.getLeftY(),
    OperatorConstants.LEFT_Y_DEADBAND),
     () -> -MathUtil.applyDeadband(driverXbox.getLeftX(),
     OperatorConstants.LEFT_X_DEADBAND),
     () -> -MathUtil.applyDeadband(driverXbox.getRightX(),
     OperatorConstants.RIGHT_X_DEADBAND),
     driverXbox.getYButton(),
     driverXbox.getAButton(),
     driverXbox.getXButton(),
     driverXbox.getBButton());
     
     AbsoluteDrive absoluteDrive = new AbsoluteDrive(drivebase, () -> driverXbox.getLeftX(), () -> driverXbox.getLeftY(), () -> driverXbox.getRightX(), () -> driverXbox.getRightY());
    // Applies deadbands and inverts controls because joysticks
    // are back-right positive while robot
    // controls are front-left positive
    // left stick controls translation
    // right stick controls the desired angle NOT angular rotation

    
     Command driveFieldOrientedDirectAngle = drivebase.driveCommand(
     () -> MathUtil.applyDeadband(driverXbox.getLeftY()  * 1,
     OperatorConstants.LEFT_Y_DEADBAND),
     () -> MathUtil.applyDeadband(driverXbox.getLeftX() * 1,
     OperatorConstants.LEFT_X_DEADBAND),
     () -> driverXbox.getRightX(),
     () -> driverXbox.getRightY());
     
    // Applies deadbands and inverts controls because joysticks
    // are back-right positive while robot
    // controls are front-left positive
    // left stick controls translation
    // right stick controls the angular velocity of the 8robot

    
     Command driveFieldOrientedDirectAngleSim = drivebase.simDriveCommand(
     () -> MathUtil.applyDeadband(driverXbox.getLeftY() * 0.8,
     OperatorConstants.LEFT_Y_DEADBAND),
     () -> MathUtil.applyDeadband(driverXbox.getLeftX() * 0.8,
     OperatorConstants.LEFT_X_DEADBAND),
     () -> driverXbox.getRightX());
     
    drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity);
    /*drivebase.setDefaultCommand(
    !RobotBase.isSimulation() ? driveFieldOrientedDirectAngle : driveFieldOrientedDirectAngleSim);
 */
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
   * an arbitrary predicate, or via the
   * named factories in
   * {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses
   * for
   * {@link CommandXboxController
   * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick
   * Flight joysticks}.
   */
  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`

    driverXbox.whileAButton((Commands.runOnce(drivebase::zeroGyro)));
    driverXbox.whileXButton(Commands.runOnce(drivebase::addFakeVisionReading));
    driverXbox.whileBButton(
        Commands.deferredProxy(() -> drivebase.driveToPose(
            new Pose2d(new Translation2d(4, 4), Rotation2d.fromDegrees(0)))));
    driverXbox.whileXButton(Commands.runOnce(drivebase::lock,
    drivebase).repeatedly());
    driverXbox.whileRightBumper(new IntakeJointAscend(m_IntakeJointSubsystem));
    driverXbox.whileLeftBumper(new IntakeJointDescend(m_IntakeJointSubsystem));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    if (ally.isPresent()) {
      if (ally.get() == Alliance.Red) {
          return RedAllianceChooser.getSelected();
      } else {
          return BlueAllianceChooser.getSelected();
      }
    }
    else {
      return RedAllianceChooser.getSelected();
    }
  } 	

  public void setDriveMode() {
    //drivebase.setDefaultCommand();
  }

  public void setMotorBrake(boolean brake) {
    drivebase.setMotorBrake(brake);
  }

}
