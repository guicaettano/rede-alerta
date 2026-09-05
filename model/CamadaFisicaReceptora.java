/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: CamadaFisicaReceptora.java
* Funcao: Simular o funcionamento da camada fisica receptora
********************************************************************/

package model;

import control.ControllerPrincipal;

import java.lang.Math;

public class CamadaFisicaReceptora {
  private ControllerPrincipal cP = new ControllerPrincipal();

  public CamadaFisicaReceptora(ControllerPrincipal cP){
    this.cP = cP;
  }

   /********************************************************************
   * Metodo: camadaFisicaReceptora
   * Funcao: decodifica a mensagem e monta o quadro com base no fluxo bruto
   * de bits recebido
   * Parametros: fluxoBrutoDeBits (int[])
   * Retorno: 
   ********************************************************************/
  public void camadaFisicaReceptora(int[] fluxoBrutoDeBits){
    int codificacao = cP.getCodificacao();
		int[] quadro = new int[codificacao == 0 ? fluxoBrutoDeBits.length : (int)Math.ceil(fluxoBrutoDeBits.length/2f)];
		switch (codificacao) {
			case 0: 
				quadro = camadaFisicaReceptoraDecodificacaoBinaria(fluxoBrutoDeBits);
				break;
			case 1: 
				quadro = camadaFisicaReceptoraDecodificacaoManchester(fluxoBrutoDeBits);
				break;
			case 2: 
				quadro = camadaFisicaReceptoraDecodificacaoManchesterDiferencial(fluxoBrutoDeBits);
				break;
		}

    ControllerPrincipal.cAR.camadaAplicacaoReceptora(quadro);
  }

  public int[] camadaFisicaReceptoraDecodificacaoBinaria(int[] fluxoBrutoDeBits){
    return fluxoBrutoDeBits;
  }

  public int[] camadaFisicaReceptoraDecodificacaoManchester(int[] fluxoBrutoDeBits){
    int[] quadroDecodificado = new int[(int)Math.ceil(fluxoBrutoDeBits.length/2f)];
    int idxQuadro = 0;
    int idxFluxo = 0;
    int deslocamentoQuadro = 31;
    int deslocamentoFluxo = 31;
    int bit;
    int proximoChar;

    for(int i = 0; i < (fluxoBrutoDeBits.length * 32)/2; i++){
      if(i % 32 == 0 && i != 0){
        idxQuadro++;
        deslocamentoQuadro = 31;
      } 
      if(i % 16 == 0 && i != 0){
        idxFluxo++;
        deslocamentoFluxo = 31;
      }
      if(i % 8 == 0){
        proximoChar = (fluxoBrutoDeBits[idxFluxo] >> (deslocamentoFluxo - 7)) & 255;
        if(proximoChar == 0){
          break;
        }
      }
      
      bit = (fluxoBrutoDeBits[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo;
      if(bit == -1){
        bit = 1;
      }
      quadroDecodificado[idxQuadro] |= bit << deslocamentoQuadro;
      deslocamentoQuadro--;
      deslocamentoFluxo -= 2;
    }

    return quadroDecodificado;
  }

  public int[] camadaFisicaReceptoraDecodificacaoManchesterDiferencial(int[] fluxoBrutoDeBits){
    int[] quadroDecodificado = new int[(int)Math.ceil(fluxoBrutoDeBits.length/2f)];
    int idxQuadro = 0;
    int idxFluxo = 0;
    int deslocamentoQuadro = 31;
    int deslocamentoFluxo = 31;
    int bit;
    int sinalAnterior = 0;
    int proximoChar;

    for(int i = 0; i < fluxoBrutoDeBits.length * 16; i++){
      if(i % 32 == 0 && i != 0){
        idxQuadro++;
        deslocamentoQuadro = 31;
      } 
      if(i % 16 == 0 && i != 0){
        idxFluxo++;
        deslocamentoFluxo = 31;
      }
      if(i % 8 == 0 && i != 0){
        proximoChar = (fluxoBrutoDeBits[idxFluxo] >> (deslocamentoFluxo - 7)) & 255;
        if(proximoChar == 0){
          break;
        }
      }
      
      bit = (fluxoBrutoDeBits[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo;
      if(bit == -1){
        bit = 1;
      }
      if(bit != sinalAnterior){
        quadroDecodificado[idxQuadro] |= 1 << deslocamentoQuadro;
        sinalAnterior = 1 - sinalAnterior;
      }
      deslocamentoQuadro--;
      deslocamentoFluxo -= 2;
    }

    return quadroDecodificado;  
  }
}
