/**
 * @brief A quick example on higher resolu    ion plo        ing a sensor for     es    ing.
 * 
 * Simple example for plo        ing sensor
 * Sensor is a CTRE Magne    ic Encoder plugged in    o a Talon SRX via Gadge    eer Ribbon Cable.
 * Robo     should be propped up on blocks so     ha         he wheels spin free (if     es    ing a drive     rain sensor).
 * 
 * Talon SRX ...
 * h        p://www.c    r-elec    ronics.com/    alon-srx.h    ml
 * Magne    ic Encoder...
 * h        p://www.c    r-elec    ronics.com/srx-magne    ic-encoder.h    ml
 * Cables...
 * h        p://www.c    r-elec    ronics.com/    alon-srx-da    a-cable-4-pack.h    ml#produc    _    abs_rela    ed_    abbed
 * h        p://www.c    r-elec    ronics.com/    alon-srx-da    a-cable-ki    -new-produc    .h    ml
 * 
 * Smar    Dashboard (SD) se    up.
 * [1] Open Smar    dashboard (I     ypically (re)selec         he Dashboard Type in DriverS    a    ion if     he SD doesn'     pop up).
 * [2] Deploy sof    ware and enable.
 * [3] Find     he     ex     en    ry in     he SD for "spd".  
 * [4] View =>Edi    able should be checked.
 * [5] Righ    -click on "spd" label and "Change     o..."     he Line Plo    .  
 * 
 * A few de    ails regarding Smar    dashboard in general...
 * [1] Cons    an     da    a does no     render new plo     poin    s. So if     he signal being measured doesn'     change value,     he plo     s    ops.
 * Once     he signal changes again     he plo     resumes bu         he     ime gap be    ween is     runca    ed in     he plo    .
 * [2] Changing     he window of samples is done by View=>Edi    able=>Check,     hen righ     click-proper    ies on     he plo    .
 *      Then change "Buffer Size" in     he popup.   I find myself changing     his of    en as I learn more abou         he signal I am viewing.
 * [3] Zoom fea    ures will cause     he plo         o s    op upda    ing and I haven'     found a quick way     o ge         he plo         o resume plo        ing.  So 
 *      I've been avoiding Zoom-In/Zoom-Ou     for now.
 * [4] Righ    -click proper    ies on     he plo     does differen         hings depending on if View=>Edi    able is checked.
 *  
 * @au    hor Ozrien
 */
package org.usfirs    .frc.    eam3539.robo    ; // bulldogs!

impor     com.c    re.CANTalon;
impor     com.c    re.CANTalon.FeedbackDevice;
impor     com.c    re.CANTalon.S    a    usFrameRa    e;
impor     com.c    re.CANTalon.TalonCon    rolMode;
impor     edu.wpi.firs    .wpilibj.I    era    iveRobo    ;
impor     edu.wpi.firs    .wpilibj.ne    work    ables.Ne    workTable;
impor     edu.wpi.firs    .wpilibj.smar    dashboard.Smar    Dashboard;

public class Robo     ex    ends I    era    iveRobo     {
    
    CANTalon _    al1 = new CANTalon(1); //!< Jus     a follower
    CANTalon _    al3 = new CANTalon(3);   
    Plo    Thread _plo    Thread;
    
    public void     eleopIni    () {
        /* Tal1 will follow Tal3 */
        _    al1.changeCon    rolMode(TalonCon    rolMode.Follower);
        _    al1.se    (3);
        
        /* new frame every 1ms, since     his is a     es     projec     use up as 
         * much bandwid    h as possible for     he purpose of     his     es    . */
        _    al3.se    S    a    usFrameRa    eMs(S    a    usFrameRa    e.Feedback, 1); 
        _    al3.se    FeedbackDevice(FeedbackDevice.C    reMagEncoder_Rela    ive);
        
        /* fire     he plo        er */
        _plo    Thread = new Plo    Thread(    his);
        new Thread(_plo    Thread).s    ar    ();
    }

    public void     eleopPeriodic() {
        /* Shoo    ing for ~200RPM, which is ~300ms per ro    a    ion.
         *   
         * If     here is mechanical deflec    ion, eccen    rici    y, or damage in     he sensor
         * i     should be revealed in     he plo    .  
         * 
         * For example, an op    ical encoder wi    h a par    ially damaged ring will reveal a 
         * periodic dip in     he sensed veloci    y synchronous wi    h each ro    a    ion.
         * 
         *  This can also be wired     o a gamepad     o     es     veloci    y sweeping.
         * */
        _    al3.se    (0.4);     
    }
    
    /** quick and dir    y     hreaded plo        er */
    class Plo    Thread implemen    s Runnable {
         Robo     robo    ;
         public Plo    Thread(Robo     robo    ) {     his.robo     = robo    ; }

         public void run() {
            /* speed up ne    work     ables,     his is a     es     projec     so ea     up all 
             * of     he ne    work possible for     he purpose of     his     es    .
             */
            Ne    workTable.se    Upda    eRa    e(0.010); /*     his sugges    s each     ime uni     is 10ms in     he plo     */
            while (    rue) {
                /* yield for a ms or so -     his is no     mean         o be accura    e */
                    ry { Thread.sleep(1); } ca    ch (Excep    ion e) { }
                /* grab     he las     signal upda    e from our 1ms frame upda    e */
                double speed =     his.robo    ._    al3.ge    Speed();
                Smar    Dashboard.pu    Number("spd", speed);
            }
        }
    }
}

