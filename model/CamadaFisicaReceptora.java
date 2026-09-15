/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaFisicaReceptora
* Funcao...........: Decodificar os sinais recebidos do meio fisico
*************************************************************** */
package model;

public class CamadaFisicaReceptora {
  /* ***************************************************************
  * Metodo: enviarParaCamadaDeAplicacao
  * Funcao: selecionar a decodificacao e encaminhar o quadro
  * Parametros: fluxoBrutoDeBits = lista de sinais A e B
  * Retorno: void
  *************************************************************** */
  public static void enviarParaCamadaDeAplicacao(char[] fluxoBrutoDeBits) {
    int[] quadro;

    switch (Estado.tipoDeCodificacao) {
      case BINARIA:
        quadro = DecodificacaoBinaria(fluxoBrutoDeBits);
        break;
      case MANCHESTER:
        quadro = DecodificacaoManchester(fluxoBrutoDeBits);
        break;
      default:
        quadro = DecodificacaoManchesterDiferencial(fluxoBrutoDeBits);
    }

    CamadaAplicacaoReceptora.enviarParaAplicacao(quadro);
  }

  /* ***************************************************************
  * Metodo: DecodificacaoBinaria
  * Funcao: reconstruir um int a cada 32 sinais binarios
  * Parametros: sinais = lista binaria formada por A e B
  * Retorno: int[] com o quadro recuperado
  *************************************************************** */
  private static int[] DecodificacaoBinaria(char[] sinais) {
    int[] quadro = new int[sinais.length / 32];

    for (int i = 0; i < quadro.length; i++) {
      for (int j = 0; j < 32; j++) {
        quadro[i] <<= 1;
        quadro[i] |= sinais[j + i * 32] == 'A' ? 1 : 0;
      }
    }
    return quadro;
  }

  /* ***************************************************************
  * Metodo: DecodificacaoManchester
  * Funcao: reconstruir os bits a partir dos pares Manchester
  * Parametros: sinais = lista Manchester formada por A e B
  * Retorno: int[] com o quadro recuperado
  *************************************************************** */
  private static int[] DecodificacaoManchester(char[] sinais) {
    int[] quadro = new int[sinais.length / 64];

    for (int i = 0; i < quadro.length; i++) {
      for (int j = 0; j < 32; j++) {
        quadro[i] <<= 1;
        quadro[i] |= manchesterParaBit(sinais, i, j);
      }
    }
    return quadro;
  }

  /* ***************************************************************
  * Metodo: DecodificacaoManchesterDiferencial
  * Funcao: reconstruir bits pelas mudancas entre pares Manchester
  * Parametros: sinais = lista diferencial formada por A e B
  * Retorno: int[] com o quadro recuperado
  *************************************************************** */
  private static int[] DecodificacaoManchesterDiferencial(char[] sinais) {
    int[] quadro = new int[sinais.length / 64];
    int bitAnterior = 0;

    for (int i = 0; i < quadro.length; i++) {
      for (int j = 0; j < 32; j++) {
        int bit = manchesterParaBit(sinais, i, j);
        quadro[i] <<= 1;
        if (i == 0 && j == 0) {
          quadro[i] |= bit;
          bitAnterior = bit;
        } else {
          if (bit == bitAnterior) {
            quadro[i] |= 0;
          } else {
            quadro[i] |= 1;
            bitAnterior = bit;
          }
        }
      }
    }
    return quadro;
  }

  /* ***************************************************************
  * Metodo: manchesterParaBit
  * Funcao: converter AB em um e qualquer outro par em zero
  * Parametros: sinais = lista recebida, i e j = indices atuais
  * Retorno: int com zero ou um
  *************************************************************** */
  private static int manchesterParaBit(char[] sinais, int i, int j) {
    if (sinais[j * 2 + i * 64] == 'A'
        && sinais[j * 2 + 1 + i * 64] == 'B') {
      return 1;
    } else {
      return 0;
    }
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaReceptora
  * Funcao: manter o nome de metodo definido no framework do trabalho
  * Parametros: quadro = niveis um e zero recebidos do meio
  * Retorno: void
  *************************************************************** */
  public void CamadaFisicaReceptora(int[] quadro) {
    enviarParaCamadaDeAplicacao(niveisParaSinais(quadro));
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaReceptoraDecodificacaoBinaria
  * Funcao: disponibilizar a decodificacao binaria do framework
  * Parametros: quadro = niveis um e zero recebidos
  * Retorno: int[] com o quadro recuperado
  *************************************************************** */
  public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int[] quadro) {
    return DecodificacaoBinaria(niveisParaSinais(quadro));
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaReceptoraDecodificacaoManchester
  * Funcao: disponibilizar a decodificacao Manchester do framework
  * Parametros: quadro = niveis um e zero recebidos
  * Retorno: int[] com o quadro recuperado
  *************************************************************** */
  public int[] CamadaFisicaReceptoraDecodificacaoManchester(int[] quadro) {
    return DecodificacaoManchester(niveisParaSinais(quadro));
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaReceptoraDecodificacaoManchesterDiferencial
  * Funcao: disponibilizar a decodificacao diferencial do framework
  * Parametros: quadro = niveis um e zero recebidos
  * Retorno: int[] com o quadro recuperado
  *************************************************************** */
  public int[] CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(int[] quadro) {
    return DecodificacaoManchesterDiferencial(niveisParaSinais(quadro));
  }

  /* ***************************************************************
  * Metodo: niveisParaSinais
  * Funcao: adaptar niveis numericos para a lista interna A e B
  * Parametros: niveis = vetor formado por zero e um
  * Retorno: char[] com os sinais equivalentes
  *************************************************************** */
  private static char[] niveisParaSinais(int[] niveis) {
    char[] sinais = new char[niveis.length];
    for (int i = 0; i < niveis.length; i++) sinais[i] = niveis[i] == 1 ? 'A' : 'B';
    return sinais;
  }
}
