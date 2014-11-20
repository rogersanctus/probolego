package probolego;

import ch.aplu.robotsim.*;

public class PRoboLego {

    LegoRobot robo; // Representa o módulo Brick do kit Lego
    Motor me;   // Motor esquerda
    Motor md;   // Motor direito
    ColorSensor sc;     // Sensor de cor
    boolean st;         // Simula um sensor de toque
    boolean dir;        // A direção dos motores muda ou não?
    
    double tempo;       // Temporizador com tipo double para suportar numeros grandes.
    
    public PRoboLego(){}
    
    public void iniciar()
    {
        robo = new LegoRobot();
        me = new Motor(MotorPort.A);
        md = new Motor(MotorPort.B);
        sc = new ColorSensor(SensorPort.S3);
        
        tempo = 0;      // Tempo inicial = 0
        st = true;      // Considera que o sensor já foi precionado
        
        robo.addPart(me);
        robo.addPart(md);
        robo.addPart(sc);
        
        while(true) // Laço 'infinito'
        {
            zigzag();
            //me.forward();
            //md.forward();
            
            if( viuCor( new Cor(sc.getColor()), new Cor(255,0,0), 40, 40 ))
            {
                me.backward();
                md.backward();
                Tools.delay(1000);
                tempo += 1000;
                
                me.forward();
                md.backward();
                Tools.delay(1200);
                tempo += 1200;
            }
            
            Tools.delay(1);
            tempo++;
        }
    }
    
    private void zigzag()
    {
        int flag = (int)(tempo % 500);
        
        if( flag == 0 )
        {
            if( !dir )
            {
                me.forward();
                md.backward();
            }
            else
            {
                me.backward();
                md.forward();
            }
            Tools.delay(500);
            
            dir = !dir;
        }else
        {
            me.forward();
            md.forward();
        }
    }
    
    private boolean viuCor( Cor sensor, Cor cmp, int min, int max )
    {
        int rmin = Math.max(cmp.red - min, 0),
            rmax = Math.min(cmp.red + max, 255),
            gmin = Math.max(cmp.green - min, 0),
            gmax = Math.min(cmp.green + max, 255),
            bmin = Math.max(cmp.blue - min, 0),
            bmax = Math.min(cmp.blue + max, 255);
        
        if( sensor.red >= rmin && sensor.red <= rmax &&
            sensor.green >= gmin && sensor.green <= gmax &&
            sensor.blue >= bmin && sensor.blue <= bmax )
        {
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) { 
        RobotContext.useBackground("cenarios/cenario1.bmp");
        RobotContext.setStartPosition(340, 150);
        PRoboLego probo = new PRoboLego();

        probo.iniciar();
    }
    
}
