/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 14/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaFisicaTransmissora
* Funcao...........: Aplicar a codificacao fisica selecionada
*************************************************************** */
package model;
import controller.ControladorPrincipal;
public class CamadaFisicaTransmissora {
  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissora
  * Funcao: selecionar a codificacao e enviar os bits ao meio
  * Parametros: quadro = bits produzidos pela camada de aplicacao
  * Retorno: void
  *************************************************************** */
  public void CamadaFisicaTransmissora(int[] quadro) {
    int tipoDeCodificacao = ControladorPrincipal.obterCodificacaoAtiva();
    int[] fluxoBrutoDeBits;
    switch (tipoDeCodificacao) {
      case 0: fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoBinaria(quadro); break;
      case 1: fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchester(quadro); break;
      case 2: fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro); break;
      default: throw new IllegalArgumentException("Codificacao invalida.");
    }
    ControladorPrincipal.registrarQuantidadeBits(quadro, fluxoBrutoDeBits);
    ControladorPrincipal.meioDeComunicacao.MeioDeComunicacao(fluxoBrutoDeBits);
  }
  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissoraCodificacaoBinaria
  * Funcao: manter o quadro na codificacao binaria original
  * Parametros: quadro = vetor de bits a ser transmitido
  * Retorno: int[] com os bits sem alteracao
  *************************************************************** */
  public int[] CamadaFisicaTransmissoraCodificacaoBinaria(int[] quadro) {
    return quadro;
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissoraCodificacaoManchester
  * Funcao: codificar cada bit em dois niveis Manchester
  * Parametros: quadro = vetor de bits a ser codificado
  * Retorno: int[] com os niveis codificados
  *************************************************************** */
  public int[] CamadaFisicaTransmissoraCodificacaoManchester(int[] quadro) {
    int[] fluxo = new int[quadro.length * 2];
    for (int i = 0; i < quadro.length; i++) {
      int bit = quadro[i];
      if (bit != 0 && bit != 1) throw new IllegalArgumentException("Bit invalido.");
      // Manchester representa 0 por 01 e 1 por 10.
      int primeiro = bit;
      fluxo[2 * i] = primeiro;
      fluxo[2 * i + 1] = 1 - primeiro;
    }
    return fluxo;
  }
  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissoraCodificacaoManchesterDiferencial
  * Funcao: codificar os bits em Manchester Diferencial
  * Parametros: quadro = vetor de bits a ser codificado
  * Retorno: int[] com os niveis codificados
  *************************************************************** */
  public int[] CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(int[] quadro) {
    int[] fluxo = new int[quadro.length * 2];
    int nivelAnterior = 1;
    for (int i = 0; i < quadro.length; i++) {
      int bit = quadro[i];
      if (bit != 0 && bit != 1) throw new IllegalArgumentException("Bit invalido.");
      // O bit 0 provoca transicao no inicio; sempre ha transicao no meio.
      int primeiro = bit == 0 ? 1 - nivelAnterior : nivelAnterior;
      fluxo[2 * i] = primeiro;
      fluxo[2 * i + 1] = 1 - primeiro;
      nivelAnterior = 1 - primeiro;
    }
    return fluxo;
  }
}
