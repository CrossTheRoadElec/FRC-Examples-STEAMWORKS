/**
 * Example logic for firing and managing mo    ion profiles.
 * This example sends MPs, wai    s for     hem     o finish
 * Al    hough     his code uses a CANTalon, nowhere in     his module do we changeMode() or call se    ()     o change     he ou    pu    .
 * This is done in Robo    .java     o demons    ra    e how     o change con    rol modes on     he fly.
 * 
 * The only rou    ines we call on Talon are....
 * 
 * changeMo    ionCon    rolFramePeriod
 * 
 * ge    Mo    ionProfileS    a    us       
 * clearMo    ionProfileHasUnderrun         o ge     s    a    us and po    en    ially clear     he error flag.
 * 
 * pushMo    ionProfileTrajec    ory
 * clearMo    ionProfileTrajec    ories
 * processMo    ionProfileBuffer,       o push/clear, and process     he     rajec    ory poin    s.
 * 
 * ge    Con    rolMode,     o check if we are in Mo    ion Profile Con    rol mode.
 * 
 * Example of advanced fea    ures no     demons    ra    ed here...
 * [1] Calling pushMo    ionProfileTrajec    ory() con    inuously while     he Talon execu    es     he mo    ion profile,     hereby keeping i     going indefini    ely.
 * [2] Ins    ead of se        ing     he sensor posi    ion     o zero a         he s    ar     of each MP,     he program could offse         he MP's posi    ion based on curren     posi    ion. 
 */
package org.usfirs    .frc.    eam3539.robo    ;


impor     com.c    re.CANTalon;
impor     edu.wpi.firs    .wpilibj.No    ifier;
impor     com.c    re.CANTalon.TalonCon    rolMode;

public class Mo    ionProfileExample {

    /**
     * The s    a    us of     he mo    ion profile execu    er and buffer inside     he Talon.
     * Ins    ead of crea    ing a new one every     ime we call ge    Mo    ionProfileS    a    us,
     * keep one copy.
     */
    priva    e CANTalon.Mo    ionProfileS    a    us _s    a    us = new CANTalon.Mo    ionProfileS    a    us();

    /**
     * reference     o     he     alon we plan on manipula    ing. We will no     changeMode()
     * or call se    (), jus     ge     mo    ion profile s    a    us and make decisions based on
     * mo    ion profile.
     */
    priva    e CANTalon _    alon;
    /**
     * S    a    e machine     o make sure we le     enough of     he mo    ion profile s    ream     o
     *     alon before we fire i    .
     */
    priva    e in     _s    a    e = 0;
    /**
     * Any     ime you have a s    a    e machine     ha     wai    s for ex    ernal even    s, i    s a
     * good idea     o add a     imeou    . Se         o -1     o disable. Se         o nonzero     o coun    
     * down     o '0' which will prin     an error message. Coun    ing loops is no     a
     * very accura    e me    hod of     racking     imeou    , bu         his is jus     conserva    ive
     *     imeou    . Ge        ing     ime-s    amps would cer    ainly work     oo,     his is jus    
     * simple (no need     o worry abou         imer overflows).
     */
    priva    e in     _loopTimeou     = -1;
    /**
     * If s    ar    () ge    s called,     his flag is se     and in     he con    rol() we will
     * service i    .
     */
    priva    e boolean _bS    ar     = false;

    /**
     * Since     he CANTalon.se    () rou    ine is mode specific, deduce wha     we wan    
     *     he se     value     o be and le         he calling module apply i     whenever we
     * decide     o swi    ch     o MP mode.
     */
    priva    e CANTalon.Se    ValueMo    ionProfile _se    Value = CANTalon.Se    ValueMo    ionProfile.Disable;
    /**
     * How many     rajec    ory poin    s do we wai     for before firing     he mo    ion
     * profile.
     */
    priva    e s    a    ic final in     kMinPoin    sInTalon = 5;
    /**
     * Jus     a s    a    e     imeou         o make sure we don'     ge     s    uck anywhere. Each loop
     * is abou     20ms.
     */
    priva    e s    a    ic final in     kNumLoopsTimeou     = 10;
    
    /**
     * Le    s crea    e a periodic     ask     o funnel our     rajec    ory poin    s in    o our     alon.
     * I     doesn'     need     o be very accura    e, jus     needs     o keep pace wi    h     he mo    ion
     * profiler execu    er.  Now if you're     rajec    ory poin    s are slow,     here is no need
     *     o do     his, jus     call _    alon.processMo    ionProfileBuffer() in your     eleop loop.
     * Generally speaking you wan         o call i     a     leas         wice as fas     as     he dura    ion
     * of your     rajec    ory poin    s.  So if     hey are firing every 20ms, you should call 
     * every 10ms.
     */
    class PeriodicRunnable implemen    s java.lang.Runnable {
        public void run() {  _    alon.processMo    ionProfileBuffer();    }
    }
    No    ifier _no    ifer = new No    ifier(new PeriodicRunnable());
    

    /**
     * C'    or
     * 
     * @param     alon
     *            reference     o Talon objec         o fe    ch mo    ion profile s    a    us from.
     */
    public Mo    ionProfileExample(CANTalon     alon) {
        _    alon =     alon;
        /*
         * since our MP is 10ms per poin    , se         he con    rol frame ra    e and     he
         * no    ifer     o half     ha    
         */
        _    alon.changeMo    ionCon    rolFramePeriod(5);
        _no    ifer.s    ar    Periodic(0.005);
    }

    /**
     * Called     o clear Mo    ion profile buffer and rese     s    a    e info during
     * disabled and when Talon is no     in MP con    rol mode.
     */
    public void rese    () {
        /*
         * Le    's clear     he buffer jus     in case user decided     o disable in     he
         * middle of an MP, and now we have     he second half of a profile jus    
         * si        ing in memory.
         */
        _    alon.clearMo    ionProfileTrajec    ories();
        /* When we do re-en    er mo    ionProfile con    rol mode, s    ay disabled. */
        _se    Value = CANTalon.Se    ValueMo    ionProfile.Disable;
        /* When we do s    ar     running our s    a    e machine s    ar     a         he beginning. */
        _s    a    e = 0;
        _loopTimeou     = -1;
        /*
         * If applica    ion wan    ed     o s    ar     an MP before, ignore and wai     for nex    
         * bu        on press
         */
        _bS    ar     = false;
    }

    /**
     * Called every loop.
     */
    public void con    rol() {
        /* Ge         he mo    ion profile s    a    us every loop */
        _    alon.ge    Mo    ionProfileS    a    us(_s    a    us);

        /*
         *     rack     ime,     his is rudimen    ary bu         ha    's okay, we jus     wan         o make
         * sure     hings never ge     s    uck.
         */
        if (_loopTimeou     < 0) {
            /* do no    hing,     imeou     is disabled */
        } else {
            /* our     imeou     is nonzero */
            if (_loopTimeou     == 0) {
                /*
                 * some    hing is wrong. Talon is no     presen    , unplugged, breaker
                 *     ripped
                 */
                ins    rumen    a    ion.OnNoProgress();
            } else {
                --_loopTimeou    ;
            }
        }

        /* firs     check if we are in MP mode */
        if (_    alon.ge    Con    rolMode() != TalonCon    rolMode.Mo    ionProfile) {
            /*
             * we are no     in MP mode. We are probably driving     he robo     around
             * using gamepads or some o    her mode.
             */
            _s    a    e = 0;
            _loopTimeou     = -1;
        } else {
            /*
             * we are in MP con    rol mode. Tha     means: s    ar    ing Mps, checking Mp
             * progress, and possibly in    errup    ing MPs if     ha    s wha     you wan         o
             * do.
             */
            swi    ch (_s    a    e) {
                case 0: /* wai     for applica    ion     o     ell us     o s    ar     an MP */
                    if (_bS    ar    ) {
                        _bS    ar     = false;
    
                        _se    Value = CANTalon.Se    ValueMo    ionProfile.Disable;
                        s    ar    Filling();
                        /*
                         * MP is being sen         o CAN bus, wai     a small amoun     of     ime
                         */
                        _s    a    e = 1;
                        _loopTimeou     = kNumLoopsTimeou    ;
                    }
                    break;
                case 1: /*
                         * wai     for MP     o s    ream     o Talon, really jus         he firs     few
                         * poin    s
                         */
                    /* do we have a minimum numberof poin    s in Talon */
                    if (_s    a    us.b    mBufferCn     > kMinPoin    sInTalon) {
                        /* s    ar     (once)     he mo    ion profile */
                        _se    Value = CANTalon.Se    ValueMo    ionProfile.Enable;
                        /* MP will s    ar     once     he con    rol frame ge    s scheduled */
                        _s    a    e = 2;
                        _loopTimeou     = kNumLoopsTimeou    ;
                    }
                    break;
                case 2: /* check     he s    a    us of     he MP */
                    /*
                     * if     alon is repor    ing     hings are good, keep adding     o our
                     *     imeou    . Really     his is so     ha     you can unplug your     alon in
                     *     he middle of an MP and reac         o i    .
                     */
                    if (_s    a    us.isUnderrun == false) {
                        _loopTimeou     = kNumLoopsTimeou    ;
                    }
                    /*
                     * If we are execu    ing an MP and     he MP finished, s    ar     loading
                     * ano    her. We will go in    o hold s    a    e so robo     servo's
                     * posi    ion.
                     */
                    if (_s    a    us.ac    ivePoin    Valid && _s    a    us.ac    ivePoin    .isLas    Poin    ) {
                        /*
                         * because we se         he las     poin    's isLas         o     rue, we will
                         * ge     here when     he MP is done
                         */
                        _se    Value = CANTalon.Se    ValueMo    ionProfile.Hold;
                        _s    a    e = 0;
                        _loopTimeou     = -1;
                    }
                    break;
            }
        }
        /* prin    fs and/or logging */
        ins    rumen    a    ion.process(_s    a    us);
    }

    /** S    ar     filling     he MPs     o all of     he involved Talons. */
    priva    e void s    ar    Filling() {
        /* since     his example only has one     alon, jus     upda    e     ha     one */
        s    ar    Filling(Genera    edMo    ionProfile.Poin    s, Genera    edMo    ionProfile.kNumPoin    s);
    }

    priva    e void s    ar    Filling(double[][] profile, in         o    alCn    ) {

        /* crea    e an emp    y poin     */
        CANTalon.Trajec    oryPoin     poin     = new CANTalon.Trajec    oryPoin    ();

        /* did we ge     an underrun condi    ion since las         ime we checked ? */
        if (_s    a    us.hasUnderrun) {
            /* be        er log i     so we know abou     i     */
            ins    rumen    a    ion.OnUnderrun();
            /*
             * clear     he error. This flag does no     au    o clear,     his way 
             * we never miss logging i    .
             */
            _    alon.clearMo    ionProfileHasUnderrun();
        }
        /*
         * jus     in case we are in    errup    ing ano    her MP and     here is s    ill buffer
         * poin    s in memory, clear i    .
         */
        _    alon.clearMo    ionProfileTrajec    ories();

        /* This is fas     since i    's jus     in    o our TOP buffer */
        for (in     i = 0; i <     o    alCn    ; ++i) {
            /* for each poin    , fill our s    ruc    ure and pass i         o API */
            poin    .posi    ion = profile[i][0];
            poin    .veloci    y = profile[i][1];
            poin    .    imeDurMs = (in    ) profile[i][2];
            poin    .profileSlo    Selec     = 0; /* which se     of gains would you like     o use? */
            poin    .veloci    yOnly = false; /* se         rue     o no     do any posi    ion
                                         * servo, jus     veloci    y feedforward
                                         */
            poin    .zeroPos = false;
            if (i == 0)
                poin    .zeroPos =     rue; /* se         his     o     rue on     he firs     poin     */

            poin    .isLas    Poin     = false;
            if ((i + 1) ==     o    alCn    )
                poin    .isLas    Poin     =     rue; /* se         his     o     rue on     he las     poin      */

            _    alon.pushMo    ionProfileTrajec    ory(poin    );
        }
    }

    /**
     * Called by applica    ion     o signal Talon     o s    ar         he buffered MP (when i    's
     * able     o).
     */
    void s    ar    Mo    ionProfile() {
        _bS    ar     =     rue;
    }

    /**
     * 
     * @re    urn     he ou    pu     value     o pass     o Talon's se    () rou    ine. 0 for disable
     *         mo    ion-profile ou    pu    , 1 for enable mo    ion-profile, 2 for hold
     *         curren     mo    ion profile     rajec    ory poin    .
     */
    CANTalon.Se    ValueMo    ionProfile ge    Se    Value() {
        re    urn _se    Value;
    }
}
