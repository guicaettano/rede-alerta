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
  private static final char SINAL_A_ALTO = 'A';
  private static final char SINAL_B_BAIXO = 'B';
  private static final char[] LISTA_AB = {SINAL_A_ALTO, SINAL_B_BAIXO};

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
    char[] sinais = new char[quadro.length * 2];
    for (int i = 0; i < quadro.length; i++) {
      int bit = quadro[i];
      if (bit != 0 && bit != 1) throw new IllegalArgumentException("Bit invalido.");
      // A representa nivel alto e B representa nivel baixo.
      char primeiro = bit == 1 ? LISTA_AB[0] : LISTA_AB[1];
      char segundo = primeiro == LISTA_AB[0] ? LISTA_AB[1] : LISTA_AB[0];
      sinais[2 * i] = primeiro;
      sinais[2 * i + 1] = segundo;
    }
    return converterListaABParaNiveis(sinais);
  }
  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissoraCodificacaoManchesterDiferencial
  * Funcao: codificar os bits em Manchester Diferencial
  * Parametros: quadro = vetor de bits a ser codificado
  * Retorno: int[] com os niveis codificados
  *************************************************************** */
  public int[] CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(int[] quadro) {
    char[] sinais = new char[quadro.length * 2];
    char nivelAnterior = LISTA_AB[0];
    for (int i = 0; i < quadro.length; i++) {
      int bit = quadro[i];
      if (bit != 0 && bit != 1) throw new IllegalArgumentException("Bit invalido.");
      // O bit 0 troca A por B ou B por A no inicio do intervalo.
      char sinalInvertido = nivelAnterior == LISTA_AB[0] ? LISTA_AB[1] : LISTA_AB[0];
      char primeiro = bit == 0 ? sinalInvertido : nivelAnterior;
      char segundo = primeiro == LISTA_AB[0] ? LISTA_AB[1] : LISTA_AB[0];
      sinais[2 * i] = primeiro;
      sinais[2 * i + 1] = segundo;
      nivelAnterior = segundo;
    }
    return converterListaABParaNiveis(sinais);
  }

  /* ***************************************************************
  * Metodo: converterListaABParaNiveis
  * Funcao: converter os caracteres A e B nos niveis um e zero
  * Parametros: sinais = lista interna formada por A e B
  * Retorno: int[] com os niveis usados pelo meio de comunicacao
  *************************************************************** */
  private int[] converterListaABParaNiveis(char[] sinais) {
    int[] niveis = new int[sinais.length];
    for (int i = 0; i < sinais.length; i++) {
      if (sinais[i] == SINAL_A_ALTO) niveis[i] = 1;
      else if (sinais[i] == SINAL_B_BAIXO) niveis[i] = 0;
      else throw new IllegalArgumentException("Sinal AB invalido.");
    }
    return niveis;
  }
}
