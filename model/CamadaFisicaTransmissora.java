/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: CamadaFisicaTransmissora.java
* Funcao: Simular o funcionamento da camada fisica transmissora
********************************************************************/

package model;

import java.util.Arrays;
import control.ControllerPrincipal;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CamadaFisicaTransmissora {

  private ControllerPrincipal cP = new ControllerPrincipal();
  private TextArea textoFluxo;

  public CamadaFisicaTransmissora(ControllerPrincipal cP, TextArea textoFluxo){
    this.cP = cP;
    this.textoFluxo = textoFluxo;
  }

  /********************************************************************
   * Metodo: camadaFisicaTransmissora
   * Funcao: codifica a mensagem e monta o fluxo bruto de bits com base 
   * no quadro da mensagem
   * Parametros: quadro (int[])
   * Retorno: void
   ********************************************************************/
  public void camadaFisicaTransmissora(int[] quadro){
    int codificacao = cP.getCodificacao();
		int[] fluxoBrutoDeBits = new int[codificacao == 0 ? quadro.length : (quadro.length)*2];
		switch (codificacao) {
			case 0: 
				fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoBinaria(quadro);
				break;
			case 1: 
				fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchester(quadro);
				break;
			case 2: 
				fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);
				break;
		}
  
    int deslocamentoFluxo = 31;
    int idxFluxo = 0;
    int bit;
    int proximoChar;
    String stringFluxo = "";
    for(int l = 0; l < fluxoBrutoDeBits.length * 32; l++){
      if(l % 32 == 0 && l != 0){
        idxFluxo++;
        deslocamentoFluxo = 31;
      }

      if(l % 8 == 0){
        proximoChar = (fluxoBrutoDeBits[idxFluxo] >> (deslocamentoFluxo - 7)) & 255;
        if(proximoChar == 0){
          break;
        }
      }

      bit = (fluxoBrutoDeBits[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo;
      if(bit == -1){
        bit = 1;
      }
      stringFluxo += bit;
      deslocamentoFluxo--;
    }

    textoFluxo.setText(stringFluxo);
    textoFluxo.setVisible(true);

    ControllerPrincipal.mC.meioDeComunicacao(fluxoBrutoDeBits);
  }

  /********************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoBinaria
   * Funcao: monta o fluxo bruto de bits com base na codificacao binaria
   * Parametros: quadro (int[])
   * Retorno: quadro (int[])
   ********************************************************************/
  public int[] camadaFisicaTransmissoraCodificacaoBinaria(int[] quadro){
    return quadro;
  }

  /********************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoManchester
   * Funcao: monta o fluxo bruto de bits com base na codificacao manchester
   * Parametros: quadro (int[])
   * Retorno: fluxoDeBits (int[])
   ********************************************************************/
  public int[] camadaFisicaTransmissoraCodificacaoManchester(int[] quadro){
    int[] fluxoDeBits = new int[quadro.length*2];
    int idxQuadro = 0;
    int idxFluxo = 0;
    int deslocamentoQuadro = 31;
    int deslocamentoFluxo = 31;
    int bit;
    int proximoChar;

    for(int i = 0; i < quadro.length * 32; i++){
      if(i % 32 == 0 && i != 0){
        idxQuadro++;
        deslocamentoQuadro = 31;
      } 
      if(i % 16 == 0 && i != 0){
        idxFluxo++;
        deslocamentoFluxo = 31;
      }
      if(i % 8 == 0){
        proximoChar = (quadro[idxQuadro] >> (deslocamentoQuadro - 7)) & 255;
        if(proximoChar == 0){
          break;
        }
      }
      
      bit = (quadro[idxQuadro] & (1 << deslocamentoQuadro)) >> deslocamentoQuadro;
      if(bit == -1){
        bit = 1;
      }
      fluxoDeBits[idxFluxo] |= (bit) << deslocamentoFluxo;
      fluxoDeBits[idxFluxo] |= (1 - bit) << deslocamentoFluxo - 1;
      deslocamentoQuadro--;
      deslocamentoFluxo -= 2;
    }

    int novoIndex = 0;
    for (int j = 0; j < fluxoDeBits.length; j++){
      if (!(fluxoDeBits[j] == 0)) {
        fluxoDeBits[novoIndex++] = fluxoDeBits[j];
      }
    }
 
    fluxoDeBits = Arrays.copyOf(fluxoDeBits, novoIndex);

    return fluxoDeBits;
  }
  
  /********************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoManchesterDiferencial
   * Funcao: monta o fluxo bruto de bits com base na codificacao manchester diferencial
   * Parametros: quadro (int[])
   * Retorno: fluxoDeBits (int[])
   ********************************************************************/
  public int[] camadaFisicaTransmissoraCodificacaoManchesterDiferencial(int[] quadro){
    int[] fluxoDeBits = new int[quadro.length*2];
    int idxQuadro = 0;
    int idxFluxo = 0;
    int deslocamentoQuadro = 31;
    int deslocamentoFluxo = 31;
    int bit;
    int sinalAnterior = 1;
    int proximoChar;

    for(int i = 0; i < quadro.length * 32; i++){
      if(i % 32 == 0 && i != 0){
        idxQuadro++;
        deslocamentoQuadro = 31;
      } 
      if(i % 16 == 0 && i != 0){
        idxFluxo++;
        deslocamentoFluxo = 31;
      }
      if(i % 8 == 0){
        proximoChar = (quadro[idxQuadro] >> (deslocamentoQuadro - 7)) & 255;
        if(proximoChar == 0){
          break;
        }
      }

      bit = (quadro[idxQuadro] & (1 << deslocamentoQuadro)) >> deslocamentoQuadro;
      if(bit == -1){
        bit = 1;
      }
      if(bit == 1){
        fluxoDeBits[idxFluxo] |= (sinalAnterior) << deslocamentoFluxo;
        fluxoDeBits[idxFluxo] |= (1 - sinalAnterior) << deslocamentoFluxo - 1;
        sinalAnterior = 1 - sinalAnterior;
      }
      else{
        fluxoDeBits[idxFluxo] |= (1 - sinalAnterior) << deslocamentoFluxo;
        fluxoDeBits[idxFluxo] |= (sinalAnterior) << deslocamentoFluxo - 1;
      }

      deslocamentoQuadro--;
      deslocamentoFluxo -= 2;
    }

    int novoIndex = 0;
    for (int j = 0; j < fluxoDeBits.length; j++){
      if (!(fluxoDeBits[j] == 0)) {
        fluxoDeBits[novoIndex++] = fluxoDeBits[j];
      }
    }
 
    fluxoDeBits = Arrays.copyOf(fluxoDeBits, novoIndex);
    
    return fluxoDeBits;
  }
}






