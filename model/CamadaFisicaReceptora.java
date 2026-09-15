/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 05/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaFisicaReceptora
* Funcao...........: Decodificar os niveis recebidos do meio fisico
*************************************************************** */
package model;
import controller.ControladorPrincipal;
public class CamadaFisicaReceptora {
  private static final char SINAL_A_ALTO = 'A';
  private static final char SINAL_B_BAIXO = 'B';
  private static final char[] LISTA_AB = {SINAL_A_ALTO, SINAL_B_BAIXO};

  /* ***************************************************************
  * Metodo: CamadaFisicaReceptora
  * Funcao: selecionar a decodificacao e encaminhar o quadro
  * Parametros: quadro = niveis recebidos do meio de comunicacao
  * Retorno: void
  *************************************************************** */
  public void CamadaFisicaReceptora(int[] quadro) {
    int tipoDeDecodificacao = ControladorPrincipal.obterCodificacaoAtiva();
    int[] fluxoBrutoDeBits;
    switch (tipoDeDecodificacao) {
      case 0: fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoBinaria(quadro); break;
      case 1: fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchester(quadro); break;
      case 2: fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadro); break;
      default: throw new IllegalArgumentException("Codificacao invalida.");
    }
    ControladorPrincipal.camadaAplicacaoReceptora.CamadaDeAplicacaoReceptora(fluxoBrutoDeBits);
  }
  /* ***************************************************************
  * Metodo: CamadaFisicaReceptoraDecodificacaoBinaria
  * Funcao: recuperar o quadro binario sem transformacao
  * Parametros: quadro = vetor binario recebido
  * Retorno: int[] com os bits originais
  *************************************************************** */
  public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int[] quadro) {
    return quadro;
  }

  /* ***************************************************************
  * Metodo: CamadaFisicaReceptoraDecodificacaoManchester
  * Funcao: validar os pares Manchester e recuperar cada bit
  * Parametros: quadro = niveis Manchester recebidos
  * Retorno: int[] com os bits decodificados
  *************************************************************** */
  public int[] CamadaFisicaReceptoraDecodificacaoManchester(int[] quadro) {
    if (quadro.length % 2 != 0) throw new IllegalArgumentException("Par incompleto.");
    char[] sinais = converterNiveisParaListaAB(quadro);
    int[] fluxo = new int[quadro.length / 2];
    for (int i = 0; i < fluxo.length; i++) {
      char primeiro = sinais[2 * i], segundo = sinais[2 * i + 1];
      boolean parAB = primeiro == LISTA_AB[0] && segundo == LISTA_AB[1];
      boolean parBA = primeiro == LISTA_AB[1] && segundo == LISTA_AB[0];
      if (!parAB && !parBA) throw new IllegalArgumentException("Transicao Manchester invalida.");
      fluxo[i] = parAB ? 1 : 0;
    }
    return fluxo;
  }
  /* ***************************************************************
  * Metodo: CamadaFisicaReceptoraDecodificacaoManchesterDiferencial
  * Funcao: interpretar as transicoes e recuperar cada bit
  * Parametros: quadro = niveis Manchester Diferencial recebidos
  * Retorno: int[] com os bits decodificados
  *************************************************************** */
  public int[] CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(int[] quadro) {
    if (quadro.length % 2 != 0) throw new IllegalArgumentException("Par incompleto.");
    char[] sinais = converterNiveisParaListaAB(quadro);
    int[] fluxo = new int[quadro.length / 2];
    char nivelAnterior = LISTA_AB[0];
    for (int i = 0; i < fluxo.length; i++) {
      char primeiro = sinais[2 * i], segundo = sinais[2 * i + 1];
      boolean parAB = primeiro == LISTA_AB[0] && segundo == LISTA_AB[1];
      boolean parBA = primeiro == LISTA_AB[1] && segundo == LISTA_AB[0];
      if (!parAB && !parBA) throw new IllegalArgumentException("Transicao Manchester invalida.");
      fluxo[i] = primeiro == nivelAnterior ? 1 : 0;
      nivelAnterior = segundo;
    }
    return fluxo;
  }

  /* ***************************************************************
  * Metodo: converterNiveisParaListaAB
  * Funcao: converter os niveis um e zero nos caracteres A e B
  * Parametros: niveis = sinais recebidos do meio de comunicacao
  * Retorno: char[] com a lista interna formada por A e B
  *************************************************************** */
  private char[] converterNiveisParaListaAB(int[] niveis) {
    char[] sinais = new char[niveis.length];
    for (int i = 0; i < niveis.length; i++) {
      if (niveis[i] == 1) sinais[i] = SINAL_A_ALTO;
      else if (niveis[i] == 0) sinais[i] = SINAL_B_BAIXO;
      else throw new IllegalArgumentException("Nivel invalido.");
    }
    return sinais;
  }
}
