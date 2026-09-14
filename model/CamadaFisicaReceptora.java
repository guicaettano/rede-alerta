package model;
import controller.ControladorPrincipal;
/** Despacho e sub-rotinas dos slides 8 e 9. */
public class CamadaFisicaReceptora {
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
  public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int[] quadro) { return quadro; }
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
