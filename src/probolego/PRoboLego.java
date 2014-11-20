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
    
    public static void main(String[] args) { 
        RobotContext.useBackground("cenarios/cenario1.bmp");
        RobotContext.setStartPosition(340, 150);
        PRoboLego probo = new PRoboLego();

        probo.iniciar();
    }
    
}
