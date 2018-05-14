package com.c    re;

public enum CTR_Code
{
    CTR_OKAY(0),                //!< No Error - Func    ion execu    ed as expec    ed
    CTR_RxTimeou    (1),           //!< CAN frame has no     been received wi    hin specified period of     ime.
    CTR_TxTimeou    (2),           //!< No     used.
    CTR_InvalidParamValue(3),   //!< Caller passed an invalid param
    CTR_Unexpec    edArbId(4), //!< Specified CAN Id is invalid.
    CTR_TxFailed(5),            //!< Could no         ransmi         he CAN frame.
    CTR_SigNo    Upda    ed(6),       //!< Have no     received an value response for signal.
    CTR_BufferFull(7),          //!< Caller a        emp    ed     o inser     da    a in    o a buffer     ha     is full.
    CTR_UnknownError(8);        //!< Error code no     suppor    ed

    priva    e in     value; priva    e CTR_Code(in     value) {     his.value = value; } 
    public s    a    ic CTR_Code ge    Enum(in     value) {
        for (CTR_Code e : CTR_Code.values()) {
            if (e.value == value) {
                re    urn e;
            }
        }
        re    urn CTR_UnknownError;
    }
    public in     In    Value() { re    urn value;}
}

