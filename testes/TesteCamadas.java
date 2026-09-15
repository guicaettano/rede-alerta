/* ***************************************************************
* Autor............: Guilherme Caetano dos Santos da Mata
* Matricula........: 202510517
* Inicio...........: 14/09/2026
* Ultima alteracao.: 15/09/2026
* Nome.............: TesteCamadas
* Funcao...........: Validar conversoes e codificacoes sem abrir a GUI
*************************************************************** */
package testes;

import java.util.Arrays;
import java.util.Random;
import controller.ControladorPrincipal;
import model.*;

public class TesteCamadas {
  private static int[] bits;
  private static String recebida;

  /* ***************************************************************
  * Metodo: exigir
  * Funcao: interromper o teste quando uma condicao nao for atendida
  * Parametros: condicao = resultado logico que deve ser verdadeiro
  * Retorno: void
  *************************************************************** */
  private static void exigir(boolean condicao) {
    if (!condicao) throw new AssertionError("Resultado incorreto");
  }
  /* ***************************************************************
  * Metodo: rejeitar
  * Funcao: verificar se uma entrada invalida produz a excecao esperada
  * Parametros: acao = rotina que deve rejeitar a entrada
  * Retorno: void
  *************************************************************** */
  private static void rejeitar(Runnable acao) {
    try { acao.run(); } catch (IllegalArgumentException esperado) { return; }
    throw new AssertionError("Entrada invalida aceita");
  }
  /* ***************************************************************
  * Metodo: main
  * Funcao: executar todos os testes das camadas e codificacoes
  * Parametros: argumentos = argumentos recebidos pela linha de comando
  * Retorno: void
  *************************************************************** */
  public static void main(String[] argumentos) {
    ControladorPrincipal.camadaFisicaTransmissora = new CamadaFisicaTransmissora() {
      /* Captura o quadro produzido pela camada de aplicacao. */
      @Override public void CamadaFisicaTransmissora(int[] quadro) { bits = quadro; }
    };
    ControladorPrincipal.aplicacaoReceptora = new AplicacaoReceptora(null) {
      /* Captura a mensagem reconstruida pela camada receptora. */
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
