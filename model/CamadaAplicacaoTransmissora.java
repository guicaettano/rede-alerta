package model;
import controller.ControladorPrincipal;
/** Conversao deixada em aberto no slide 4; cada elemento armazena um bit. */
public class CamadaAplicacaoTransmissora {
  public void CamadaDeAplicacaoTransmissora(String mensagem) {
    int[] quadro = new int[mensagem.length() * 16];
    // Cada caractere Java ocupa 16 bits; cada posicao do vetor guarda um bit.
    for (int caractere = 0; caractere < mensagem.length(); caractere++) {
      for (int bit = 0; bit < 16; bit++) {
        quadro[caractere * 16 + bit] = (mensagem.charAt(caractere) >>> (15 - bit)) & 1;
      }
    }
    ControladorPrincipal.camadaFisicaTransmissora.CamadaFisicaTransmissora(quadro);
  }
}
