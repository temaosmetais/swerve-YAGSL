package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IntakeJointSubsystem extends SubsystemBase {
  VictorSPX leftMotor = new VictorSPX(Constants.IntakeJointConstants.LEFT_MOTOR);
  VictorSPX rightMotor = new VictorSPX(Constants.IntakeJointConstants.RIGHT_MOTOR);

  public IntakeJointSubsystem() {}

  public void setMotors(int percentOutput) {
    leftMotor.set(VictorSPXControlMode.PercentOutput, percentOutput);
    rightMotor.set(VictorSPXControlMode.PercentOutput, percentOutput);
  }

  @Override
  public void periodic() {

  }

}
