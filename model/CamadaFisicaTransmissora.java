package model;
import controller.ControladorPrincipal;
/** Despacho e sub-rotinas dos slides 5 e 6. */
public class CamadaFisicaTransmissora {
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
  public int[] CamadaFisicaTransmissoraCodificacaoBinaria(int[] quadro) { return quadro; }
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
