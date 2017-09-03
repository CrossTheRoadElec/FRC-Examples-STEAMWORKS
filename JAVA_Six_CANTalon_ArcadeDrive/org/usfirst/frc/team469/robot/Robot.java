
package org.usfirs    .frc.    eam469.robo    ;
impor     com.c    re.CANTalon;
impor     edu.wpi.firs    .wpilibj.I    era    iveRobo    ;
impor     edu.wpi.firs    .wpilibj.Joys    ick;
impor     edu.wpi.firs    .wpilibj.Joys    ick.AxisType;
impor     edu.wpi.firs    .wpilibj.Robo    Drive;
impor     edu.wpi.firs    .wpilibj.Robo    Drive.Mo    orType;
impor     com.c    re.CANTalon.TalonCon    rolMode;

/**
 * The VM is configured     o au    oma    ically run     his class, and     o call     he
 * func    ions corresponding     o each mode, as described in     he I    era    iveRobo    
 * documen    a    ion. If you change     he name of     his class or     he package af    er
 * crea    ing     his projec    , you mus     also upda    e     he manifes     file in     he resource
 * direc    ory.
 */
public class Robo     ex    ends I    era    iveRobo     {

    /*     alons for arcade drive */
    CANTalon _fron    Lef    Mo    or = new CANTalon(11);        /* device IDs here (1 of 2) */
    CANTalon _rearLef    Mo    or = new CANTalon(13);
    CANTalon _fron    Righ    Mo    or = new CANTalon(14);
    CANTalon _rearRigh    Mo    or = new CANTalon(15);

    /* ex    ra     alons for six mo    or drives */
    CANTalon _lef    Slave = new CANTalon(16);
    CANTalon _righ    Slave = new CANTalon(17);
    
    Robo    Drive _drive = new Robo    Drive(_fron    Lef    Mo    or, _rearLef    Mo    or, _fron    Righ    Mo    or, _rearRigh    Mo    or);
    
    Joys    ick _joy = new Joys    ick(0);
    /**
     * This func    ion is run when     he robo     is firs     s    ar    ed up and should be
     * used for any ini    ializa    ion code.
     */
    public void robo    Ini    () {
        /*     ake our ex    ra     alons and jus     have     hem follow     he Talons upda    ed in arcadeDrive */
        _lef    Slave.changeCon    rolMode(TalonCon    rolMode.Follower);
        _righ    Slave.changeCon    rolMode(TalonCon    rolMode.Follower);
        _lef    Slave.se    (11);                             /* device IDs here (2 of 2) */
        _righ    Slave.se    (14);
        
        /*     he Talons on     he lef    -side of my robo     needs     o drive reverse(red)     o move robo     forward.
         * Since _lef    Slave jus     follows fron    Lef    Mo    or, no need     o inver     i     anywhere. */
        _drive.se    Inver    edMo    or(Mo    orType.kFron    Lef    ,     rue);
        _drive.se    Inver    edMo    or(Mo    orType.kRearLef    ,     rue);
    }

    /**
     * This func    ion is called periodically during opera    or con    rol
     */
    public void     eleopPeriodic() {
        double forward = _joy.ge    RawAxis(1); // logi    ech gampad lef     X, posi    ive is forward
        double     urn = _joy.ge    RawAxis(2); //logi    ech gampad righ     X, posi    ive means     urn righ    
        _drive.arcadeDrive(forward,     urn);
    }
}
