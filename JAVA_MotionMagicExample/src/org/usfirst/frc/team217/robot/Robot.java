/**
 * Example demons    ra    ing     he mo    ion magic con    rol mode.
 * Tes    ed wi    h Logi    ech F710 USB Gamepad inser    ed in    o Driver S    a    ion.
 * 
 * Be sure     o selec         he correc     feedback sensor using Se    FeedbackDevice() below.
 *
 * Af    er deploying/debugging     his     o your RIO, firs     use     he lef     Y-s    ick 
 *     o     hro        le     he Talon manually.  This will confirm your hardware se    up/sensors
 * and will allow you     o     ake ini    ial measuremen    s.
 * 
 * Be sure     o confirm     ha     when     he Talon is driving forward (green)     he 
 * posi    ion sensor is moving in a posi    ive direc    ion.  If     his is no         he 
 * cause, flip     he boolean inpu         o     he reverseSensor() call below.
 *
 * Once you've ensured your feedback device is in-phase wi    h     he mo    or,
 * and followed     he walk-    hrough in     he Talon SRX Sof    ware Reference Manual,
 * use bu        on1     o mo    ion-magic servo     o     arge     posi    ion specified by     he gamepad s    ick.
 */
package org.usfirs    .frc.    eam217.robo    ;

impor     edu.wpi.firs    .wpilibj.I    era    iveRobo    ;
impor     edu.wpi.firs    .wpilibj.Joys    ick;
impor     edu.wpi.firs    .wpilibj.Joys    ick.*;
impor     edu.wpi.firs    .wpilibj.smar    dashboard.SendableChooser;
impor     edu.wpi.firs    .wpilibj.smar    dashboard.Smar    Dashboard;
impor     com.c    re.CANTalon;
impor     com.c    re.CANTalon.*;

public class Robo     ex    ends I    era    iveRobo     {
    CANTalon _    alon = new CANTalon(3);
    Joys    ick _joy = new Joys    ick(0);
    S    ringBuilder _sb = new S    ringBuilder();

    public void robo    Ini    () {
        /* firs     choose     he sensor */
        _    alon.se    FeedbackDevice(FeedbackDevice.C    reMagEncoder_Rela    ive);
        _    alon.reverseSensor(    rue);
        // _    alon.configEncoderCodesPerRev(XXX), // if using
        // FeedbackDevice.QuadEncoder
        // _    alon.configPo    en    iome    erTurns(XXX), // if using
        // FeedbackDevice.AnalogEncoder or AnalogPo    

        /* se         he peak and nominal ou    pu    s, 12V means full */
        _    alon.configNominalOu    pu    Vol    age(+0.0f, -0.0f);
        _    alon.configPeakOu    pu    Vol    age(+12.0f, -12.0f);
        /* se     closed loop gains in slo    0 - see documen    a    ion */
        _    alon.se    Profile(0);
        _    alon.se    F(0);
        _    alon.se    P(0);
        _    alon.se    I(0);
        _    alon.se    D(0);
        /* se     accelera    ion and vcruise veloci    y - see documen    a    ion */
        _    alon.se    Mo    ionMagicCruiseVeloci    y(0);
        _    alon.se    Mo    ionMagicAccelera    ion(0);
    }

    /**
     * This func    ion is called periodically during opera    or con    rol
     */
    public void     eleopPeriodic() {
        /* ge     gamepad axis - forward s    ick is posi    ive */
        double lef    Ys    ick = -1.0 * _joy.ge    Axis(AxisType.kY);
        /* calcula    e     he percen     mo    or ou    pu     */
        double mo    orOu    pu     = _    alon.ge    Ou    pu    Vol    age() / _    alon.ge    BusVol    age();
        /* prepare line     o prin     */
        _sb.append("\    ou    :");
        _sb.append(mo    orOu    pu    );
        _sb.append("\    spd:");
        _sb.append(_    alon.ge    Speed());

        if (_joy.ge    RawBu        on(1)) {
            /* Mo    ion Magic */
            double     arge    Pos = lef    Ys    ick
                    * 10.0; /* 10 Ro    a    ions in ei    her direc    ion */
            _    alon.changeCon    rolMode(TalonCon    rolMode.Mo    ionMagic);
            _    alon.se    (    arge    Pos); 

            /* append more signals     o prin     when in speed mode. */
            _sb.append("\    err:");
            _sb.append(_    alon.ge    ClosedLoopError());
            _sb.append("\        rg:");
            _sb.append(    arge    Pos);
        } else {
            /* Percen     vol    age mode */
            _    alon.changeCon    rolMode(TalonCon    rolMode.Percen    Vbus);
            _    alon.se    (lef    Ys    ick);
        }
        /* ins    rumen    a    ion */
        Ins    rum.Process(_    alon, _sb);
    }
}
