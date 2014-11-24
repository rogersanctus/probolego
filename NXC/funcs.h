//#include "cor.h"
#define MIN(a,b)    ((a < b)? (a) : (b))
#define MAX(a,b)    ((a > b)? (a) : (b))


struct Cor
{
    byte r;
    byte g;
    byte b;
};

#define B_MIN       80
#define B_MAX       0
#define AZUL_MIN    80
#define AZUL_MAX    120
#define VERDE_MIN   80
#define VERDE_MAX   120
#define VERM_MIN    80
#define VERM_MAX    120
#define AM_MIN      80
#define AM_MAX      120

Cor corRGB( byte r, byte g, byte b )
{
    Cor c;
    c.r = r;
    c.g = g;
    c.b = b;
    return c;
}

#define BRANCO      corRGB(255, 255, 255)
#define AZUL        corRGB(55, 55, 255)
#define VERDE       corRGB(55, 255, 55)
#define VERMELHO    corRGB(255, 55, 55)
#define AMARELO     corRGB(200, 200, 0)

void paraFrente( long tempo, int vel )
{
    OnFwd( OUT_AC, vel );
    Wait( tempo );
}

void paraTraz( long tempo, int vel )
{
    OnRev( OUT_AC, vel );
    Wait( tempo );
}

/**
 * Girar robô para esquerda? por tempo e com vel percentual
 * da velocidade.
 * (Considerando motor A à esquerda e motor B à direita do robô)
 */
void girarRobo( bool esquerda, long tempo, int vel )
{
    if( esquerda )
    {
        OnRev( OUT_A, vel );
        OnFwd( OUT_C, vel );
    }else
    {
        OnFwd( OUT_A, vel );
        OnRev( OUT_C, vel );
    }
    
    Wait( tempo );
}

bool viuCor( Cor cmp, int min, int max )
{
    unsigned int raw[4];
    ReadSensorColorRaw( IN_3, raw );
    
    byte rmin = MAX( cmp.r - min, 0 ),
         rmax = MIN( cmp.r + max, 255 ),
         gmin = MAX( cmp.g - min, 0 ),
         gmax = MIN( cmp.g + max, 255),
         bmin = MAX( cmp.b - min, 0 ),
         bmax = MIN( cmp.b + max, 255 );    
    
    /* Escala as cores que vem do sensor entre 0 e 1024
     * para 0 e 255
     */
    Cor s;
    s.r = 256 * raw[INPUT_RED] / 1024;
    s.g = 256 * raw[INPUT_GREEN] / 1024;
    s.b = 256 * raw[INPUT_BLUE] / 1024;
    
    if( s.r >= rmin && s.r <= rmax &&
        s.g >= gmin && s.g <= gmax &&
        s.b >= bmin && s.b <= bmax )
    {
        return true;
    }
    
    return false;
}

inline bool viuBranco()
{
    return viuCor( BRANCO, B_MIN, B_MAX );
}

inline bool viuAzul()
{
    return viuCor( AZUL, AZUL_MIN, AZUL_MAX );
}

inline bool viuVerde()
{
    return viuCor( VERDE, VERDE_MIN, VERDE_MAX );
}

inline bool viuVermelho()
{
    return viuCor( VERMELHO, VERM_MIN, VERM_MAX );
}

inline bool viuAmarelo()
{
    return viuCor( AMARELO, AM_MIN, AM_MAX );
}
