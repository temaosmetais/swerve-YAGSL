package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeJointSubsystem extends SubsystemBase {
  VictorSPX leftMotor = new VictorSPX(8);
  VictorSPX rightMotor = new VictorSPX(9);

  public IntakeJointSubsystem() {}

  public void setMotors(int percentOutput) {
    leftMotor.set(VictorSPXControlMode.PercentOutput, percentOutput);
    rightMotor.set(VictorSPXControlMode.PercentOutput, percentOutput);
  }

  @Override
  public void periodic() {

  }

}
