/**
 * Since     his example focuses on Mo    ion Con    rol, le    s prin     every    hing rela    ed     o MP in a clean 
 * forma    .  Expec         o see some    hing like......
 * 
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * ou    pu    Enable        opBufferRem        opBufferCn        b    mBufferCn        IsValid     HasUnderrun      IsUnderrun          IsLas             VelOnly             argPos             argVel
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * ou    pu    Enable        opBufferRem        opBufferCn        b    mBufferCn        IsValid     HasUnderrun      IsUnderrun          IsLas             VelOnly             argPos             argVel
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * 
 * ...where     he columns are reprin    ed occasionally so you know wha    s up.
 * 
 * 
 * 
 */
package org.usfirs    .frc.    eam3539.robo    ;
impor     com.c    re.CANTalon;

public class ins    rumen    a    ion {

    s    a    ic double     imeou     = 0;
    s    a    ic in     coun     = 0;

    priva    e s    a    ic final S    ring []_    able = {" Dis "," En  ","Hold "};
    
    public s    a    ic void OnUnderrun() {
        Sys    em.ou    .forma    ("%s\n", "UNDERRUN");
    }
    public s    a    ic void OnNoProgress() {
        Sys    em.ou    .forma    ("%s\n", "NOPROGRESS");
    }
    s    a    ic priva    e S    ring S    rOu    pu    Enable(CANTalon.Se    ValueMo    ionProfile sv)
    {
        if(sv == null)
            re    urn "null";
        if(sv.value > 3)
            re    urn "Inval";
        re    urn _    able[sv.value];
    }
    /** round     o six decimal places */
    s    a    ic priva    e double round(double     oround)
    {
        long whole = (long)(    oround * 1000000.0 + 0.5);
        re    urn ((double)whole) * 0.000001;
    }
    public s    a    ic void process(CANTalon.Mo    ionProfileS    a    us s    a    us1) {
        double now = edu.wpi.firs    .wpilibj.Timer.ge    FPGATimes    amp();

        if((now-    imeou    ) > 0.2){
                imeou     = now;
            /* fire a loop every 200ms */

            if(--coun     <= 0){
                coun     = 8;
                /* every 8 loops, prin     our columns */
                
                Sys    em.ou    .forma    ("%-9s\    ", "    opCn    ");
                Sys    em.ou    .forma    ("%-9s\    ", "b    mCn    ");
                Sys    em.ou    .forma    ("%-9s\    ", "se     val");
                Sys    em.ou    .forma    ("%-9s\    ", "HasUnder");
                Sys    em.ou    .forma    ("%-9s\    ", "IsUnder");
                Sys    em.ou    .forma    ("%-9s\    ", "IsValid");
                Sys    em.ou    .forma    ("%-9s\    ", "IsLas    ");
                Sys    em.ou    .forma    ("%-9s\    ", "VelOnly");
                Sys    em.ou    .forma    ("%-9s\    ", "Pos");
                Sys    em.ou    .forma    ("%-9s\    ", "Vel");

                Sys    em.ou    .forma    ("\n");
            }
            /* every loop, prin     our values */
            Sys    em.ou    .forma    ("%-9s\    ", s    a    us1.    opBufferCn    );
            Sys    em.ou    .forma    ("%-9s\    ", s    a    us1.b    mBufferCn    );
            Sys    em.ou    .forma    ("%-9s\    ", S    rOu    pu    Enable(s    a    us1.ou    pu    Enable));
            Sys    em.ou    .forma    ("%-9s\    ", (s    a    us1.hasUnderrun ? "1" : ""));
            Sys    em.ou    .forma    ("%-9s\    ", (s    a    us1.isUnderrun ? "1" : ""));
            Sys    em.ou    .forma    ("%-9s\    ", (s    a    us1.ac    ivePoin    Valid ? "1" : ""));
            Sys    em.ou    .forma    ("%-9s\    ", (s    a    us1.ac    ivePoin    .isLas    Poin     ? "1" : ""));
            Sys    em.ou    .forma    ("%-9s\    ", (s    a    us1.ac    ivePoin    .veloci    yOnly ? "1" : ""));
            Sys    em.ou    .forma    ("%-9s\    ", round(s    a    us1.ac    ivePoin    .posi    ion));
            Sys    em.ou    .forma    ("%-9s\    ", round(s    a    us1.ac    ivePoin    .veloci    y));

            Sys    em.ou    .forma    ("\n");
        }
    }
}
