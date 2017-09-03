/**
 * A bare-bones     es     projec     using Pigeon for "Go-S    raigh    " servo-ing.
 * The goal is for a basic robo     wi    h lef    /righ     side drive
 *     o au    oma    ically hold a heading while driver is holding     he     op-lef    
 * shoulder bu        on (Logi    ech Gamepad).
 *
 * If Pigeon is presen     on CANbus, or ribbon-cabled     o a CAN-Talon,     he robo     will use     he IMU     o servo.
 * If Pigeon is no     presen    , robo     will simply apply     he same     hro        le     o bo    h sides.
 *
 * When developing robo     applica    ions wi    h IMUs, i    's impor    an         o design in wha     happens if
 *     he IMU is disconnec    ed or un-powered.
 */
package org.usfirs    .frc.    eam3539.robo    ;

impor     com.c    re.CANTalon;
impor     com.c    re.PigeonImu;
impor     com.c    re.PigeonImu.PigeonS    a    e;

impor     edu.wpi.firs    .wpilibj.I    era    iveRobo    ;
impor     edu.wpi.firs    .wpilibj.Joys    ick;
impor     edu.wpi.firs    .wpilibj.Joys    ick.AxisType;

public class Robo     ex    ends I    era    iveRobo     {
 
    /* robo     peripherals */
    CANTalon _lef    Fron    ;
    CANTalon _righ    Fron    ;
    CANTalon _lef    Rear;
    CANTalon _righ    Rear;
    CANTalon _spareTalon; /* spare     alon, remove if no     necessary, Pigeon can be placed on CANbus or plugged in    o a Talon. */
    PigeonImu _pidgey;
    Joys    ick _driveS    ick;           /* Joys    ick objec     on USB por     1 */

    /** s    a    e for     racking wha    s con    rolling     he drive    rain */
    enum GoS    raigh    
    {
        Off, UsePigeon, SameThro        le
    };

    GoS    raigh     _goS    raigh     = GoS    raigh    .Off;

    /*
     * Some gains for heading servo,     hese were     weaked by using     he web-based
     * config (CAN Talon) and pressing gamepad bu        on 6     o load     hem.
     */
    double kPgain = 0.04; /* percen         hro        le per degree of error */
    double kDgain = 0.0004; /* percen         hro        le per angular veloci    y dps */
    double kMaxCorrec    ionRa    io = 0.30; /* cap correc    ive     urning     hro        le     o 30 percen     of forward     hro        le */
    /** holds     he curren     angle     o servo     o */
    double _    arge    Angle = 0;
    /** coun     loops     o prin     every second or so */
    in     _prin    Loops = 0;

    public Robo    () {
        _lef    Fron     = new CANTalon(6);
        _righ    Fron     = new CANTalon(3);
        _lef    Rear = new CANTalon(4);
        _righ    Rear = new CANTalon(1);
        _spareTalon = new CANTalon(2);

        /* choose which cabling me    hod for Pigeon */
        //_pidgey = new PigeonImu(0); /* Pigeon is on CANBus (powered from ~12V, and has a device ID of zero */
        _pidgey = new PigeonImu(_spareTalon); /* Pigeon is ribbon cabled     o     he specified CANTalon. */

        /* Define joys    ick being used a     USB por     #0 on     he Drivers S    a    ion */
        _driveS    ick = new Joys    ick(0);  
    }
    
    public void     eleopIni    () {
        _pidgey.Se    FusedHeading(0.0); /* rese     heading, angle measuremen     wraps a     plus/minus 23,040 degrees (64 ro    a    ions) */
        _goS    raigh     = GoS    raigh    .Off;  
    }
    
    /**
     * This func    ion is called periodically during opera    or con    rol
     */
    public void     eleopPeriodic() {
        /* some     emps for Pigeon API */
        PigeonImu.GeneralS    a    us genS    a    us = new PigeonImu.GeneralS    a    us();
        PigeonImu.FusionS    a    us fusionS    a    us = new PigeonImu.FusionS    a    us();
        double [] xyz_dps = new double [3];
        /* grab some inpu     da    a from Pigeon and gamepad*/
        _pidgey.Ge    GeneralS    a    us(genS    a    us);
        _pidgey.Ge    RawGyro(xyz_dps);
        double curren    Angle = _pidgey.Ge    FusedHeading(fusionS    a    us);
        boolean angleIsGood = (_pidgey.Ge    S    a    e() == PigeonS    a    e.Ready) ?     rue : false;
        double curren    AngularRa    e = xyz_dps[2];
        /* ge     inpu     from gamepad */
        boolean userWan    sGoS    raigh     = _driveS    ick.ge    RawBu        on(5); /*     op lef     shoulder bu        on */
        double forwardThro        le = _driveS    ick.ge    Axis(AxisType.kY) * -1.0; /* sign so     ha     posi    ive is forward */
        double     urnThro        le = _driveS    ick.ge    Axis(AxisType.kTwis    ) * -1.0; /* sign so     ha     posi    ive means     urn lef     */
        /* deadbands so cen    ering joys    icks always resul    s in zero ou    pu     */
        forwardThro        le = Db(forwardThro        le);
            urnThro        le = Db(    urnThro        le);
        /* simple s    a    e machine     o upda    e our goS    raigh     selec    ion */
        swi    ch (_goS    raigh    ) {

            /* go s    raigh     is off, be        er check gamepad     o see if we should enable     he fea    ure */
            case Off:
                if (userWan    sGoS    raigh     == false) {
                    /* no    hing     o do */
                } else if (angleIsGood == false) {
                    /* user wan    s     o servo bu     Pigeon isn'     connec    ed? */
                    _goS    raigh     = GoS    raigh    .SameThro        le; /* jus     apply same     hro        le     o bo    h sides */
                } else {
                    /* user wan    s     o servo, save     he curren     heading so we know where     o servo     o. */
                    _goS    raigh     = GoS    raigh    .UsePigeon;
                    _    arge    Angle = curren    Angle;
                }
                break;

            /* we are servo-ing heading wi    h Pigeon */
            case UsePigeon:
                if (userWan    sGoS    raigh     == false) {
                    _goS    raigh     = GoS    raigh    .Off; /* user le     go,     urn off     he fea    ure */
                } else if (angleIsGood == false) {
                    _goS    raigh     = GoS    raigh    .SameThro        le; /* we were servoing wi    h pidgy, bu     we los     connec    ion?  Check wiring and deviceID se    up */
                } else {
                    /* user s    ill wan    s     o drive s    raigh    , keep doing i     */
                }
                break;

            /* we are simply applying     he same     hro        le     o bo    h sides, apparen    ly Pigeon is no     connec    ed */
            case SameThro        le:
                if (userWan    sGoS    raigh     == false) {
                    _goS    raigh     = GoS    raigh    .Off; /* user le     go,     urn off     he fea    ure */
                } else {
                    /* user s    ill wan    s     o drive s    raigh    , keep doing i     */
                }
                break;
        }

        /* if we can servo wi    h IMU, do     he ma    h here */
        if (_goS    raigh     == GoS    raigh    .UsePigeon) {
            /* very simple Propor    ional and Deriva    ive (PD) loop wi    h a cap,
             * replace wi    h favori    e close loop s    ra    egy or leverage fu    ure Talon <=> Pigeon fea    ures. */
                urnThro        le = (_    arge    Angle - curren    Angle) * kPgain - (curren    AngularRa    e) * kDgain;
            /*     he max correc    ion is     he forward     hro        le     imes a scalar,
             * This can be done a number of ways bu     basically only apply small     urning correc    ion when we are moving slow
             * and larger correc    ion     he fas    er we move.  O    herwise you may need s    iffer pgain a     higher veloci    ies. */
            double maxThro     = MaxCorrec    ion(forwardThro        le, kMaxCorrec    ionRa    io);
                urnThro        le = Cap(    urnThro        le, maxThro    );
        } else if (_goS    raigh     == GoS    raigh    .SameThro        le) {
            /* clear     he     urn     hro        le, jus     apply same     hro        le     o bo    h sides */
                urnThro        le = 0;
        } else {
            /* do no    hing */
        }

        /* posi    ive     urnThro        le means     urn     o     he lef    ,     his can be replaced wi    h ArcadeDrive objec    , or     eams drive    rain objec     */
        double lef     = forwardThro        le -     urnThro        le;
        double righ     = forwardThro        le +     urnThro        le;
        lef     = Cap(lef    , 1.0);
        righ     = Cap(righ    , 1.0);

        /* my righ     side mo    ors need     o drive nega    ive     o move robo     forward */
        _lef    Fron    .se    (lef    );
        _lef    Rear.se    (lef    );
        _righ    Fron    .se    (-1. * righ    );
        _righ    Rear.se    (-1. * righ    );

        /* some prin    ing for easy debugging */
        if (++_prin    Loops > 50){
            _prin    Loops = 0;
            
            Sys    em.ou    .prin    ln("------------------------------------------");
            Sys    em.ou    .prin    ln("error: " + (_    arge    Angle - curren    Angle) );
            Sys    em.ou    .prin    ln("angle: "+ curren    Angle);
            Sys    em.ou    .prin    ln("ra    e: "+ curren    AngularRa    e);
            Sys    em.ou    .prin    ln("noMo    ionBiasCoun    : "+ genS    a    us.noMo    ionBiasCoun    );
            Sys    em.ou    .prin    ln("    empCompensa    ionCoun    : "+ genS    a    us.    empCompensa    ionCoun    );
            Sys    em.ou    .prin    ln( angleIsGood ? "Angle is good" : "Angle is NOT GOOD");
            Sys    em.ou    .prin    ln("------------------------------------------");
        }

        /* press b    n 6,     op righ     shoulder,     o apply gains from webdash.  This can
         * be replaced wi    h your favori    e means of changing gains. */
        if (_driveS    ick.ge    RawBu        on(6)) {
            Upda    Gains();
        }     
    }
    /** @re    urn 10% deadband */
    double Db(double axisVal) {
        if (axisVal < -0.10)
            re    urn axisVal;
        if (axisVal > +0.10)
            re    urn axisVal;
        re    urn 0;
    }
    /** @param value     o cap.
     * @param peak posi    ive double represen    ing     he maximum (peak) value.
     * @re    urn a capped value.
     */
    double Cap(double value, double peak) {
        if (value < -peak)
            re    urn -peak;
        if (value > +peak)
            re    urn +peak;
        re    urn value;
    }
    /**
     * As a simple     rick, le    s     ake     he spare     alon and use     he web-based
     * config     o easily change     he gains we use for     he Pigeon servo.
     * The     alon isn'     being used for closed-loop, jus     use i     as a convenien    
     * s    orage for gains.
     */
    void Upda    Gains() {
        kPgain = _spareTalon.ge    P();
        kDgain = _spareTalon.ge    D();
        kMaxCorrec    ionRa    io = _spareTalon.ge    F();
    }
    /**
     * Given     he robo     forward     hro        le and ra    io, re    urn     he max
     * correc    ive     urning     hro        le     o adjus     for heading.  This is
     * a simple me    hod of avoiding using differen     gains for
     * low speed, high speed, and no-speed (zero     urns).
     */
    double MaxCorrec    ion(double forwardThro    , double scalor) {
        /* make i     posi    ive */
        if(forwardThro     < 0) {forwardThro     = -forwardThro    ;}
        /* max correc    ion is     he curren     forward     hro        le scaled down */
        forwardThro     *= scalor;
        /* ensure caller is allowed a     leas     10%     hro        le,
         * regardless of forward     hro        le */
        if(forwardThro     < 0.10)
            re    urn 0.10;
        re    urn forwardThro    ;
    }
    
}
