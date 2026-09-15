/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaFisicaTransmissora
* Funcao...........: Aplicar a codificacao fisica selecionada
*************************************************************** */
package model;

public class CamadaFisicaTransmissora {
  /* ***************************************************************
  * Metodo: enviarPelaRede
  * Funcao: selecionar a codificacao e enviar os sinais ao meio
  * Parametros: quadro = inteiros produzidos pela camada de aplicacao
  * Retorno: void
  *************************************************************** */
  public static void enviarPelaRede(int[] quadro) {
    char[] fluxoBrutoDeBits;

    switch (Estado.tipoDeCodificacao) {
      case BINARIA:
        fluxoBrutoDeBits = codificacaoBinaria(quadro);
        break;
      case MANCHESTER:
        fluxoBrutoDeBits = codificacaoManchester(quadro);
        break;
      default:
        fluxoBrutoDeBits = codificacaoManchesterDiferencial(quadro);
    }

    MeioDeComunicacao.transportar(fluxoBrutoDeBits);
  }

  /* ***************************************************************
  * Metodo: codificacaoBinaria
  * Funcao: representar cada bit por um sinal A ou B
  * Parametros: quadro = inteiros com os bits da mensagem
  * Retorno: char[] com a lista de sinais A e B
  *************************************************************** */
  private static char[] codificacaoBinaria(int[] quadro) {
    char[] sinais = new char[quadro.length * 32];

    for (int i = 0; i < quadro.length; i++) {
      for (int j = 0; j < 32; j++) {
        sinais[j + i * 32] = lerBit(quadro, i) == 1 ? 'A' : 'B';
      }
    }
    return sinais;
  }

  /* ***************************************************************
  * Metodo: codificacaoManchester
  * Funcao: converter cada bit em um par Manchester
  * Parametros: quadro = inteiros com os bits da mensagem
  * Retorno: char[] com a lista de sinais A e B
  *************************************************************** */
  private static char[] codificacaoManchester(int[] quadro) {
    char[] sinais = new char[quadro.length * 64];

    for (int i = 0; i < quadro.length; i++) {
      for (int j = 0; j < 32; j++) {
        bitParaManchester(lerBit(quadro, i), sinais, i, j);
      }
    }
    return sinais;
  }

  /* ***************************************************************
  * Metodo: codificacaoManchesterDiferencial
  * Funcao: codificar mudancas de bit com Manchester Diferencial
  * Parametros: quadro = inteiros com os bits da mensagem
  * Retorno: char[] com a lista de sinais A e B
  *************************************************************** */
  private static char[] codificacaoManchesterDiferencial(int[] quadro) {
    char[] sinais = new char[quadro.length * 64];
    int bitAnterior = 0;

    for (int i = 0; i < quadro.length; i++) {
      for (int j = 0; j < 32; j++) {
        int bit = lerBit(quadro, i);

        if (i == 0 && j == 0) {
          bitParaManchester(bit, sinais, i, j);
          bitAnterior = bit;
        } else {
          if (bit == 1) {
            bitAnterior = 1 - bitAnterior;
            bitParaManchester(bitAnterior, sinais, i, j);
          } else {
            bitParaManchester(bitAnterior, sinais, i, j);
          }
        }
      }
    }
    return sinais;
  }

  /* ***************************************************************
  * Metodo: lerBit
  * Funcao: ler o bit mais significativo e deslocar o inteiro
  * Parametros: quadro = vetor lido, indiceContainer = int atual
  * Retorno: int com zero ou um
  *************************************************************** */
  private static int lerBit(int[] quadro, int indiceContainer) {
    int mascara = 1 << 31;
    int bit = (quadro[indiceContainer] & mascara) >>> 31;
    quadro[indiceContainer] <<= 1;
    return bit;
  }

  /* ***************************************************************
  * Metodo: bitParaManchester
  * Funcao: gravar AB para um e BA para zero
  * Parametros: bit = valor, sinais = destino, i e j = indices atuais
  * Retorno: void
  *************************************************************** */
  private static void bitParaManchester(int bit, char[] sinais, int i, int j) {
    if (bit == 1) {
      sinais[j * 2 + i * 64] = 'A';
      sinais[j * 2 + 1 + i * 64] = 'B';
    } else {
      sinais[j * 2 + i * 64] = 'B';
      sinais[j * 2 + 1 + i * 64] = 'A';
    }
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissora
  * Funcao: manter o nome de metodo definido no framework do trabalho
  * Parametros: quadro = inteiros produzidos pela camada de aplicacao
  * Retorno: void
  *************************************************************** */
  public void CamadaFisicaTransmissora(int[] quadro) {
    enviarPelaRede(quadro);
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissoraCodificacaoBinaria
  * Funcao: disponibilizar a codificacao binaria exigida no framework
  * Parametros: quadro = inteiros com os bits da mensagem
  * Retorno: int[] com A representado por um e B por zero
  *************************************************************** */
  public int[] CamadaFisicaTransmissoraCodificacaoBinaria(int[] quadro) {
    return sinaisParaNiveis(codificacaoBinaria(quadro));
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissoraCodificacaoManchester
  * Funcao: disponibilizar a codificacao Manchester do framework
  * Parametros: quadro = inteiros com os bits da mensagem
  * Retorno: int[] com A representado por um e B por zero
  *************************************************************** */
  public int[] CamadaFisicaTransmissoraCodificacaoManchester(int[] quadro) {
    return sinaisParaNiveis(codificacaoManchester(quadro));
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaTransmissoraCodificacaoManchesterDiferencial
  * Funcao: disponibilizar a codificacao diferencial do framework
  * Parametros: quadro = inteiros com os bits da mensagem
  * Retorno: int[] com A representado por um e B por zero
  *************************************************************** */
  public int[] CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(int[] quadro) {
    return sinaisParaNiveis(codificacaoManchesterDiferencial(quadro));
  }

  /* ***************************************************************
  * Metodo: sinaisParaNiveis
  * Funcao: adaptar os sinais para os metodos publicos do framework
  * Parametros: sinais = lista interna formada por A e B
  * Retorno: int[] com niveis um e zero
  *************************************************************** */
  private static int[] sinaisParaNiveis(char[] sinais) {
    int[] niveis = new int[sinais.length];
    for (int i = 0; i < sinais.length; i++) niveis[i] = sinais[i] == 'A' ? 1 : 0;
    return niveis;
  }
}
