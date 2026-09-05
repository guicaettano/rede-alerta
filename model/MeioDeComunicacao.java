/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: MeioDeComunicacao.java
* Funcao: Simular o funcionamento do meio de comunicacao
********************************************************************/

package model;

import control.ControllerPrincipal;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;

public class MeioDeComunicacao{
  private ControllerPrincipal cP = new ControllerPrincipal();
  private Slider slider;
  private ImageView botaoVoltar;
  
  public MeioDeComunicacao(ControllerPrincipal cP, Slider slider, ImageView botaoVoltar){
    this.cP = cP;
    this.slider = slider;
    this.botaoVoltar = botaoVoltar;
  }
  
  public int getVelocidade(){
    double velAux = slider.getValue();
    int vel = (int)velAux;
    return vel * 50;
  }
 
  /********************************************************************
   * Metodo: meioDeComunicacao
   * Funcao: simula o comportamento de onda na transmissão do fluxo bruto
   * de bits do ponto A para o ponto B
   * Parametros: fluxoBrutoDeBits (int[])
   * Retorno: 
   ********************************************************************/
  public void meioDeComunicacao(int[] fluxoBrutoDeBits){
    int[] fluxoBrutoDeBitsPontoA, fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBits.length];
    fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;  
    botaoVoltar.setVisible(false);
    botaoVoltar.setDisable(true);
    new Thread(() -> {
      int deslocamento = 31;
      int idxFluxo = 0; 
      int bit; 
      int ultimoSinal = -1;
      int proximoChar;

      for(int i = 0; i < fluxoBrutoDeBitsPontoA.length * 32; i++){
        if(i % 32 == 0 && i != 0){
          idxFluxo++;
          deslocamento = 31;
        } 
        if(i % 8 == 0){
          proximoChar = (fluxoBrutoDeBitsPontoA[idxFluxo] >> (deslocamento - 7)) & 255;
          if(proximoChar == 0){
            break;
          }
        }

        bit = (fluxoBrutoDeBitsPontoA[idxFluxo] & (1 << deslocamento)) >> deslocamento;
        if(bit == -1){
          bit = 1;
        }
        fluxoBrutoDeBitsPontoB[idxFluxo] |= (bit) << deslocamento;
        cP.deslocaSinal();
        cP.atualizaSinal(bit, ultimoSinal);
        ultimoSinal = bit;
        deslocamento--;

        try{
          Thread.sleep(getVelocidade());
        } catch (Exception e){
          e.printStackTrace();
        }
      }

      for(int j = 0; j < 8; j++){
        cP.deslocaSinal();
        cP.removeSinal(j);
        try{
          Thread.sleep(getVelocidade());
        } catch (Exception e){
          e.printStackTrace();
        }
      }

      ControllerPrincipal.cFR.camadaFisicaReceptora(fluxoBrutoDeBitsPontoB);
    }).start(); 
  }
}
