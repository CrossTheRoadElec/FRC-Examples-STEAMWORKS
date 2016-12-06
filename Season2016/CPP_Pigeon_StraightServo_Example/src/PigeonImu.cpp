/*
 *  Software License Agreement
 *
 * Copyright (C) Cross The Road Electronics.  All rights
 * reserved.
 * 
 * Cross The Road Electronics (CTRE) licenses to you the right to 
 * use, publish, and distribute copies of CRF (Cross The Road) firmware files (*.crf) and Software
 * API Libraries ONLY when in use with Cross The Road Electronics hardware products.
 * 
 * THE SOFTWARE AND DOCUMENTATION ARE PROVIDED "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT
 * LIMITATION, ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * CROSS THE ROAD ELECTRONICS BE LIABLE FOR ANY INCIDENTAL, SPECIAL, 
 * INDIRECT OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF
 * PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY OR SERVICES, ANY CLAIMS
 * BY THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY DEFENSE
 * THEREOF), ANY CLAIMS FOR INDEMNITY OR CONTRIBUTION, OR OTHER
 * SIMILAR COSTS, WHETHER ASSERTED ON THE BASIS OF CONTRACT, TORT
 * (INCLUDING NEGLIGENCE), BREACH OF WARRANTY, OR OTHERWISE
 */

#include <memory>

#ifndef CTR_EXCLUDE_WPILIB_CLASSES
#include "PigeonImu.h"
#include "FRC_NetworkCommunication/CANSessionMux.h"
#include "HAL/HAL.h"

/**
 * Create a Pigeon object that communicates with Pigeon on CAN Bus.
 * @param deviceNumber CAN Device Id of Pigeon [0,62]
 */
PigeonImu::PigeonImu(int deviceNumber) : CtreCanMap(0)
{
	_deviceId = 0x15000000 | (int) deviceNumber;

	SendCAN(CONTROL_1 | _deviceId, 0x00000000, 0, 100);
}

/**
 * Create a Pigeon object that communciates with Pigeon through the Gadgeteer ribbon
 * @param talonSrx cable connected to a Talon on CAN Bus.
 */
PigeonImu::PigeonImu(CANTalon * talonSrx) : CtreCanMap(0)
{
	_deviceId = (int) 0x02000000 | (talonSrx->GetDeviceID());

	SendCAN(CONTROL_1 | _deviceId, 0x00000000, 0, 100);
}

//----------------------- Control Param routines -----------------------//
int PigeonImu::ConfigSetParameter(ParamEnum paramEnum, double paramValue)
{
	uint64_t frame;
	/* param specific encoders can be placed here */
	frame = (int32_t) paramValue;
	frame <<= 8;
	frame |= (uint8_t) paramEnum;
	int status = SendCAN(PARAM_SET | _deviceId, frame, 5, 0);
	return status;
}
int PigeonImu::ConfigSetParameter(ParamEnum paramEnum, TareType tareType, double angleDeg)
{
	const double deg_per_canunit = 0.015625f;
	int deg_3B = ((int) (angleDeg / deg_per_canunit));
	int32_t paramValue;
	paramValue = (long) deg_3B;
	paramValue <<= 8;
	paramValue |= (uint8_t) tareType;
	return ConfigSetParameter(paramEnum, (double)paramValue);
}
/**
 * Change the periodMs of a TALON's status frame.  See kStatusFrame_* enums for
 * what's available.
 */
void PigeonImu::SetStatusFrameRateMs(StatusFrameRate statusFrameRate, int periodMs) {
	/* bounds check the period */
	if (periodMs < 1)
		periodMs = 1;
	else if (periodMs > 255)
		periodMs = 255;
	/* bounds frame */
	if((unsigned int)statusFrameRate > 255)
		return;
	/* save the unsigned pieces */
	uint8_t period = (uint8_t)periodMs;
	uint8_t idx = statusFrameRate;
	/* assemble */
	int32_t paramValue = period;
	paramValue <<= 8;
	paramValue |= idx;
	/* send the set request */
	ConfigSetParameter(ParamEnum::ParamEnum_StatusFrameRate, paramValue);
}
int PigeonImu::SetYaw(double angleDeg)
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_YawOffset, TareType::SetValue, angleDeg);
	return HandleError(errCode);
}
/**
 * Atomically add to the Yaw register.
 */
int PigeonImu::AddYaw(double angleDeg)
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_YawOffset, TareType::AddOffset, angleDeg);
	return HandleError(errCode);
}
int PigeonImu::SetYawToCompass()
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_YawOffset, TareType::MatchCompass, 0);
	return HandleError(errCode);
}
int PigeonImu::SetFusedHeading(double angleDeg)
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_FusedHeadingOffset, TareType::SetValue, angleDeg);
	return HandleError(errCode);
}
int PigeonImu::SetAccumZAngle(double angleDeg)
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_AccumZ, TareType::SetValue, angleDeg);
	return HandleError(errCode);
}
/**
 * Enable/Disable Temp compensation.  Pigeon defaults with this on at boot.
 * @param tempCompEnable
 * @return nonzero for error, zero for success.
 */
int PigeonImu::EnableTemperatureCompensation(bool bTempCompEnable)
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_TempCompDisable, bTempCompEnable ? 0 : 1);
	return HandleError(errCode);
}
/**
 * Atomically add to the Fused Heading register.
 */
int PigeonImu::AddFusedHeading(double angleDeg)
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_FusedHeadingOffset, TareType::AddOffset, angleDeg);
	return HandleError(errCode);
}
int PigeonImu::SetFusedHeadingToCompass()
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_FusedHeadingOffset, TareType::MatchCompass, 0);
	return HandleError(errCode);
}
/**
 * Set the declination for compass.
 * Declination is the difference between Earth Magnetic north, and the geographic "True North".
 */
int PigeonImu::SetCompassDeclination(double angleDegOffset)
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_CompassOffset, TareType::SetOffset, 0);
	return HandleError(errCode);
}
/**
 * Sets the compass angle.
 * Although compass is absolute [0,360) degrees, the continuous compass
 * register holds the wrap-arounds.
 */
int PigeonImu::SetCompassAngle(double angleDeg)
{
	int errCode = ConfigSetParameter(ParamEnum::ParamEnum_CompassOffset, TareType::SetValue, 0);
	return HandleError(errCode);
}
//----------------------- Calibration routines -----------------------//
int PigeonImu::EnterCalibrationMode(CalibrationMode calMode)
{
	long frame;
	frame = (uint32_t) calMode;
	frame <<= 8;
	frame |= (uint8_t) ParamEnum::ParamEnum_EnterCalibration;
	int status = SendCAN(PARAM_SET | _deviceId, frame, 5, 0);
	return status;
}
/**
 * Get the status of the current (or previousley complete) calibration.
 * @param statusToFill
 */
int PigeonImu::GetGeneralStatus(PigeonImu::GeneralStatus & statusToFill)
{
	int errCode = ReceiveCAN(COND_STATUS_1);

	uint8_t b3 = (uint8_t)(_cache >> 0x18);
	uint8_t b5 = (uint8_t)(_cache >> 0x28);

	uint8_t iCurrMode = (b5 >> 4) & 0xF;
	PigeonImu::CalibrationMode currentMode = (PigeonImu::CalibrationMode)(iCurrMode);

	/* shift up bottom nibble, and back down with sign-extension */
	int32_t calibrationErr = b5 & 0xF;
	calibrationErr <<= (32 - 4);
	calibrationErr >>= (32 - 4);

	int32_t noMotionBiasCount =  (uint8_t)(_cache >> 0x24) & 0xF;
	int32_t tempCompensationCount =  (uint8_t)(_cache >> 0x20) & 0xF;
	int32_t upTimSec =  (uint8_t)(_cache >> 0x38);

	statusToFill.currentMode = currentMode;
	statusToFill.calibrationError = calibrationErr;
	statusToFill.bCalIsBooting = ((b3 & 1) == 1);
	statusToFill.state = GetState(errCode, _cache);
	statusToFill.tempC = GetTemp(_cache);
	statusToFill.noMotionBiasCount = noMotionBiasCount;
	statusToFill.tempCompensationCount = tempCompensationCount;
	statusToFill.upTimeSec = upTimSec;
	statusToFill.lastError = errCode;

	/* build description string */
	if (errCode != 0) { // same as NoComm
		statusToFill.description = "Status frame was not received, check wired connections and web-based config.";
	} else if(statusToFill.bCalIsBooting) {
		statusToFill.description = "Pigeon is boot-caling to properly bias accel and gyro.  Do not move Pigeon.  When finished biasing, calibration mode will start.";
	} else if(statusToFill.state == UserCalibration) {
		/* mode specific descriptions */
		switch(currentMode) {
			case BootTareGyroAccel:
				statusToFill.description = "Boot-Calibration: Gyro and Accelerometer are being biased.";
				break;
			case Temperature:
				statusToFill.description = "Temperature-Calibration: Pigeon is collecting temp data and will finish when temp range is reached.  "
				"Do not moved Pigeon.";
				break;
			case Magnetometer12Pt:
				statusToFill.description = "Magnetometer Level 1 calibration: Orient the Pigeon PCB in the 12 positions documented in the User's Manual.";
				break;
			case Magnetometer360:
				statusToFill.description = "Magnetometer Level 2 calibration: Spin robot slowly in 360' fashion.  ";
				break;
			case Accelerometer:
				statusToFill.description = "Accelerometer Calibration: Pigeon PCB must be placed on a level source.  Follow User's Guide for how to level surfacee.  ";
				break;
		}
	} else if (statusToFill.state == Ready){
		/* definitely not doing anything cal-related.  So just instrument the motion driver state */
		statusToFill.description = "Pigeon is running normally.  Last CAL error code was ";
		statusToFill.description += std::to_string(calibrationErr);
		statusToFill.description += ".";
	} else if (statusToFill.state == Initializing){
		/* definitely not doing anything cal-related.  So just instrument the motion driver state */
		statusToFill.description = "Pigeon is boot-caling to properly bias accel and gyro.  Do not move Pigeon.";
	} else {
		statusToFill.description = "Not enough data to determine status.";
	}

	return HandleError(errCode);
}
//----------------------- General Error status  -----------------------//
int PigeonImu::GetLastError()
{
	return _lastError;
}

int PigeonImu::HandleError(int errorCode)
{
	/* error handler */
	if (errorCode != 0) {
		/* what should we do here? */
	    wpi_setErrorWithContext(errorCode, HAL_GetErrorMessage(errorCode));
	}
	/* mirror last status */
	_lastError = errorCode;
	return _lastError;
}

//----------------------- General Signal decoders  -----------------------//
int PigeonImu::ReceiveCAN(int arbId)
{
	return ReceiveCAN(arbId, true);
}
int PigeonImu::ReceiveCAN(int arbId, bool allowStale)
{
	_len = 0;
	_cache = 0;
	return this->GetRx(arbId | _deviceId, (uint8_t *)&_cache, EXPECTED_RESPONSE_TIMEOUT_MS, allowStale);
}
int PigeonImu::SendCAN(int arbId, const uint64_t & data, int dataSize, int periodMs)
{

	int32_t status = 0;
	FRC_NetworkCommunication_CANSessionMux_sendMessage(	arbId,
														(const uint8_t*)&data,
														dataSize,
														periodMs,
														&status);
	return status;
}

/**
 * Decode two 16bit params.
 */
int PigeonImu::GetTwoParam16(int arbId, int16_t words[2])
{
	int errCode = ReceiveCAN(arbId);
	/* always give caller the latest */
	words[0] = (short)((uint8_t)(_cache));
	words[0] <<= 8;
	words[0] |= (short)((uint8_t)(_cache >> 0x08));

	words[1] = (short)((uint8_t)(_cache >> 0x10));
	words[1] <<= 8;
	words[1] |= (short)((uint8_t)(_cache >> 0x18));

	return errCode;
}
int PigeonImu::GetThreeParam16(int arbId, short words[2])
{
	int errCode = ReceiveCAN(arbId);

	words[0] = (short)((uint8_t)(_cache));
	words[0] <<= 8;
	words[0] |= (short)((uint8_t)(_cache >> 0x08));

	words[1] = (short)((uint8_t)(_cache >> 0x10));
	words[1] <<= 8;
	words[1] |= (short)((uint8_t)(_cache >> 0x18));

	words[2] = (short)((uint8_t)(_cache >> 0x20));
	words[2] <<= 8;
	words[2] |= (short)((uint8_t)(_cache >> 0x28));
	return errCode;
}

int PigeonImu::GetThreeParam16(int arbId, double signals[3], double scalar)
{
	short word_p1;
	short word_p2;
	short word_p3;
	int errCode = ReceiveCAN(arbId);

	word_p1 = (short)((uint8_t)(_cache));
	word_p1 <<= 8;
	word_p1 |= (short)((uint8_t)(_cache >> 0x08));

	word_p2 = (short)((uint8_t)(_cache >> 0x10));
	word_p2 <<= 8;
	word_p2|= (short)((uint8_t)(_cache >> 0x18));

	word_p3 = (short)((uint8_t)(_cache >> 0x20));
	word_p3 <<= 8;
	word_p3 |= (short)((uint8_t)(_cache >> 0x28));

	signals[0] = word_p1 * scalar;
	signals[1] = word_p2 * scalar;
	signals[2] = word_p3 * scalar;

	return errCode;
}

int PigeonImu::GetThreeBoundedAngles(int arbId, double boundedAngles[3])
{
	return GetThreeParam16(arbId, boundedAngles, 360. / 32768.);
}
int PigeonImu::GetFourParam16(int arbId, double params[4], double scalar)
{
	short p0,p1,p2,p3;
	int errCode = ReceiveCAN(arbId);

	p0 = (short)((uint8_t)(_cache));
	p0 <<= 8;
	p0 |= (short)((uint8_t)(_cache >> 0x08));

	p1 = (short)((uint8_t)(_cache >> 0x10));
	p1 <<= 8;
	p1 |= (short)((uint8_t)(_cache >> 0x18));

	p2 = (short)((uint8_t)(_cache >> 0x20));
	p2 <<= 8;
	p2 |= (short)((uint8_t)(_cache >> 0x28));

	p3 = (short)((uint8_t)(_cache >> 0x30));
	p3 <<= 8;
	p3 |= (short)((uint8_t)(_cache >> 0x38));

	/* always give caller the latest */
	params[0] = p0 * scalar;
	params[1] = p1 * scalar;
	params[2] = p2 * scalar;
	params[3] = p3 * scalar;


	return errCode;
}

int PigeonImu::GetThreeParam20(int arbId, double params[3], double scalar)
{
	int p1, p2, p3;

	int errCode = ReceiveCAN(arbId);

	uint8_t p1_h8 = (uint8_t)_cache;
	uint8_t p1_m8 = (uint8_t)(_cache >> 8);
	uint8_t p1_l4 = (uint8_t)(_cache >> 20);

	uint8_t p2_h4 = (uint8_t)(_cache >> 16);
	uint8_t p2_m8 = (uint8_t)(_cache >> 24);
	uint8_t p2_l8 = (uint8_t)(_cache >> 32);

	uint8_t p3_h8 = (uint8_t)(_cache >> 40);
	uint8_t p3_m8 = (uint8_t)(_cache >> 48);
	uint8_t p3_l4 = (uint8_t)(_cache >> 60);

	p1_l4 &= 0xF;
	p2_h4 &= 0xF;
	p3_l4 &= 0xF;

	p1 = p1_h8;
	p1 <<= 8;
	p1 |= p1_m8;
	p1 <<= 4;
	p1 |= p1_l4;
	p1 <<= (32 - 20);
	p1 >>= (32 - 20);

	p2 = p2_h4;
	p2 <<= 8;
	p2 |= p2_m8;
	p2 <<= 8;
	p2 |= p2_l8;
	p2 <<= (32 - 20);
	p2 >>= (32 - 20);

	p3 = p3_h8;
	p3 <<= 8;
	p3 |= p3_m8;
	p3 <<= 4;
	p3 |= p3_l4;
	p3 <<= (32 - 20);
	p3 >>= (32 - 20);

	params[0] = p1 * scalar;
	params[1] = p2 * scalar;
	params[2] = p3 * scalar;

	return errCode;
}
//----------------------- Strongly typed Signal decoders  -----------------------//
int PigeonImu::Get6dQuaternion(double wxyz[4])
{
	int errCode = GetFourParam16(COND_STATUS_10, wxyz, 1.0 / 16384.);
	return HandleError(errCode);
}
int PigeonImu::GetYawPitchRoll(double ypr[3])
{
	int errCode = GetThreeParam20(COND_STATUS_9, ypr, (360. / 8192.));
	return HandleError(errCode);
}
int PigeonImu::GetAccumGyro(double xyz_deg[3])
{
	int errCode = GetThreeParam20(COND_STATUS_11, xyz_deg, (360. / 8192.));
	return HandleError(errCode);
}
/**
 *  @return compass heading [0,360) degrees.
 */
double PigeonImu::GetAbsoluteCompassHeading()
{
	int32_t raw;
	double retval;
	int errCode = ReceiveCAN(COND_STATUS_2);

	uint8_t  m8 =  (_cache >> 0x30) & 0xFF;
	uint8_t  l8 =  (_cache >> 0x38) & 0xFF;

	raw = m8;
	raw <<= 8;
	raw |= l8;
	raw &= 0x1FFF;

	retval = raw * (360. / 8192.);

	HandleError(errCode);
	return retval;
}
/**
 *  @return continuous compass heading [-23040, 23040) degrees.
 *  Use SetCompassHeading to modify the wrap-around portion.
 */
double PigeonImu::GetCompassHeading()
{
	int32_t raw;
	double retval;
	int errCode = ReceiveCAN(COND_STATUS_2);
	uint8_t  h4 =  (_cache >> 0x28) & 0xF;
	uint8_t  m8 =  (_cache >> 0x30) & 0xFF;
	uint8_t  l8 =  (_cache >> 0x38) & 0xFF;

	raw = h4;
	raw <<= 8;
	raw |= m8;
	raw <<= 8;
	raw |= l8;
	raw <<= (32 - 20);
	raw >>= (32 - 20);

	retval = raw * (360. / 8192.);

	HandleError(errCode);
	return retval;
}
/**
 * @return field strength in Microteslas (uT).
 */
double PigeonImu::GetCompassFieldStrength()
{
	double magnitudeMicroTeslas;
	int16_t words[2];
	int errCode = GetTwoParam16(COND_STATUS_2, words);
	magnitudeMicroTeslas = words[1] * (0.15f);
	HandleError(errCode);
	return magnitudeMicroTeslas;
}

double PigeonImu::GetTemp(const uint64_t & statusFrame)
{
	uint8_t H = (uint8_t)(statusFrame >> 0);
	uint8_t L = (uint8_t)(statusFrame >> 8);
	int raw = 0;
	raw |= H;
	raw <<= 8;
	raw |= L;
	double tempC = raw * (1.0f / 256.0f);
	return tempC;
}
double PigeonImu::GetTemp()
{
	int errCode = ReceiveCAN(COND_STATUS_1);
	double tempC = GetTemp(_cache);
	HandleError(errCode);
	return tempC;
}
PigeonImu::PigeonState PigeonImu::GetState(int errCode, const uint64_t & statusFrame)
{
	PigeonState retval = PigeonState::NoComm;

	if(errCode != 0){
		/* bad frame */
	} else {
		/* good frame */
		uint8_t b2 = (uint8_t)(statusFrame >> 0x10);

		MotionDriverState mds = (MotionDriverState)(b2 & 0x1f);
		switch (mds) {
			case Error:
			case Init0:
			case WaitForPowerOff:
			case ConfigAg:
			case SelfTestAg:
			case StartDMP:
			case ConfigCompass_0:
			case ConfigCompass_1:
			case ConfigCompass_2:
			case ConfigCompass_3:
			case ConfigCompass_4:
			case ConfigCompass_5:
			case SelfTestCompass:
			case WaitForGyroStable:
			case AdditionalAccelAdjust:
				retval = PigeonState::Initializing;
				break;
			case Idle:
				retval = PigeonState::Ready;
				break;
			case Calibration:
			case LedInstrum:
				retval = PigeonState::UserCalibration;
				break;
			default:
				retval = PigeonState::Initializing;
				break;
		}
	}
	return retval;
}
PigeonImu::PigeonState PigeonImu::GetState()
{
	int errCode = ReceiveCAN(COND_STATUS_1);
	PigeonState retval = PigeonImu::GetState(errCode, _cache);
	HandleError(errCode);
	return retval;
}
/// <summary>
/// How long has Pigeon been running
/// </summary>
/// <param name="timeSec"></param>
/// <returns></returns>
uint32_t PigeonImu::GetUpTime()
{
	/* repoll status frame */
	int errCode = ReceiveCAN(COND_STATUS_1);
	uint32_t timeSec = (uint8_t)(_cache >> 56);
	HandleError(errCode);
	return timeSec;
}

int PigeonImu::GetRawMagnetometer(int16_t rm_xyz[3])
{
	int errCode = GetThreeParam16(RAW_STATUS_4, rm_xyz);
	return HandleError(errCode);
}
int PigeonImu::GetBiasedMagnetometer(int16_t bm_xyz[3])
{
	int errCode = GetThreeParam16(BIASED_STATUS_4, bm_xyz);
	return HandleError(errCode);
}
int PigeonImu::GetBiasedAccelerometer(int16_t ba_xyz[3])
{
	int errCode = GetThreeParam16(BIASED_STATUS_6, ba_xyz);
	return HandleError(errCode);
}
int PigeonImu::GetRawGyro(double xyz_dps[3])
{
	int errCode = GetThreeParam16(BIASED_STATUS_2, xyz_dps, 1.0f / 16.4f);
	return HandleError(errCode);
}

int PigeonImu::GetAccelerometerAngles(double tiltAngles[3])
{
	int errCode = GetThreeBoundedAngles(COND_STATUS_3, tiltAngles);
	return HandleError(errCode);
}
/**
 * @param status 	object reference to fill with fusion status flags.  
 *					Caller may omit this parameter if flags are not needed.
 * @return fused heading in degrees.
 */
double PigeonImu::GetFusedHeading(FusionStatus & status)
{
	bool bIsFusing, bIsValid;
	double temp[3];
	double fusedHeading;

	int errCode = GetThreeParam20(COND_STATUS_6, temp, 360. / 8192.);
	fusedHeading = temp[0];
	uint8_t b2 = (uint8_t)(_cache >> 16);

	std::string description;

	if (errCode != 0) {
		bIsFusing = false;
		bIsValid = false;
		description = "Could not receive status frame.  Check wiring and web-config.";
	} else {
		int flags = (b2) & 7;
		if (flags == 7) {
			bIsFusing = true;
		} else {
			bIsFusing = false;
		}

		if ((b2 & 0x8) == 0) {
			bIsValid = false;
		} else {
			bIsValid = true;
		}

		if(bIsValid == false) {
			description = "Fused Heading is not valid.";
		}else if(bIsFusing == false){
			description = "Fused Heading is valid.";
		} else {
			description = "Fused Heading is valid and is fusing compass.";
		}
	}

	/* fill caller's struct */
	status.heading = fusedHeading;
	status.bIsFusing = bIsFusing;
	status.bIsValid = bIsValid;
	status.description = description;
	status.lastError = errCode;

	HandleError(errCode);
	return fusedHeading;
}
double PigeonImu::GetFusedHeading()
{
	FusionStatus temp;
	return GetFusedHeading(temp);
}
//----------------------- Startup/Reset status -----------------------//
/**
 * Polls status5 frame, which is only transmitted on motor controller boot.
 * @return error code.
 */
int PigeonImu::GetStartupStatus()
{
	int errCode = ReceiveCAN(COND_STATUS_5, false);
	if (errCode == 0) {
		uint8_t H, L;
		int raw;
		/* frame has been received, therefore motor contorller has reset at least once */
		_resetStats.hasReset = true;
		/* reset count */
		H = (uint8_t)(_cache >> 0);
		L = (uint8_t)(_cache >> 8);
		raw = H << 8 | L;
		_resetStats.resetCount = (int) raw;
		/* reset flags */
		H = (uint8_t)(_cache >> 16);
		L = (uint8_t)(_cache >> 24);
		raw = H << 8 | L;
		_resetStats.resetFlags = (int) raw;
		/* firmVers */
		H = (uint8_t)(_cache >> 32);
		L = (uint8_t)(_cache >> 40);
		raw = H << 8 | L;
		_resetStats.firmVers = (int) raw;
	}
	return errCode;
}
uint32_t PigeonImu::GetResetCount()
{
	/* repoll status frame */
	int errCode = GetStartupStatus();
	uint32_t retval = _resetStats.resetCount;
	HandleError(errCode);
	return retval;
}
uint32_t PigeonImu::GetResetFlags()
{
	/* repoll status frame */
	int errCode = GetStartupStatus();
	uint32_t retval = _resetStats.resetFlags;
	HandleError(errCode);
	return retval;
}
/**
 * @param param holds the version of the Talon.  Talon must be powered cycled at least once.
 */
uint32_t PigeonImu::GetFirmVers()
{
	/* repoll status frame */
	int errCode = GetStartupStatus();
	uint32_t retval = _resetStats.firmVers;
	HandleError(errCode);
	return retval;
}
/**
 * @return true iff a reset has occured since last call.
 */
bool PigeonImu::HasResetOccured()
{
	/* repoll status frame, ignore return since hasReset is explicitly tracked */
	GetStartupStatus();
	/* get-then-clear reset flag */
	bool retval = _resetStats.hasReset;
	_resetStats.hasReset = false;
	return retval;
}

/* static */ std::string PigeonImu::ToString(PigeonState state)
{
	std::string retval = "Unknown";
	switch (state) {
	case Initializing:
		return "Initializing";
	case Ready:
		return "Ready";
	case UserCalibration:
		return "UserCalibration";
	case NoComm:
		return "NoComm";
	}
	return retval;
}
/* static */ std::string PigeonImu::ToString(CalibrationMode cm)
{
	std::string retval = "Unknown";
	switch (cm) {
	case BootTareGyroAccel:
		return "BootTareGyroAccel";
	case Temperature:
		return "Temperature";
	case Magnetometer12Pt:
		return "Magnetometer12Pt";
	case Magnetometer360:
		return "Magnetometer360";
	case Accelerometer:
		return "Accelerometer";
	}
	return retval;
}

#endif // CTR_EXCLUDE_WPILIB_CLASSES
