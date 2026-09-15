/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 14/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: CamadaFisicaReceptora
* Funcao...........: Decodificar os niveis recebidos do meio fisico
*************************************************************** */
package model;
import controller.ControladorPrincipal;
public class CamadaFisicaReceptora {
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
    int[] fluxo = new int[quadro.length / 2];
    for (int i = 0; i < fluxo.length; i++) {
      int primeiro = quadro[2 * i], segundo = quadro[2 * i + 1];
      if ((primeiro != 0 && primeiro != 1) || segundo != 1 - primeiro)
        throw new IllegalArgumentException("Transicao Manchester invalida.");
      fluxo[i] = primeiro;
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
    int[] fluxo = new int[quadro.length / 2];
    int nivelAnterior = 1;
    for (int i = 0; i < fluxo.length; i++) {
      int primeiro = quadro[2 * i], segundo = quadro[2 * i + 1];
      if ((primeiro != 0 && primeiro != 1) || segundo != 1 - primeiro)
        throw new IllegalArgumentException("Transicao Manchester invalida.");
      fluxo[i] = primeiro == nivelAnterior ? 1 : 0;
      nivelAnterior = segundo;
    }
    return fluxo;
  }
}
