/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaFisicaTransmissora
* Funcao...........: Aplicar a codificacao fisica selecionada
*************************************************************** */
package model;
import controller.ControladorPrincipal;
public class CamadaFisicaTransmissora {
  private static final int SINAL_A_BAIXO = 0;
  private static final int SINAL_B_ALTO = 1;
  private static final int[] LISTA_AB = {SINAL_A_BAIXO, SINAL_B_ALTO};

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
      // Na lista AB, A representa nivel baixo e B representa nivel alto.
      int primeiro = bit == 0 ? LISTA_AB[0] : LISTA_AB[1];
      int segundo = primeiro == LISTA_AB[0] ? LISTA_AB[1] : LISTA_AB[0];
      fluxo[2 * i] = primeiro;
      fluxo[2 * i + 1] = segundo;
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
    int nivelAnterior = LISTA_AB[1];
    for (int i = 0; i < quadro.length; i++) {
      int bit = quadro[i];
      if (bit != 0 && bit != 1) throw new IllegalArgumentException("Bit invalido.");
      // O bit 0 troca A por B ou B por A no inicio do intervalo.
      int sinalInvertido = nivelAnterior == LISTA_AB[0] ? LISTA_AB[1] : LISTA_AB[0];
      int primeiro = bit == 0 ? sinalInvertido : nivelAnterior;
      int segundo = primeiro == LISTA_AB[0] ? LISTA_AB[1] : LISTA_AB[0];
      fluxo[2 * i] = primeiro;
      fluxo[2 * i + 1] = segundo;
      nivelAnterior = segundo;
    }
    return fluxo;
  }
}
