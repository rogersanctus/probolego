package probolego;

import ch.aplu.robotsim.*;

public class PRoboLego {

    LegoRobot robo; // Representa o módulo Brick do kit Lego
    Motor me;   // Motor esquerda
    Motor md;   // Motor direito
    ColorSensor sc;     // Sensor de cor
    boolean st;         // Simula um sensor de toque
    
    boolean pAzul, tentarEsquerda, oldTentarEsquerda;        // Tentar achar a outra cor na esquerda
    boolean bVerde, bAzul;
    boolean toque;
    
    int calibragem;
    boolean bLinhas;
    boolean reverso;
    
    double tTent, brancoT;       // Temporizadores com tipo double para suportar numeros grandes.
    
    static int TCURVA = 50;
    static int TPARAFRENTE = 10;
    static int VELCURVA = 35;
    static int VELFRENTE = 10;
    
    public PRoboLego(){}
    
    public void iniciar()
    {
        robo = new LegoRobot();
        me = new Motor(MotorPort.A);
        md = new Motor(MotorPort.B);
        sc = new ColorSensor(SensorPort.S3);
        
        bAzul = bVerde = false;
        
        reverso = false;
        bLinhas = false;
        brancoT = tTent = 0;
        calibragem = 0;

        st = true;      // Considera que o sensor já foi precionado
        toque = true;
        
        /*
        * Considerando, inicialmente, que a linha verde está a esquerda da azul.
        */
        pAzul = tentarEsquerda = oldTentarEsquerda = true;
        
        robo.addPart(me);
        robo.addPart(md);
        robo.addPart(sc);
        
        me.setSpeed(60);
        md.setSpeed(60);
        
        // Giro inicial
        girarRobo( false, 70, 100 );
        
        // Simula sensor de toque
        while(!toque);
        
        while(true) // Laço 'infinito'
        {
            
            if(false)
            {
                me.setSpeed(50);
                md.setSpeed(50);
                me.forward();
                md.backward();
                Tools.delay(1400);
                me.stop();
                md.stop();
                Tools.delay(1000);
                continue;
            }            
            
            if( viuAmarelo() )
            {
                RobotContext.setStatusText("Objetivo Alcançado");
                me.stop();
                md.stop();
                continue;
            }
            
            // Desvia das bordas limite do cenário
            if( viuCor( new Cor(sc.getColor()), new Cor(255,0,0), 40, 40 ))
            {
                RobotContext.setStatusText("Viu vermelho");
                me.setSpeed(70);
                md.setSpeed(70);
                
                me.backward();
                md.backward();
                Tools.delay(1000);
                
                me.forward();
                md.backward();
                Tools.delay(500);
            }
            
            buscarLinha();
            
            if( calibragem == 0 || calibragem > 2 )
            {
                paraFrente(1, 100);
            }
        }
    }
    
    private void buscarLinha()
    {
        //if( !bLinhas )
        if( calibragem == 0 )
        {
            if( viuAzul() ) bAzul = true;
            if( viuVerde() ) bVerde = true;

            if( bAzul )
            {
                girarRobo(tentarEsquerda, TCURVA, VELCURVA);
                paraFrente(TPARAFRENTE, VELFRENTE);

                if( viuBranco() ) tentarEsquerda = !tentarEsquerda;
                if( viuVerde() )
                {
                    RobotContext.setStatusText("Primeiro Azul.");
                    
                    bAzul = false;
                    //bLinhas = true;
                    calibragem = 1;
                    pAzul = true;
                }
            }else
            if( bVerde )
            {
                girarRobo(tentarEsquerda, TCURVA, VELCURVA);
                paraFrente(TPARAFRENTE, VELFRENTE);

                if( viuBranco() ) tentarEsquerda = !tentarEsquerda;
                if( viuAzul() )
                {
                    RobotContext.setStatusText("Primeiro Verde.");
                    
                    bVerde = false;
                    //bLinhas = true;
                    calibragem = 1;
                    pAzul = false;
                }
            }
        }else if( calibragem == 1 )
        {
            // Se a primeira cor foi azul, é hora de procurá-la novamente
            if( pAzul )
            {
                if(!viuAzul())
                {
                    girarRobo(!tentarEsquerda, TCURVA, VELCURVA);
                    paraFrente(TPARAFRENTE, 0);
                    
                    if(viuBranco()) tentarEsquerda = !tentarEsquerda;
                }else calibragem++;
            }else
            {
                if(!viuVerde())
                {
                    girarRobo(!tentarEsquerda, TCURVA, VELCURVA);
                    paraFrente(TPARAFRENTE, 0);
                    
                    if(viuBranco()) tentarEsquerda = !tentarEsquerda;
                }else calibragem++;
            }
        }else
        {
            //me.stop();
            //md.stop();
            
            
            if(!reverso)
            {
                girarRobo(tentarEsquerda, TCURVA, VELCURVA);
                paraFrente(TPARAFRENTE, 10);
            }
            else
            {
                girarRobo(!tentarEsquerda, TCURVA, VELCURVA);
                paraTraz(TPARAFRENTE, 10);
            }
            //oldTentarEsquerda = tentarEsquerda;
            
            if(viuAzul())bAzul = true;
            if(viuVerde())bVerde = true;
            
            if( bAzul )
            {
                if(viuVerde())
                {
                    tentarEsquerda = !tentarEsquerda;
                    bAzul = false;
                }
            }
            if( bVerde )
            {
                if(viuAzul())
                {
                    tentarEsquerda = !tentarEsquerda;
                    bVerde = false;
                }
            }
            
            /**
             * Se ver um branco enquanto segue a linha é por que passou um pouco
             * de uma das linhas ou chegou ao fim do percurso sem encontrar o
             * ponto amarelo. Nesse caso, volta um pouco para traz e gira o mais
             * próximo possível de 180º.
             */            
            if( viuBranco() )
            {
                if( tTent >= 10 )
                {
                    paraTraz( 100, 20 );
                    
                    me.setSpeed(50);
                    md.setSpeed(50);
                    me.backward();
                    md.forward();
                    Tools.delay(1500);
                    
                    tTent = 0;
                    brancoT = -35; // ;)
                    calibragem = 0;
                    //reverso = true;
                    //bVerde = bAzul = false;
                    tentarEsquerda = !tentarEsquerda;
                }
                else if(brancoT >= 5)
                {
                    paraTraz( 50, 20 );
                    brancoT = 0;
                }
                brancoT++;
                tTent++;
            }
        }
        //if( !bAzul && !bVerde ) bLinhas= false;
        
        /*if( viuAzul() )
        {
            //while(true)
            {
                girarRobo(tentarEsquerda, TCURVA);
                
                paraFrente(10, TPARAFRENTE);
                
                if(viuBranco()) tentarEsquerda = false;
                
                if( viuVerde() )
                {
                    break;
                }
            }
        }
        if( viuVerde() )
        {
            while(true)
            {
                girarRobo(tentarEsquerda, TCURVA);
                
                paraFrente(10, TPARAFRENTE);
                
                if(viuBranco()) tentarEsquerda = false;
                
                if( viuAzul() )
                {
                    break;
                }
            }
        }*/
    }
    
    private void girarRobo( boolean esquerda, int tempo, int vel )
    {
        me.setSpeed(vel);
        md.setSpeed(vel);
        
        if( esquerda )
        {
            me.backward();
            md.forward();            
        }else
        {
            me.forward();
            md.backward();            
        }
        Tools.delay(tempo);
    }
    
    private void paraFrente( int tempo, int vel )
    {
        me.setSpeed(vel);
        md.setSpeed(vel);
        
        me.forward();
        md.forward();
        Tools.delay(tempo);
    }
    
    private void paraTraz( int tempo, int vel )
    {
        me.setSpeed(vel);
        md.setSpeed(vel);

        me.backward();
        md.backward();
        Tools.delay(tempo);
    }
    
    private boolean viuBranco()
    {
        return viuCor( new Cor(sc.getColor()), new Cor(255,255,255), 80, 0 );
    }
    
    private boolean viuAzul()
    {
        return viuCor( new Cor(sc.getColor()), new Cor(55,55,255), 80, 120);
    }
    
    private boolean viuVerde()
    {
        return viuCor( new Cor(sc.getColor()), new Cor(55,255,55), 80, 120);
    }
    
    private boolean viuAmarelo()
    {
        return viuCor( new Cor(sc.getColor()), new Cor(200,200,0), 80, 120);
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
        RobotContext.showStatusBar(28);
        RobotContext.useBackground("cenarios/cenario1.bmp");
        RobotContext.setStartPosition(320, 80);
        PRoboLego probo = new PRoboLego();

        probo.iniciar();
    }
    
}
