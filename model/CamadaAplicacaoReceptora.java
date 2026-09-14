package model;
import controller.ControladorPrincipal;
/** Conversao inversa, seguida da chamada de aplicacao do slide 10. */
public class CamadaAplicacaoReceptora {
  public void CamadaDeAplicacaoReceptora(int[] quadro) {
    if (quadro.length % 16 != 0) throw new IllegalArgumentException("Quadro incompleto.");
    StringBuilder mensagem = new StringBuilder();
    for (int inicio = 0; inicio < quadro.length; inicio += 16) {
      int caractere = 0;
      for (int bit = 0; bit < 16; bit++) {
        if (quadro[inicio + bit] != 0 && quadro[inicio + bit] != 1)
          throw new IllegalArgumentException("Bit invalido.");
        caractere = (caractere << 1) | quadro[inicio + bit];
      }
      mensagem.append((char) caractere);
    }
    ControladorPrincipal.aplicacaoReceptora.AplicacaoReceptora(mensagem.toString());
  }
}
