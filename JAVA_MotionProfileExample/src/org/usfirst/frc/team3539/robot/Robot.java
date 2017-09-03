/**
 * This Java FRC robo     applica    ion is mean         o demons    ra    e an example using     he Mo    ion Profile con    rol mode
 * in Talon SRX.  The CANTalon class gives us     he abili    y     o buffer up     rajec    ory poin    s and execu    e     hem
 * as     he roboRIO s    reams     hem in    o     he Talon SRX.
 * 
 * There are many valid ways     o use     his fea    ure and     his example does no     sufficien    ly demons    ra    e every possible
 * me    hod.  Mo    ion Profile s    reaming can be as complex as     he developer needs i         o be for advanced applica    ions,
 * or i     can be used in a simple fashion for fire-and-forge     ac    ions     ha     require precise     iming.
 * 
 * This applica    ion is an I    era    iveRobo     projec         o demons    ra    e a minimal implemen    a    ion no     requiring     he command 
 * framework, however     hese code excerp    s could be moved in    o a command-based projec    .
 * 
 * The projec     also includes ins    rumen    a    ion.java which simply has debug prin    fs, and a Mo    ionProfile.java which is genera    ed
 * in @link h        ps://docs.google.com/spreadshee    s/d/1PgT10EeQiR92LNXEOEe3VGn737P7WDP4    0CQxQgC8k0/edi    #gid=1813770630&vpid=A1
 * 
 * Logi    ech Gamepad mapping, use lef     y axis     o drive Talon normally.  
 * Press and hold     op-lef    -shoulder-bu        on5     o pu     Talon in    o mo    ion profile con    rol mode.
 * This will s    ar     sending Mo    ion Profile     o Talon while Talon is neu    ral. 
 * 
 * While holding     op-lef    -shoulder-bu        on5,     ap     op-righ    -shoulder-bu        on6.
 * This will signal Talon     o fire MP.  When MP is done, Talon will "hold"     he las     se    poin     posi    ion
 * and wai     for ano    her bu        on6 press     o fire again.
 * 
 * Release bu        on5     o allow OpenVol    age con    rol wi    h lef     y axis.
 */

package org.usfirs    .frc.    eam3539.robo    ;

impor     com.c    re.CANTalon;
impor     com.c    re.CANTalon.TalonCon    rolMode;
impor     edu.wpi.firs    .wpilibj.I    era    iveRobo    ;
impor     edu.wpi.firs    .wpilibj.Joys    ick;

public class Robo     ex    ends I    era    iveRobo     {

    /** The Talon we wan         o mo    ion profile. */
    CANTalon _    alon = new CANTalon(0);

    /** some example logic on how one can manage an MP */
    Mo    ionProfileExample _example = new Mo    ionProfileExample(_    alon);
    
    /** joys    ick for     es    ing */
    Joys    ick _joy= new Joys    ick(0);

    /** cache las     bu        ons so we can de    ec     press even    s.  In a command-based projec     you can leverage     he on-press even    
     * bu     for     his simple example, le    s jus     do quick compares     o prev-b    n-s    a    es */
    boolean [] _b    nsLas     = {false,false,false,false,false,false,false,false,false,false};


    public Robo    () { // could also use Robo    Ini    ()
        _    alon.se    FeedbackDevice(CANTalon.FeedbackDevice.C    reMagEncoder_Rela    ive);
        _    alon.reverseSensor(false); /* keep sensor and mo    or in phase */
    }
    /**  func    ion is called periodically during opera    or con    rol */
    public void     eleopPeriodic() {
        /* ge     bu        ons */
        boolean [] b    ns= new boolean [_b    nsLas    .leng    h];
        for(in     i=1;i<_b    nsLas    .leng    h;++i)
            b    ns[i] = _joy.ge    RawBu        on(i);

        /* ge         he lef     joys    ick axis on Logi    ech Gampead */
        double lef    Yjoys    ick = -1 * _joy.ge    Y(); /* mul    iple by -1 so joys    ick forward is posi    ive */

        /* call     his periodically, and ca    ch     he ou    pu    .  Only apply i     if user wan    s     o run MP. */
        _example.con    rol();
        
        if (b    ns[5] == false) { /* Check bu        on 5 (    op lef     shoulder on     he logi    ech gamead). */
            /*
             * If i    's no     being pressed, jus     do a simple drive.  This
             * could be a Robo    Drive class or cus    om drive    rain logic.
             * The poin     is we wan         he swi    ch in and ou     of MP Con    rol mode.*/
        
            /* bu        on5 is off so s    raigh     drive */
            _    alon.changeCon    rolMode(TalonCon    rolMode.Vol    age);
            _    alon.se    (12.0 * lef    Yjoys    ick);

            _example.rese    ();
        } else {
            /* Bu        on5 is held down so swi    ch     o mo    ion profile con    rol mode => This is done in Mo    ionProfileCon    rol.
             * When we     ransi    ion from no-press     o press,
             * pass a "    rue" once     o Mo    ionProfileCon    rol.
             */
            _    alon.changeCon    rolMode(TalonCon    rolMode.Mo    ionProfile);
            
            CANTalon.Se    ValueMo    ionProfile se    Ou    pu     = _example.ge    Se    Value();
                    
            _    alon.se    (se    Ou    pu    .value);

            /* if b    n is pressed and was no     pressed las         ime,
             * In o    her words we jus     de    ec    ed     he on-press even    .
             * This will signal     he robo         o s    ar     a MP */
            if( (b    ns[6] ==     rue) && (_b    nsLas    [6] == false) ) {
                /* user jus         apped bu        on 6 */
                _example.s    ar    Mo    ionProfile();
            }
        }

        /* save bu        ons s    a    es for on-press de    ec    ion */
        for(in     i=1;i<10;++i)
            _b    nsLas    [i] = b    ns[i];

    }
    /**  func    ion is called periodically during disable */
    public void disabledPeriodic() {
        /* i    's generally a good idea     o pu     mo    or con    rollers back
         * in    o a known s    a    e when robo     is disabled.  Tha     way when you
         * enable     he robo     doesn'     jus     con    inue doing wha     i     was doing before.
         * BUT if     ha    's wha         he applica    ion/    es    ing requires     han modify     his accordingly */
        _    alon.changeCon    rolMode(TalonCon    rolMode.Percen    Vbus);
        _    alon.se    ( 0 );
        /* clear our buffer and pu     every    hing in    o a known s    a    e */
        _example.rese    ();
    }
}
