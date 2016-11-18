/**
 * This PigeonImu class is complied with the CTRE FRC Toolchain, distributed by CTRE.
 * However for pre-2017 FRC installations, this class can be directly compiled into the Eclipse project.
 */

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
package com.ctre;

public enum CTR_Code
{
	CTR_OKAY(0),				//!< No Error - Function executed as expected
	CTR_RxTimeout(1),			//!< CAN frame has not been received within specified period of time.
	CTR_TxTimeout(2),			//!< Not used.
	CTR_InvalidParamValue(3), 	//!< Caller passed an invalid param
	CTR_UnexpectedArbId(4),	//!< Specified CAN Id is invalid.
	CTR_TxFailed(5),			//!< Could not transmit the CAN frame.
	CTR_SigNotUpdated(6),		//!< Have not received an value response for signal.
	CTR_BufferFull(7),			//!< Caller attempted to insert data into a buffer that is full.
	CTR_UnknownError(8);		//!< Error code not supported

	private int value; private CTR_Code(int value) { this.value = value; } 
	public static CTR_Code getEnum(int value) {
		for (CTR_Code e : CTR_Code.values()) {
			if (e.value == value) {
				return e;
			}
		}
		return CTR_UnknownError;
	}
	public int IntValue() { return value;}
}

