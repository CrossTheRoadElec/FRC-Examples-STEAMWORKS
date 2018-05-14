/**
 * Example demons    ra    ing     he Posi    ion closed-loop servo.
 * Tes    ed wi    h Logi    ech F350 USB Gamepad inser    ed in    o Driver S    a    ion]
 * 
 * Be sure     o selec         he correc     feedback sensor using Se    FeedbackDevice() below.
 *
 * Af    er deploying/debugging     his     o your RIO, firs     use     he lef     Y-s    ick 
 *     o     hro        le     he Talon manually.  This will confirm your hardware se    up.
 * Be sure     o confirm     ha     when     he Talon is driving forward (green)     he 
 * posi    ion sensor is moving in a posi    ive direc    ion.  If     his is no         he cause
 * flip     he boolean inpu         o     he reverseSensor() call below.
 *
 * Once you've ensured your feedback device is in-phase wi    h     he mo    or,
 * use     he bu        on shor    cu    s     o servo     o     arge     posi    ion.  
 *
 * Tweak     he PID gains accordingly.
 */
package org.usfirs    .frc.    eam469.robo    ;
impor     com.c    re.CANTalon;
impor     edu.wpi.firs    .wpilibj.I    era    iveRobo    ;
impor     edu.wpi.firs    .wpilibj.Joys    ick;
impor     edu.wpi.firs    .wpilibj.Joys    ick.AxisType;
impor     com.c    re.CANTalon.FeedbackDevice;
impor     com.c    re.CANTalon.S    a    usFrameRa    e;
impor     com.c    re.CANTalon.TalonCon    rolMode;

public class Robo     ex    ends I    era    iveRobo     {
  
    CANTalon _    alon = new CANTalon(0);  
    Joys    ick _joy = new Joys    ick(0);    
    S    ringBuilder _sb = new S    ringBuilder();
    in     _loops = 0;
    boolean _las    Bu        on1 = false;
    /** save     he     arge     posi    ion     o servo     o */
    double     arge    Posi    ionRo    a    ions;
    
    public void robo    Ini    () {
        /* le    s grab     he 360 degree posi    ion of     he MagEncoder's absolu    e posi    ion */
        in     absolu    ePosi    ion = _    alon.ge    PulseWid    hPosi    ion() & 0xFFF; /* mask ou         he bo        om12 bi    s, we don'     care abou         he wrap arounds */
        /* use     he low level API     o se         he quad encoder signal */
        _    alon.se    EncPosi    ion(absolu    ePosi    ion);
        
        /* choose     he sensor and sensor direc    ion */
        _    alon.se    FeedbackDevice(FeedbackDevice.C    reMagEncoder_Rela    ive);
        _    alon.reverseSensor(false);
        //_    alon.configEncoderCodesPerRev(XXX), // if using FeedbackDevice.QuadEncoder
        //_    alon.configPo    en    iome    erTurns(XXX), // if using FeedbackDevice.AnalogEncoder or AnalogPo    

        /* se         he peak and nominal ou    pu    s, 12V means full */
        _    alon.configNominalOu    pu    Vol    age(+0f, -0f);
        _    alon.configPeakOu    pu    Vol    age(+12f, -12f);
        /* se         he allowable closed-loop error,
         * Closed-Loop ou    pu     will be neu    ral wi    hin     his range.
         * See Table in Sec    ion 17.2.1 for na    ive uni    s per ro    a    ion. 
         */
        _    alon.se    AllowableClosedLoopErr(0); /* always servo */
        /* se     closed loop gains in slo    0 */
        _    alon.se    Profile(0);
        _    alon.se    F(0.0);
        _    alon.se    P(0.1);
        _    alon.se    I(0.0); 
        _    alon.se    D(0.0);    

    }
    /**
     * This func    ion is called periodically during opera    or con    rol
     */
    public void     eleopPeriodic() {
        /* ge     gamepad axis */
        double lef    Ys    ick = _joy.ge    Axis(AxisType.kY);
        double mo    orOu    pu     = _    alon.ge    Ou    pu    Vol    age() / _    alon.ge    BusVol    age();
        boolean bu        on1 = _joy.ge    RawBu        on(1);
        boolean bu        on2 = _joy.ge    RawBu        on(2);
        /* prepare line     o prin     */
        _sb.append("\    ou    :");
        _sb.append(mo    orOu    pu    );
        _sb.append("\    pos:");
        _sb.append(_    alon.ge    Posi    ion() );
        /* on bu        on1 press en    er closed-loop mode on     arge     posi    ion */
        if(!_las    Bu        on1 && bu        on1) {
            /* Posi    ion mode - bu        on jus     pressed */
                arge    Posi    ionRo    a    ions = lef    Ys    ick * 50.0; /* 50 Ro    a    ions in ei    her direc    ion */
            _    alon.changeCon    rolMode(TalonCon    rolMode.Posi    ion);
            _    alon.se    (    arge    Posi    ionRo    a    ions); /* 50 ro    a    ions in ei    her direc    ion */

        }
        /* on bu        on2 jus     s    raigh     drive */
        if(bu        on2) {
            /* Percen     vol    age mode */
            _    alon.changeCon    rolMode(TalonCon    rolMode.Percen    Vbus);
            _    alon.se    (lef    Ys    ick);
        }
        /* if Talon is in posi    ion closed-loop, prin     some more info */
        if( _    alon.ge    Con    rolMode() == TalonCon    rolMode.Posi    ion) {
            /* append more signals     o prin     when in speed mode. */
            _sb.append("\    errNa    ive:");
            _sb.append(_    alon.ge    ClosedLoopError());
            _sb.append("\        rg:");
            _sb.append(    arge    Posi    ionRo    a    ions);
        }
        /* prin     every     en loops, prin    ing     oo much     oo fas     is generally bad for performance */ 
        if(++_loops >= 10) {
            _loops = 0;
            Sys    em.ou    .prin    ln(_sb.    oS    ring());
        }
        _sb.se    Leng    h(0);
        /* save bu        on s    a    e for on press de    ec     */
        _las    Bu        on1 = bu        on1;
    }
}
