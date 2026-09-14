package testes;

import java.util.Arrays;
import java.util.Random;
import controller.ControladorPrincipal;
import model.*;

/** Testes sem janela: conversao da aplicacao e tres codificacoes. */
public class TesteCamadas {
  private static int[] bits;
  private static String recebida;
  private static void exigir(boolean condicao) {
    if (!condicao) throw new AssertionError("Resultado incorreto");
  }
  private static void rejeitar(Runnable acao) {
    try { acao.run(); } catch (IllegalArgumentException esperado) { return; }
    throw new AssertionError("Entrada invalida aceita");
  }
  public static void main(String[] argumentos) {
    ControladorPrincipal.camadaFisicaTransmissora = new CamadaFisicaTransmissora() {
      @Override public void CamadaFisicaTransmissora(int[] quadro) { bits = quadro; }
    };
    ControladorPrincipal.aplicacaoReceptora = new AplicacaoReceptora(null) {
      @Override public void AplicacaoReceptora(String mensagem) { recebida = mensagem; }
    };
    CamadaAplicacaoTransmissora aplicacao = new CamadaAplicacaoTransmissora();
    CamadaAplicacaoReceptora destino = new CamadaAplicacaoReceptora();
    CamadaFisicaTransmissora transmissor = new CamadaFisicaTransmissora();
    CamadaFisicaReceptora receptor = new CamadaFisicaReceptora();
    Random aleatorio = new Random(42);
    for (int caso = 0; caso < 1005; caso++) {
      String mensagem;
      if (caso == 0) mensagem = "";
      else if (caso == 1) mensagem = "REDE ALERTA";
      else if (caso == 2) mensagem = "CAMADA FISICA";
      else if (caso == 3) mensagem = "01010101";
      else if (caso == 4) mensagem = "ALERTA";
      else {
        StringBuilder texto = new StringBuilder();
        for (int i = aleatorio.nextInt(181); i > 0; i--)
          texto.append((char) ('A' + aleatorio.nextInt(26)));
        mensagem = texto.toString();
      }
      aplicacao.CamadaDeAplicacaoTransmissora(mensagem);
      exigir(bits.length == mensagem.length() * 16);
      exigir(transmissor.CamadaFisicaTransmissoraCodificacaoBinaria(bits) == bits);
      int[][] quadros = {
        receptor.CamadaFisicaReceptoraDecodificacaoBinaria(bits),
        receptor.CamadaFisicaReceptoraDecodificacaoManchester(transmissor.CamadaFisicaTransmissoraCodificacaoManchester(bits)),
        receptor.CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(transmissor.CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(bits))
      };
      for (int[] quadro : quadros) {
        exigir(Arrays.equals(bits, quadro));
        destino.CamadaDeAplicacaoReceptora(quadro);
        exigir(mensagem.equals(recebida));
      }
    }
    exigir(Arrays.equals(transmissor.CamadaFisicaTransmissoraCodificacaoManchester(new int[]{0,1}), new int[]{0,1,1,0}));
    exigir(Arrays.equals(transmissor.CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(new int[]{0,1,0}), new int[]{0,1,1,0,1,0}));
    rejeitar(() -> receptor.CamadaFisicaReceptoraDecodificacaoManchester(new int[]{0}));
    rejeitar(() -> receptor.CamadaFisicaReceptoraDecodificacaoManchester(new int[]{1,1}));
    rejeitar(() -> receptor.CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(new int[]{0,0}));
    rejeitar(() -> destino.CamadaDeAplicacaoReceptora(new int[]{1}));
    System.out.println("OK: 3015 round-trips, vetores conhecidos e entradas invalidas.");
  }
}
